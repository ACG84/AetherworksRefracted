# Porting Aetherworks Refracted to Embers Re-Ignited (1.21.1 / NeoForge)

This branch retargets the mod from **Minecraft 1.20.1 + Forge 47 + Embers Rekindled 1.4.7**
to **Minecraft 1.21.1 + NeoForge 21.1.228 + [Embers Re-Ignited](https://github.com/Riieno/EmbersRekindled-Reignited) 1.5.7**.

The port is **incomplete**: the build system and the majority of the codebase are converted,
but the mod does not compile yet. This file records what has been done, the decisions taken,
and exactly what is left.

## Status

At the last run, 90 of 164 compiled source files are clean and 74 still have errors
(252 javac errors, down from 1010 at the start of the port).

To see the current state:

```
./gradlew compileJava
```

## Dependency

Embers Re-Ignited is resolved from the Modrinth maven. `8znVCTxA` is its Modrinth project id:

```groovy
implementation "maven.modrinth:8znVCTxA:${embers_version}"   # 1.21.1-1.5.7
```

## What was converted

| Area | Change |
|---|---|
| Build | ForgeGradle → ModDevGradle (`net.neoforged.moddev`), Java 17 → 21, Parchment 2024.11.17 |
| Metadata | `mods.toml` → `neoforge.mods.toml`, pack format 15 → 34 |
| Registries | `RegistryObject` → `DeferredHolder`/`DeferredItem`/`DeferredBlock`; `ForgeRegistries` → `BuiltInRegistries`/`Registries`/`NeoForgeRegistries` |
| Entrypoint | Mod bus and `ModContainer` are constructor parameters; `MinecraftForge` → `NeoForge`; `@Mod.EventBusSubscriber` → `@EventBusSubscriber` |
| Config | Registered against the mod's own `ModContainer`; `ForgeConfigSpec` → `ModConfigSpec` |
| Networking | `SimpleChannel` → `CustomPacketPayload` + `StreamCodec` + `RegisterPayloadHandlersEvent`; `PacketDistributor` call style updated |
| Item data | New `AWDataComponents` replaces stack NBT (toggle mode, focus, crown gem, lexicon item/amount, potion colour, prismarine shovel fluid) |
| Potions | `PotionUtils` → the `POTION_CONTENTS` data component; effects are `Holder<MobEffect>` throughout |
| Capabilities | Chunk data → data attachments (`AWAttachments`); block entity capabilities bridged onto NeoForge's system in `AWCapabilities`, mirroring Embers Re-Ignited's own legacy bridge |
| Block entities | 1.21 `loadAdditional`/`saveAdditional`/`getUpdateTag` signatures; `invalidateCaps` → `invalidateCapabilities` |
| GUI / models | `IGuiOverlay` → `LayeredDraw.Layer`; manual model baking → `ModelEvent.RegisterAdditional`; 1.21 `BufferBuilder` API |
| Tools | `ToolAction` → `ItemAbility`; `ForgeTier` → `SimpleTier`; `getBlockReach` → `blockInteractionRange`; `hurt` → `hurtAndBreak` |
| Misc | `new ResourceLocation(..)` → `fromNamespaceAndPath`/`parse` (129 sites); `BlockPathTypes` → `PathType`; `Properties.copy` → `ofFullCopy` |

### Access transformers

None are needed any more. The two things the mod used to reach into (`ModelBakery.ModelBakerImpl`
and `EntityRenderDispatcher.renderers`) are both reachable through public 1.21 APIs, so
`accesstransformer.cfg` is now empty apart from a comment.

## Decisions worth reviewing

1. **Recipe datagen is excluded from compilation.** Embers Re-Ignited excludes its own recipe
   datagen from its published jar (`EmbersRecipes`, `AlchemyRecipeBuilder`,
   `ConsumerWrapperBuilder`, `GenericRecipeBuilder`, `*RecipeBuilder` — all verified absent from
   the jar). `AWRecipes` and the Aetherworks `*RecipeBuilder` classes are written against them,
   so they are excluded in `build.gradle` for the same reason. **The recipes themselves are
   unaffected** — the generated JSON under `src/generated/resources` ships as before. Only
   *regenerating* them is unavailable until Embers restores that API.

2. **The Aetheric enchantment is now data-driven.** 1.21 removed enchantment subclassing.
   The registration was already commented out upstream ("aetherium items will just self-repair"),
   so `AethericEnchantment` is now a `ResourceKey<Enchantment>` plus an optional lookup, and
   `AethericEnchantmentHelper` no-ops when no datapack defines it. No new content was invented.

3. **Aetherium tier.** `TierSortingRegistry` is gone; a tier is described by the tag of blocks it
   *cannot* harvest. Aetherium now uses `BlockTags.INCORRECT_FOR_NETHERITE_TOOL`, matching how
   Embers Re-Ignited treats its own clockwork tools, so it still mines everything it used to.

4. **A latent bug was fixed incidentally.** `MessageFluidSync.decode` read `buf.capacity()`
   instead of the encoded int. The rewrite to `StreamCodec.composite` makes this correct.

## What is left

The remaining errors are concentrated in areas that need real API work rather than mechanical
renaming. Roughly in priority order:

- **`item/tool/AetherCrossbow*` (~36 errors).** `CrossbowItem` was reworked in 1.21: charging
  moved to the `CHARGED_PROJECTILES` component, `getChargeDuration`/`getUseDuration` changed
  signature, and enchantment levels are read from `Holder<Enchantment>` resolved via the registry
  rather than `Enchantments.X` constants.
- **`recipe/MetalFormerRecipe`, `AetheriumAnvilRecipe`, `ToolStationRecipe` (~41 errors).** These
  three carry real data, so each `Serializer` needs a hand-written `MapCodec` plus
  `StreamCodec<RegistryFriendlyByteBuf, T>` in place of `fromJson`/`fromNetwork`/`toNetwork`.
  The five dataless crafting recipes are already converted and can serve as the shape.
- **`item/tool/AOEEmberDiggerItem`, `SculkAxe`, `SlimeShovel`, `PrismarineShovel`.** Leftover NBT
  access plus the enchantment-Holder change.
- **`api/damage/*`, `effect/*`.** `MobEffect` → `Holder<MobEffect>` and damage-source changes.
- **`compat/jei/JEIPlugin`.** JEI 19.x moved `mezz.jei.api.forge` and changed `registerRecipes`.
- **`util/FaceRendererUtil`, `blockentity/render/*`, `model/AetherCrownGemLayer`.** The 1.21
  `VertexConsumer`/`BufferBuilder` rewrite.
- **`datagen/AWItemTags`, `AWItemProperties`, `AWBlockStates`.** Provider signature changes.

Once it compiles, none of this has been run in-game yet — the data attachment migration, the
capability bridge, and the data-component conversions all need runtime testing, and there is no
migration path written for worlds saved with the 1.20.1 version.

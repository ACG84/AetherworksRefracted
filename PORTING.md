# Porting Aetherworks Refracted to Embers Re-Ignited (1.21.1 / NeoForge)

This branch retargets the mod from **Minecraft 1.20.1 + Forge 47 + Embers Rekindled 1.4.7**
to **Minecraft 1.21.1 + NeoForge 21.1.228 + [Embers Re-Ignited](https://github.com/Riieno/EmbersRekindled-Reignited) 1.5.7**.

## Status

`./gradlew build` succeeds, and `./gradlew runServer` boots a dedicated server to
`Done (10.5s)!` with **no Aetherworks errors** — all registries, recipes, tags and loot
tables load. The only errors left in the log come from Embers itself (loot tables for its
Create-integration blocks, and common metal tags nothing else provides) and from vanilla
chunk generation.

`./gradlew runClient` also starts cleanly: the mod's resource pack loads, every model bakes,
texture atlases stitch, shaders compile and the JEI plugin initialises, with **no Aetherworks
errors**. That run found four real client-side bugs, now fixed (see below).

**Not yet verified:** actual gameplay. The headless container only has software OpenGL
(llvmpipe), which renders far too slowly to drive the title screen through to joining a world,
so nothing has been *seen* on screen and no block/item has been placed or used. There is also
no world-migration path from 1.20.1 saves — see "Known gaps" below.

## Dependency

Embers Re-Ignited resolves from the Modrinth maven (`8znVCTxA` is its project id):

```groovy
implementation "maven.modrinth:8znVCTxA:${embers_version}"   # 1.21.1-1.5.7
```

## What changed

| Area | Change |
|---|---|
| Build | ForgeGradle → ModDevGradle (`net.neoforged.moddev`), Java 17 → 21, Parchment 2024.11.17 |
| Metadata | `mods.toml` → `neoforge.mods.toml`, pack format 15 → 34 |
| Registries | `RegistryObject` → `DeferredHolder`/`DeferredItem`/`DeferredBlock`; `ForgeRegistries` → `BuiltInRegistries`/`Registries`/`NeoForgeRegistries` |
| Entrypoint | Mod bus and `ModContainer` are constructor parameters; `MinecraftForge` → `NeoForge`; `@Mod.EventBusSubscriber` → `@EventBusSubscriber` |
| Config | Registered against the mod's own `ModContainer`; `ForgeConfigSpec` → `ModConfigSpec` |
| Networking | `SimpleChannel` → `CustomPacketPayload` + `StreamCodec` + `RegisterPayloadHandlersEvent` |
| Item data | New `AWDataComponents` replaces stack NBT (toggle mode, focus, crown gem, lexicon item/amount, potion colour, shovel fluid) |
| Potions | `PotionUtils` → the `POTION_CONTENTS` component; effects are `Holder<MobEffect>` throughout |
| Capabilities | Chunk data → data attachments (`AWAttachments`); block-entity and item capabilities bridged onto NeoForge's system in `AWCapabilities`, mirroring Embers Re-Ignited's own legacy bridge |
| Block entities | 1.21 `loadAdditional`/`saveAdditional`/`getUpdateTag` signatures; `invalidateCaps` → `invalidateCapabilities` |
| Recipes | All eight serializers rewritten onto `MapCodec` + `StreamCodec`; recipes no longer carry their own id |
| Blocks | `BaseEntityBlock.codec()` implemented; `use` → `useItemOn` |
| Crossbows | Charge state → `CHARGED_PROJECTILES`; enchantments resolved via `Holder` |
| Client | `IGuiOverlay` → `LayeredDraw.Layer`; manual model baking → `ModelEvent.RegisterAdditional`; 1.21 `VertexConsumer` API |
| Tools | `ToolAction` → `ItemAbility`; `ForgeTier` → `SimpleTier`; `hurt` → `hurtAndBreak` |
| Datapack | Folders singularised (`recipes`→`recipe` etc.), `forge:` → `c:`, stack results `"item"` → `"id"`, conditions → `neoforge:conditions` |

### Access transformers

None are needed. The two internals the mod used to reach into (`ModelBakery.ModelBakerImpl`
and `EntityRenderDispatcher.renderers`) are both reachable through public 1.21 APIs, so
`accesstransformer.cfg` is now empty apart from a comment.

## Decisions worth reviewing

1. **Recipe datagen is excluded from compilation.** Embers Re-Ignited excludes its own recipe
   datagen from its published jar — `EmbersRecipes`, `AlchemyRecipeBuilder`,
   `ConsumerWrapperBuilder` and `GenericRecipeBuilder` are all verifiably absent. `AWRecipes`
   and the Aetherworks `*RecipeBuilder` classes are written against them, so they are excluded
   in `build.gradle` for the same reason. The recipes themselves were instead **migrated in
   place** to the 1.21 format and all load correctly; only *regenerating* them from source is
   unavailable until Embers restores that API.

2. **The Aetheric enchantment is now data-driven.** 1.21 removed enchantment subclassing. The
   registration was already commented out upstream ("aetherium items will just self-repair"),
   so `AethericEnchantment` is now a `ResourceKey<Enchantment>` plus an optional lookup, and
   `AethericEnchantmentHelper` no-ops when no datapack defines it. No new content was invented.

3. **Aetherium tier.** `TierSortingRegistry` is gone; a tier is described by the tag of blocks it
   *cannot* harvest. Aetherium uses `BlockTags.INCORRECT_FOR_NETHERITE_TOOL`, matching how
   Embers Re-Ignited treats its own clockwork tools, so it still mines everything it used to.

4. **AoE mining hook changed.** NeoForge removed `onBlockStartBreak`, so `AOEEmberDiggerItem`
   now triggers from `mineBlock`. The effect is the same (AoE fires on break) but it runs
   *after* the centre block breaks rather than before — worth a look in game.

5. **A latent bug was fixed incidentally.** `MessageFluidSync.decode` read `buf.capacity()`
   instead of the encoded int; the `StreamCodec.composite` rewrite makes this correct.

## Bugs the client run caught

1. `"loader": "forge:fluid_container"` in the four bucket models had been rewritten to `c:` by
   the datapack namespace sweep. Model loaders live under `neoforge:`, not `c:`.
2. Block model faces renamed `forge_data` to `neoforge_data` in 1.21 (anvil, ore, tool station).
3. `"model": "forge:fluid"` in the gas blockstates likewise became `neoforge:fluid`.
4. `crossbow_Base.json` had a capital letter. Every model referenced it as `crossbow_base`, so
   it was already broken on case-sensitive filesystems; 1.21 rejects non-lowercase resource
   paths outright, which finally surfaced it. Renamed to `crossbow_base.json`.

## Bug found in play testing

**`update_recipes` failed to encode on world load**, disconnecting the client with
`IllegalStateException: Can't encode 'PotionGemUnsocketRecipe@a', expected 'PotionGemUnsocketRecipe@b'`.

`StreamCodec.unit(value)` does not simply write nothing — its `encode` *asserts* that the value
being encoded `equals` the instance it was constructed with. The five dataless crafting recipes
have no `equals()`, and `MapCodec.unit(X::new)` builds a fresh instance per datapack load, so the
identity check could never pass and recipe sync threw for every player joining.

Fixed by giving those five a genuinely dataless stream codec:

```java
StreamCodec.of((buf, recipe) -> {}, buf -> new X());
```

`AWCodecSelfTest` was added as a regression guard: it round-trips all eight recipe serializers'
stream codecs exactly as the server does on join. It is opt-in, since the failure is otherwise
invisible until a player connects:

```
./gradlew runServer -Daetherworks.codecSelfTest=true     # look for AW-CODEC-SELFTEST in the log
```

It was validated both ways — all eight pass on the fixed code, and reintroducing
`StreamCodec.unit` on one recipe reproduces the original exception verbatim.

## Second bug found in play testing

**Moonsnare jars and cartridges rendered as nothing.** 1.21 switched item tints from RGB to
**ARGB**, so a handler returning a colour without an alpha byte is fully transparent rather than
opaque. Three handlers were affected:

| Handler | Returned | Now |
|---|---|---|
| `AetherEmberColorHandler` | `Misc.intColor(r,g,b)`, `16777215` | `0xFF000000 \| ...`, `0xFFFFFFFF` |
| `PotionGemItem.ColorHandler` | raw `POTION_COLOR` (RGB) | `0xFF000000 \| ...` |
| `AetherCrownItem.ColorHandler` | gem colour (RGB), `0x0021b2ff` | `0xFF000000 \| ...`, `0xFF21b2ff` |

Only the ember items were reported, but potion gems and the crown's gem overlay had the same
fault. Embers Re-Ignited's own ported handlers do exactly this (`0xFF000000 | Misc.intColor(...)`
and `0xFFFFFFFF`), which is what the fix follows. Colour *data* is still stored as plain RGB —
alpha is forced only on the render path.

## Dev conveniences added

`./gradlew runClient` accepts three optional properties:

- `-PquickPlay=<save>` — boot straight into a singleplayer world
- `-PquickPlayServer=<host:port>` — join a server on launch
- `-PsmallWindow` — 400x300 window, for software-rendered/headless runs

## Known gaps

- **Limited gameplay testing.** Recipe sync is now verified, but renderers, the aetheriometer
  overlay, the crown gem layer and JEI have only been confirmed to *load* — nothing has been
  rendered in a world or interacted with from this container. The AoE mining hook change and the
  data-component conversions especially want a play session.
- **No world migration.** Items saved by the 1.20.1 build keep their old NBT, which 1.21 will
  not read into the new data components. Existing tools will lose their toggle state, focus,
  socketed gem, lexicon contents and stored fluid. A `DataFixer`, or a one-off conversion on
  `ItemStack` load, would be needed to preserve them.
- **Embers' own log errors.** Embers Re-Ignited ships loot tables for Create-integration blocks
  that only exist when Create is installed, so those log parse errors without Create present.
  Not caused by this port.

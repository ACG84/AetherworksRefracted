package net.sirplop.aetherworks.datagen;

import com.rekindled.embers.datagen.EmbersBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class AWBlockTags  extends BlockTagsProvider {
    public AWBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Aetherworks.MODID, existingFileHelper);
    }
    public static final TagKey<Block> NEEDS_AETHERIUM_TOOL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "needs_aetherium_tool"));
    public static final TagKey<Block> SCULK_AXE_MINEABLE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "sculk_axe_mineable"));
    public static final TagKey<Block> FORGE_HEATER_BELOW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "forge_heater_below"));
    public static final TagKey<Block> FORGE_COOLER_BELOW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "forge_cooler_below"));
    public static final TagKey<Block> BLOCK_AETHERIUM = BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/aetherium"));
    public static final TagKey<Block> BLOCK_SHARDS = BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/raw_aetherium"));

    public static final TagKey<Block> DROPS_GEODES = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "drops_geodes"));
    public static final TagKey<Block> HOVH_RIGHTCLICK = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "hovh_rightclick"));
    public static final TagKey<Block> SOIC_BANNED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "soic_banned"));
    public static final TagKey<Block> AOSA_ALLOWED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aosa_allowed"));
    public static final TagKey<Block> AOTR_ALLOWED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aotr_allowed"));
    public static final TagKey<Block> POMD_ALLOWED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "pomd_allowed"));
    public static final TagKey<Block> POBS_BANNED = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "pobs_banned"));



    @Override
    protected void addTags(HolderLookup.Provider provider) {
        decoTags(AWRegistry.SUEVITE_COBBLE_DECO);
        decoTags(AWRegistry.SUEVITE_BRICKS_DECO);
        decoTags(AWRegistry.SUEVITE_SMALL_BRICKS_DECO);
        decoTags(AWRegistry.SUEVITE_BIG_TILE_DECO);
        decoTags(AWRegistry.SUEVITE_SMALL_TILE_DECO);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                AWRegistry.AETHERIUM_ORE.get(),
                AWRegistry.AETHERIUM_SHARD_BLOCK.get(),
                AWRegistry.AETHERIUM_BLOCK.get(),
                AWRegistry.PRISM_SUPPORT.get(),
                AWRegistry.PRISM.get(),
                AWRegistry.MOONLIGHT_AMPLIFIER.get(),
                AWRegistry.CONTROL_MATRIX.get(),
                AWRegistry.AETHER_FORGE.get(),
                AWRegistry.AETHER_FORGE_EDGE.get(),
                AWRegistry.FORGE_HEATER.get(),
                AWRegistry.FORGE_COOLER.get(),
                AWRegistry.FORGE_VENT.get(),
                AWRegistry.FORGE_ANVIL.get(),
                AWRegistry.FORGE_METAL_FORMER.get(),
                AWRegistry.FORGE_TOOL_STATION.get(),
                AWRegistry.LEXICON_RECEPTACLE.get(),
                AWRegistry.HEAT_DIAL.get(),
                AWRegistry.SUEVITE.get(),
                AWRegistry.SUEVITE_COBBLE.get(),
                AWRegistry.SUEVITE_BRICKS.get(),
                AWRegistry.SUEVITE_SMALL_BRICKS.get(),
                AWRegistry.SUEVITE_BIG_TILE.get(),
                AWRegistry.SUEVITE_SMALL_TILE.get(),
                AWRegistry.GLASS_AETHERIUM.get(),
                AWRegistry.GLASS_AETHERIUM_BORDERLESS.get()
        );
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                AWRegistry.AETHERIUM_ORE.get(),
                AWRegistry.AETHERIUM_SHARD_BLOCK.get(),
                AWRegistry.AETHERIUM_BLOCK.get()
        );
        tag(BlockTags.NEEDS_IRON_TOOL).add(
                AWRegistry.GLASS_AETHERIUM.get(),
                AWRegistry.GLASS_AETHERIUM_BORDERLESS.get()
        );

        tag(EmbersBlockTags.DIAL).add(AWRegistry.HEAT_DIAL.get());
        tag(EmbersBlockTags.RELOCATION_NOT_SUPPORTED).add(
                AWRegistry.AETHER_FORGE.get(),
                AWRegistry.AETHER_FORGE_EDGE.get()
        );
        tag(EmbersBlockTags.MECH_CORE_PROXYABLE_BOTTOM).add(
                AWRegistry.AETHER_FORGE.get()
        );

        tag(EmbersBlockTags.MECH_CORE_PROXYABLE).add(
                AWRegistry.LEXICON_RECEPTACLE.get()
        );

        tag(BLOCK_AETHERIUM).add(AWRegistry.AETHERIUM_BLOCK.get());
        tag(BLOCK_SHARDS).add(AWRegistry.AETHERIUM_SHARD_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(AWRegistry.AETHERIUM_BLOCK.get(), AWRegistry.AETHERIUM_SHARD_BLOCK.get());

        tag(BlockTags.WITHER_IMMUNE).add(AWRegistry.GLASS_AETHERIUM.get());
        getTag("forge:glass").add(AWRegistry.GLASS_AETHERIUM.get(), AWRegistry.GLASS_AETHERIUM_BORDERLESS.get());

        tag(SCULK_AXE_MINEABLE).addTags(BlockTags.MINEABLE_WITH_AXE, BlockTags.LEAVES);

        tag(FORGE_COOLER_BELOW).addTags(
                BlockTags.ICE
        );

        tag(DROPS_GEODES).add(
                Blocks.DRIPSTONE_BLOCK,
                Blocks.CALCITE,
                Blocks.SMOOTH_BASALT,
                Blocks.BASALT,
                Blocks.END_STONE,
                Blocks.OBSIDIAN,
                AWRegistry.SUEVITE.get()
        ).addTags(
                BlockTags.BASE_STONE_OVERWORLD,
                BlockTags.BASE_STONE_NETHER,
                BlockTags.NYLIUM,
                Tags.Blocks.SANDSTONE_BLOCKS
        );
        tag(HOVH_RIGHTCLICK).add(
                Blocks.SWEET_BERRY_BUSH,
                Blocks.COCOA,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT
        ).addOptional(ResourceLocation.parse("farmersdelight:tomatoes"));
        tag(SOIC_BANNED).add(
                Blocks.BEDROCK,
                Blocks.REINFORCED_DEEPSLATE,
                AWRegistry.AETHER_FORGE.get(),
                AWRegistry.AETHER_FORGE_EDGE.get()
        );
        tag(AOSA_ALLOWED).addTag(BlockTags.LEAVES);
        tag(AOTR_ALLOWED).addTag(BlockTags.LOGS);
        tag(POMD_ALLOWED).add(
                Blocks.DRIPSTONE_BLOCK,
                Blocks.CALCITE,
                Blocks.SMOOTH_BASALT,
                Blocks.BASALT,
                Blocks.END_STONE,
                Blocks.OBSIDIAN,
                AWRegistry.SUEVITE.get()
        ).addTags(
                BlockTags.BASE_STONE_OVERWORLD,
                BlockTags.BASE_STONE_NETHER,
                BlockTags.NYLIUM,
                Tags.Blocks.SANDSTONE_BLOCKS
        );
        tag(POBS_BANNED).add(
                Blocks.BEDROCK,
                Blocks.REINFORCED_DEEPSLATE,
                AWRegistry.AETHER_FORGE.get(),
                AWRegistry.AETHER_FORGE_EDGE.get()
        );

        //individual tags
        getTag("forge:cobblestone").add(AWRegistry.SUEVITE_COBBLE.get());
        getTag("minecraft:sculk_replaceable").add(AWRegistry.SUEVITE.get());
        getTag("forge:stone").add(AWRegistry.SUEVITE.get());
        getTag("minecraft:snaps_goat_horn").add(AWRegistry.SUEVITE.get());
        getTag("minecraft:moss_replaceable").add(AWRegistry.SUEVITE.get());
        getTag("minecraft:stone_bricks").add(AWRegistry.SUEVITE_BRICKS.get());

        getTag("forge:ores").add(AWRegistry.AETHERIUM_ORE.get());
    }

    public IntrinsicTagAppender<Block> getTag(String name) {
        return tag(BlockTags.create(ResourceLocation.parse(name)));
    }


    public void decoTags(AWRegistry.StoneDecoBlocks deco) {
        if (deco.stairs != null) {
            tag(BlockTags.STAIRS).add(deco.stairs.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(deco.stairs.get());
        }
        if (deco.slab != null) {
            tag(BlockTags.SLABS).add(deco.slab.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(deco.slab.get());
        }
        if (deco.wall != null) {
            tag(BlockTags.WALLS).add(deco.wall.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(deco.wall.get());
        }
    }
}

package net.sirplop.aetherworks.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.datagen.EmbersItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.compat.curios.CuriosCompat;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;


public class AWItemTags extends ItemTagsProvider {
    public AWItemTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, Aetherworks.MODID, existingFileHelper);
    }
    public static final TagKey<Item> RAW_AETHERIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "raw_materials/aetherium"));
    public static final TagKey<Item> BLOCK_AETHERIUM = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/aetherium"));
    public static final TagKey<Item> BLOCK_SHARDS = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/raw_aetherium"));
    public static final TagKey<Item> AETHERIUM_INGOT = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "ingots/aetherium"));
    public static final TagKey<Item> AETHERIUM_PLATE = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "plates/aetherium"));
    public static final TagKey<Item> AETHERIUM_ASPECTUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "aspectus/aetherium"));


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.INGOTS).addTag(AETHERIUM_INGOT);
        tag(EmbersItemTags.PLATES).addTag(AETHERIUM_PLATE);
        tag(Tags.Items.RAW_MATERIALS).addTag((RAW_AETHERIUM));

        tag(RAW_AETHERIUM).add(AWRegistry.AETHER_SHARD.get());
        tag(AETHERIUM_INGOT).add(AWRegistry.INGOT_AETHER.get());
        tag(AETHERIUM_PLATE).add(AWRegistry.PLATE_AETHER.get());
        tag(AETHERIUM_ASPECTUS).add(AWRegistry.AETHER_ASPECTUS.get());
        tag(EmbersItemTags.ASPECTUS).add(AWRegistry.AETHER_ASPECTUS.get());

        tag(Tags.Items.ORES).add(AWRegistry.AETHERIUM_ORE.get().asItem());
        tag(Tags.Items.STORAGE_BLOCKS).add(
                AWRegistry.AETHERIUM_SHARD_BLOCK.get().asItem(),
                AWRegistry.AETHERIUM_BLOCK.get().asItem()
        );
        tag(BLOCK_AETHERIUM).add(AWRegistry.AETHERIUM_BLOCK.get().asItem());
        tag(BLOCK_SHARDS).add(AWRegistry.AETHERIUM_SHARD_BLOCK.get().asItem());
        tag(Tags.Items.COBBLESTONE).add(AWRegistry.SUEVITE_COBBLE.get().asItem());
        tag(Tags.Items.STONE).add(AWRegistry.SUEVITE.get().asItem());
        tag(Tags.Items.GLASS).add(
                AWRegistry.GLASS_AETHERIUM.get().asItem(),
                AWRegistry.GLASS_AETHERIUM_BORDERLESS.get().asItem()
        );
        tag(Tags.Items.GLASS_BLUE).add(
                AWRegistry.GLASS_AETHERIUM.get().asItem(),
                AWRegistry.GLASS_AETHERIUM_BORDERLESS.get().asItem()
        );

        tag(ItemTags.PICKAXES).add(AWRegistry.PICKAXE_EMBER.get(), AWRegistry.PICKAXE_AETHER.get());
        tag(ItemTags.AXES).add(AWRegistry.AXE_ENDER.get(), AWRegistry.AXE_SCULK.get());
        tag(ItemTags.SHOVELS).add(AWRegistry.SHOVEL_SLIME.get(), AWRegistry.SHOVEL_PRISMARINE.get());
        tag(ItemTags.HOES).add(AWRegistry.HOE_AMETHYST.get(), AWRegistry.HOE_HONEY.get());
        tag(Tags.Items.TOOLS_SHIELDS).add(AWRegistry.AETHER_SHIELD.get());

        tag(EmbersItemTags.NORMAL_WALK_SPEED_TOOL).add(AWRegistry.CROSSBOW_QUARTZ.get(), AWRegistry.CROSSBOW_MAGMA.get());
        tag(EmbersItemTags.AUGMENTABLE_PROJECTILE_WEAPONS).add(AWRegistry.CROSSBOW_QUARTZ.get(), AWRegistry.CROSSBOW_MAGMA.get());

        tag(EmbersItemTags.GAUGE_OVERLAY).add(AWRegistry.AETHERIOMETER.get());

        tag(EmbersItemTags.TINKER_LENS_HELMETS).add(AWRegistry.AETHER_CROWN.get());
        tag(Tags.Items.ARMORS_HELMETS).add(AWRegistry.AETHER_CROWN.get());

        tag(EmbersItemTags.ANY_CURIO).addOptional(CuriosCompat.AETHER_EMBER_BULB.getId());
    }
}

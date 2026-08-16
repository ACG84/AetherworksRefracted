package net.sirplop.aetherworks.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sirplop.aetherworks.Aetherworks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AWBiomeTags extends BiomeTagsProvider {
    public AWBiomeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Aetherworks.MODID, existingFileHelper);
    }

    public static TagKey<Biome> TC_END_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_end_geodes"));
    public static TagKey<Biome> TC_NETHER_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_nether_geodes"));
    public static TagKey<Biome> TC_OCEAN_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_ocean_geodes"));
    public static TagKey<Biome> TC_HOT_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_hot_geodes"));
    public static TagKey<Biome> TC_COLD_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_cold_geodes"));
    public static TagKey<Biome> TC_MAGIC_GEODES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "tc_magic_geodes"));

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        tag(TC_END_GEODES).addTags(BiomeTags.IS_END);
        tag(TC_NETHER_GEODES).addTags(BiomeTags.IS_NETHER);
        tag(TC_OCEAN_GEODES).addTags(BiomeTags.IS_OCEAN, BiomeTags.IS_DEEP_OCEAN);
        tag(TC_HOT_GEODES).addTags(Tags.Biomes.IS_HOT).remove(BiomeTags.IS_NETHER); //sure it's hot, but it's not the nether
        tag(TC_COLD_GEODES).addTags(Tags.Biomes.IS_COLD).remove(BiomeTags.IS_END); //sure it's cold, but it's not the end
        tag(TC_MAGIC_GEODES).addOptionalTag(Tags.Biomes.IS_MAGICAL);
    }
}

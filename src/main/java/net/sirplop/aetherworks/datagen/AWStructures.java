package net.sirplop.aetherworks.datagen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.worldgen.MeteorStructure;

import java.util.Map;

public class AWStructures {
    public static final ResourceKey<Structure> METEOR = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "meteor"));

    public static void generateStructures(BootstapContext<Structure> bootstrap) {
        HolderGetter<Biome> biome = bootstrap.lookup(ForgeRegistries.Keys.BIOMES);
        HolderSet<Biome> overworldBiomes = biome.getOrThrow(BiomeTags.IS_OVERWORLD);
        bootstrap.register(METEOR, new MeteorStructure(new Structure.StructureSettings(overworldBiomes, Map.of(MobCategory.AMBIENT,
                new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create())),
                GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
    }


    public static final ResourceKey<StructureSet> METEOR_SET = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "meteor"));

    public static void generateSets(BootstapContext<StructureSet> bootstrap) {
        HolderGetter<Structure> structure = bootstrap.lookup(Registries.STRUCTURE);

        bootstrap.register(METEOR_SET, new StructureSet(structure.getOrThrow(METEOR),
                new RandomSpreadStructurePlacement(15, 5, RandomSpreadType.LINEAR, 773540182)));
    }
}

package net.sirplop.aetherworks.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class AWLootTables extends LootTableProvider {
    public AWLootTables(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(AWBlockLootTables::new, LootContextParamSets.BLOCK)
                //new LootTableProvider.SubProviderEntry(EmbersEntityLootTables::new, LootContextParamSets.ENTITY)
        ), registries);
    }
}

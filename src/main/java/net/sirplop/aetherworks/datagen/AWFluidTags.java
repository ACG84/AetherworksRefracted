package net.sirplop.aetherworks.datagen;

import com.rekindled.embers.datagen.EmbersFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class AWFluidTags extends FluidTagsProvider {

    public AWFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Aetherworks.MODID, existingFileHelper);
    }

    public static final TagKey<Fluid> AETHERIUM = FluidTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aetherium"));
    public static final TagKey<Fluid> FORGE_HEATER_BELOW = FluidTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "forge_heater_below"));
    public static final TagKey<Fluid> FORGE_COOLER_BELOW = FluidTags.create(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "forge_cooler_below"));

    @Override
    public void addTags(HolderLookup.Provider provider) {
        tag(AETHERIUM).add(
                AWRegistry.AETHERIUM_GAS_IMPURE.FLUID.get(),
                AWRegistry.AETHERIUM_GAS_IMPURE.FLUID_FLOW.get(),
                AWRegistry.AETHERIUM_GAS.FLUID.get(),
                AWRegistry.AETHERIUM_GAS.FLUID_FLOW.get(),
                AWRegistry.SEETHING_AETHERIUM.FLUID.get(),
                AWRegistry.SEETHING_AETHERIUM.FLUID_FLOW.get()
        );
        tag(EmbersFluidTags.INGOT_TOOLTIP).addTag(AETHERIUM);

        tag(FORGE_HEATER_BELOW).addTag(
                FluidTags.LAVA
        ).add(AWRegistry.ALCHEMIC_PRECURSOR.FLUID.get()).addOptionalTags(
                EmbersFluidTags.MOLTEN_ALUMINUM,
                EmbersFluidTags.MOLTEN_BRASS,
                EmbersFluidTags.MOLTEN_BRONZE,
                EmbersFluidTags.MOLTEN_COPPER,
                EmbersFluidTags.MOLTEN_DAWNSTONE,
                EmbersFluidTags.MOLTEN_CONSTANTAN,
                EmbersFluidTags.MOLTEN_ELECTRUM,
                EmbersFluidTags.MOLTEN_GOLD,
                EmbersFluidTags.MOLTEN_INVAR,
                EmbersFluidTags.MOLTEN_IRON,
                EmbersFluidTags.MOLTEN_LEAD,
                EmbersFluidTags.MOLTEN_NICKEL,
                EmbersFluidTags.MOLTEN_PLATINUM,
                EmbersFluidTags.MOLTEN_TIN,
                EmbersFluidTags.MOLTEN_URANIUM,
                EmbersFluidTags.MOLTEN_ZINC
        );
    }
}

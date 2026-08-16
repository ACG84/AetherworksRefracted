package net.sirplop.aetherworks.capabilities;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.capabilities.CapabilityManager;
import com.rekindled.embers.compat.legacy.capabilities.CapabilityToken;
import com.rekindled.embers.compat.legacy.capabilities.ForgeCapabilities;
import com.rekindled.embers.util.CapabilityCompat;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.api.capabilities.IHeatCapability;

/**
 * Capabilities work in two layers after the 1.21 rewrite.
 * <p>
 * The {@link Capability} tokens below are the legacy markers Embers still exposes through
 * {@code IExtraCapabilityInformation} and its block entities' {@code getCapability} methods, so
 * they have to stay. The {@link BlockCapability} is the real NeoForge lookup, and
 * {@link #registerCapabilities} bridges one to the other exactly as Embers Re-Ignited does for
 * its own block entities.
 */
public class AWCapabilities {

    public static final Capability<IHeatCapability> HEAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final BlockCapability<IHeatCapability, Direction> HEAT_BLOCK_CAPABILITY =
            BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "heat"), IHeatCapability.class);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> entry : AWRegistry.BLOCK_ENTITY_TYPES.getEntries()) {
            registerLegacyBlockEntityCapabilities(event, entry.get());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> void registerLegacyBlockEntityCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<?> type) {
        BlockEntityType<T> blockEntityType = (BlockEntityType<T>) type;
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType, (blockEntity, side) -> {
            IItemHandler handler = CapabilityCompat.getCapability(blockEntity, ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
            return handler;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, blockEntityType, (blockEntity, side) -> {
            IFluidHandler handler = CapabilityCompat.getCapability(blockEntity, ForgeCapabilities.FLUID_HANDLER, side).orElse(null);
            return handler;
        });
        event.registerBlockEntity(EmbersCapabilities.EMBER_BLOCK_CAPABILITY, blockEntityType, (blockEntity, side) -> {
            IEmberCapability handler = CapabilityCompat.getCapability(blockEntity, EmbersCapabilities.EMBER_CAPABILITY, side).orElse(null);
            return handler;
        });
        event.registerBlockEntity(HEAT_BLOCK_CAPABILITY, blockEntityType, (blockEntity, side) -> {
            IHeatCapability handler = CapabilityCompat.getCapability(blockEntity, HEAT_CAPABILITY, side).orElse(null);
            return handler;
        });
    }
}

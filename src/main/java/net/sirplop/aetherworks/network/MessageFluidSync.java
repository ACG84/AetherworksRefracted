package net.sirplop.aetherworks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.AWDataComponents;
import net.sirplop.aetherworks.Aetherworks;

public record MessageFluidSync(ItemStack held, FluidStack fluid, int capacity) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageFluidSync> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "fluid_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFluidSync> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, MessageFluidSync::held,
            FluidStack.OPTIONAL_STREAM_CODEC, MessageFluidSync::fluid,
            ByteBufCodecs.INT, MessageFluidSync::capacity,
            MessageFluidSync::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageFluidSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            //FluidHandlerItemStack now wraps a mutable component holder rather than raw NBT.
            FluidHandlerItemStack stack = new FluidHandlerItemStack(
                    AWDataComponents.FLUID_CONTENT.get(), msg.held(), msg.capacity());
            setFluid(stack, msg.fluid());
        });
    }

    public static void setFluid(FluidHandlerItemStack held, FluidStack focus) {
        if(!held.getContainer().isEmpty()) {
            if (focus.isEmpty()) {
                held.drain(held.getTankCapacity(1), IFluidHandler.FluidAction.EXECUTE);
            }
            else {
                held.drain(held.getFluidInTank(1).getAmount(), IFluidHandler.FluidAction.EXECUTE);
                held.fill(focus, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }
}

package net.sirplop.aetherworks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.AWDataComponents;
import net.sirplop.aetherworks.Aetherworks;

public record MessageFocusedStack(ItemStack held, ItemStack focus) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageFocusedStack> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "focused_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFocusedStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, MessageFocusedStack::held,
            ItemStack.OPTIONAL_STREAM_CODEC, MessageFocusedStack::focus,
            MessageFocusedStack::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageFocusedStack msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> setFocus(msg.held(), msg.focus()));
    }

    public static void setFocus(ItemStack held, ItemStack focus) {
        if(!held.isEmpty()) {
            if (focus.isEmpty())
                held.remove(AWDataComponents.FOCUS.get());
            else
                held.set(AWDataComponents.FOCUS.get(), focus.copy());
        }
    }
}

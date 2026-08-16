package net.sirplop.aetherworks.network;

import com.rekindled.embers.util.EmberInventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.AWConfig;
import net.sirplop.aetherworks.AWDataComponents;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.api.item.IToggleEmberItem;
import net.sirplop.aetherworks.api.item.IToggleItem;

public record MessageToggleItem(byte stateFlag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageToggleItem> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "toggle_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageToggleItem> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, MessageToggleItem::stateFlag,
            MessageToggleItem::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageToggleItem msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer serverPlayer) {
                handleServer(serverPlayer);
            } else {
                handleClient(msg);
            }
        });
    }

    private static void handleServer(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IToggleItem toggle) {
            toggle.toggleItem(stack, player, (byte)0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageToggleItem msg) {
        Player player = Minecraft.getInstance().player;
        if (player != null)
            toggleItem(player.getMainHandItem(), player, msg.stateFlag());
    }

    /**
     * The toggle state lives in a data component now rather than loose stack NBT; "no component"
     * takes the place of the old "no tag" branch.
     */
    public static void toggleItem(ItemStack stack, Player player, byte stateFlag) {
        if(!stack.isEmpty() && stack.getItem() instanceof IToggleItem item) {
            Level level = player.getCommandSenderWorld();
            Byte current = stack.get(AWDataComponents.TOGGLE_MODE.get());
            if (stack.getItem() instanceof IToggleEmberItem) { //toggle and check min ember use config.
                if (current == null) {
                    byte val = (byte) (EmberInventoryUtil.getEmberTotal(player) >= AWConfig.TOOL_EMBER_USE.get() ? 1 : 0);
                    stack.set(AWDataComponents.TOGGLE_MODE.get(), val);
                    if (level.isClientSide)
                        item.clientModeChanged(stack, player, (byte)0, val, (byte)0);
                } else {
                    byte oldVal = current;
                    byte val = (byte)(oldVal + 1);
                    boolean ember = EmberInventoryUtil.getEmberTotal(player) < AWConfig.TOOL_EMBER_USE.get();
                    if (val > item.getToggleMax() || ember)
                        val = 0;
                    stack.set(AWDataComponents.TOGGLE_MODE.get(), val);
                    if (level.isClientSide)
                        item.clientModeChanged(stack, player, oldVal, val, (byte)(ember ? 1 : stateFlag));
                }
            } else if (current == null) {
                stack.set(AWDataComponents.TOGGLE_MODE.get(), (byte)1);
                if (level.isClientSide)
                    item.clientModeChanged(stack, player, (byte)0, (byte)1, stateFlag);
            } else {
                byte oldVal = current;
                byte val = (byte)(oldVal + 1);
                if (val > item.getToggleMax())
                    val = 0;
                stack.set(AWDataComponents.TOGGLE_MODE.get(), val);
                if (level.isClientSide)
                    item.clientModeChanged(stack, player, oldVal, val, stateFlag);
            }
        }
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(new MessageToggleItem((byte)0));
    }
}

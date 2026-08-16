package net.sirplop.aetherworks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.Aetherworks;

public record MessageSyncItemEntityTag(int syncTarget, String tag) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncItemEntityTag> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "sync_item_entity_tag"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncItemEntityTag> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageSyncItemEntityTag::syncTarget,
            ByteBufCodecs.STRING_UTF8, MessageSyncItemEntityTag::tag,
            MessageSyncItemEntityTag::new);

    public MessageSyncItemEntityTag(ItemEntity entity, String add) {
        this(entity.getId(), add);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageSyncItemEntityTag msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> handleClient(msg));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageSyncItemEntityTag msg) {
        Entity ent = Minecraft.getInstance().level.getEntity(msg.syncTarget());
        if (ent instanceof ItemEntity) {
            ent.addTag(msg.tag());
        }
    }
}

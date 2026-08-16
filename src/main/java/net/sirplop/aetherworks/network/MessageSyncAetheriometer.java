package net.sirplop.aetherworks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.api.capabilities.IAetheriometerCap;
import net.sirplop.aetherworks.capabilities.AetheriometerChunk;
import net.sirplop.aetherworks.capabilities.AetheriometerChunkCapability;

public record MessageSyncAetheriometer(ChunkPos pos, int set) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncAetheriometer> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "sync_aetheriometer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncAetheriometer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG.map(ChunkPos::new, ChunkPos::toLong), MessageSyncAetheriometer::pos,
            ByteBufCodecs.INT, MessageSyncAetheriometer::set,
            MessageSyncAetheriometer::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageSyncAetheriometer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> handleClient(msg));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageSyncAetheriometer msg) {
        //LogicalSidedProvider is gone; on the client the level is simply Minecraft's.
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        final IAetheriometerCap data = AetheriometerChunkCapability.getData(level, msg.pos());
        if (data instanceof final AetheriometerChunk chunk) {
            chunk.setDataNoUpdate(msg.set());
        }
    }
}

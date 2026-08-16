package net.sirplop.aetherworks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.client.AetherShieldReflectHandler;
import org.joml.Vector3f;

public record MessageSyncEntityMotion(int syncTarget, Vector3f deltaMotion, float yRot,
                                      float yRotO) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSyncEntityMotion> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "sync_entity_motion"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSyncEntityMotion> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MessageSyncEntityMotion::syncTarget,
            ByteBufCodecs.VECTOR3F, MessageSyncEntityMotion::deltaMotion,
            ByteBufCodecs.FLOAT, MessageSyncEntityMotion::yRot,
            ByteBufCodecs.FLOAT, MessageSyncEntityMotion::yRotO,
            MessageSyncEntityMotion::new);

    public MessageSyncEntityMotion(Entity ent, Vector3f deltaMotion, float yRot, float yRotO) {
        this(ent.getId(), deltaMotion, yRot, yRotO);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageSyncEntityMotion msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> handleClient(msg));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(MessageSyncEntityMotion msg) {
        Entity ent = Minecraft.getInstance().level.getEntity(msg.syncTarget());
        if (ent == null)
            return;
        ent.setDeltaMovement(new Vec3(msg.deltaMotion().x, msg.deltaMotion().y, msg.deltaMotion().z));
        ent.setYRot(msg.yRot());
        ent.yRotO = msg.yRotO();
        AetherShieldReflectHandler.setReflected((Projectile)ent);
    }
}

package net.sirplop.aetherworks.network;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.Aetherworks;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public record MessageShieldParticle(Vector3f point, float rotX, float rotY, byte numberOfParticles,
                                    int packedColor) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageShieldParticle> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "shield_particle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageShieldParticle> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, MessageShieldParticle::point,
            ByteBufCodecs.FLOAT, MessageShieldParticle::rotX,
            ByteBufCodecs.FLOAT, MessageShieldParticle::rotY,
            ByteBufCodecs.BYTE, MessageShieldParticle::numberOfParticles,
            ByteBufCodecs.INT, MessageShieldParticle::packedColor,
            MessageShieldParticle::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageShieldParticle msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> spawnParticles(msg));
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnParticles(MessageShieldParticle msg) {
        Level level = Minecraft.getInstance().level;
        assert level != null;


        PoseStack poseStack = new PoseStack();
        poseStack.translate(msg.point().x, msg.point().y, msg.point().z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(0));
        poseStack.mulPose(Axis.YP.rotation((float)-Math.toRadians(msg.rotY())));
        poseStack.mulPose(Axis.XP.rotation((float)Math.toRadians(msg.rotX())));
        Matrix4f matrix4f = poseStack.last().pose();

        Vector3f color = Misc.colorFromInt(msg.packedColor());
        GlowParticleOptions glow = new GlowParticleOptions(color, 1.25f, 10);
        double rotInc = (Math.PI * 2) / (double)msg.numberOfParticles();
        double randStart = level.random.nextFloat() * (0.087);

        for (int i = 0; i < msg.numberOfParticles(); i++) {
            float x = (float)Math.cos((rotInc * i) + randStart);
            float y = (float)Math.sin((rotInc * i) + randStart);

            Vector3f point = matrix4f.transformPosition(new Vector3f(x, y,  0));
            Vector3f speed = matrix4f.transformDirection(new Vector3f(x * 0.25f, y, -0.5f));
            level.addParticle(glow, false, point.x, point.y, point.z,
                    speed.x, speed.y, speed.z);
        }
    }
}

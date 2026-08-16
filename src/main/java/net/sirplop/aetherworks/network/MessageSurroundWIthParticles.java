package net.sirplop.aetherworks.network;

import com.rekindled.embers.particle.GlowParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.joml.Vector3f;

public record MessageSurroundWIthParticles(BlockPos pos, int numberOfParticles, Vector3f color) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageSurroundWIthParticles> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "surround_with_particles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSurroundWIthParticles> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, MessageSurroundWIthParticles::pos,
            ByteBufCodecs.INT, MessageSurroundWIthParticles::numberOfParticles,
            ByteBufCodecs.VECTOR3F, MessageSurroundWIthParticles::color,
            MessageSurroundWIthParticles::new);

    public MessageSurroundWIthParticles(BlockPos pos, int numberOfParticles, float r, float g, float b) {
        this(pos, numberOfParticles, new Vector3f(r, g, b));
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageSurroundWIthParticles msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> spawnParticles(msg));
    }

    @OnlyIn(Dist.CLIENT)
    public static void spawnParticles(MessageSurroundWIthParticles msg) {
        Level level = Minecraft.getInstance().level;
        assert level != null;
        BlockPos pos = msg.pos();
        GlowParticleOptions particle = new GlowParticleOptions(msg.color(), 1.0F, 50);
        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                for (int i = 0; i < msg.numberOfParticles(); i++) {
                    Direction.Axis direction$axis = direction.getAxis();
                    double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : level.random.nextFloat();
                    double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : level.random.nextFloat();
                    double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : level.random.nextFloat();
                    level.addParticle(particle,
                            (double) pos.getX() + d1,
                            (double) pos.getY() + d2,
                            (double) pos.getZ() + d3,
                            (level.random.nextFloat() - 0.5f) * 0.3f,
                            (level.random.nextFloat() - 0.5f) * 0.3f,
                            (level.random.nextFloat() - 0.5f) * 0.3f);
                }
            }
        }
    }
}

package net.sirplop.aetherworks.network;

import com.rekindled.embers.particle.GlowParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sirplop.aetherworks.Aetherworks;
import org.joml.Vector3f;

public record MessageHarvestNode(int stateID, BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageHarvestNode> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "harvest_node"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageHarvestNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MessageHarvestNode::stateID,
            BlockPos.STREAM_CODEC, MessageHarvestNode::pos,
            MessageHarvestNode::new);

    public MessageHarvestNode(BlockState state, BlockPos pos) {
        this(Block.getId(state), pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getStateID() {
        return stateID;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageHarvestNode msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> spawnParticles(msg));
    }

    private static final GlowParticleOptions GLOW = new GlowParticleOptions(new Vector3f(0, 0.72F, 0.95F), 1.0F, 100);

    @OnlyIn(Dist.CLIENT)
    public static void spawnParticles(MessageHarvestNode msg) {

        Level level = Minecraft.getInstance().level;
        final BlockParticleOption BLOCK = new BlockParticleOption(ParticleTypes.BLOCK, Block.stateById(msg.getStateID()));
        for (int i = 0; i < 10; ++i)
        {
            level.addParticle(GLOW,
                    msg.getPos().getX() + level.random.nextFloat(),
                    msg.getPos().getY() + level.random.nextFloat(),
                    msg.getPos().getZ() + level.random.nextFloat(),
                    level.random.nextFloat() - level.random.nextFloat() / 10f,
                    level.random.nextFloat() - level.random.nextFloat() / 10f,
                    level.random.nextFloat() - level.random.nextFloat() / 10f);
            level.addParticle(BLOCK,
                    msg.getPos().getX() + level.random.nextFloat(),
                    msg.getPos().getY() + level.random.nextFloat(),
                    msg.getPos().getZ() + level.random.nextFloat(),
                    0, 0, 0);
        }
    }
}

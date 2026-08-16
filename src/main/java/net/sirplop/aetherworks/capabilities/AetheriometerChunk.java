package net.sirplop.aetherworks.capabilities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sirplop.aetherworks.api.capabilities.IAetheriometerCap;
import net.sirplop.aetherworks.network.MessageSyncAetheriometer;

/**
 * View over a chunk's aetheriometer reading. The value itself lives in the AETHER_AMOUNT data
 * attachment on the LevelChunk; this type only supplies the level/chunk context and the
 * client-sync behaviour that used to live in the capability provider.
 */
public class AetheriometerChunk implements IAetheriometerCap {

    private final Level level;
    private final ChunkPos chunk;

    public AetheriometerChunk(final Level level, final ChunkPos chunkPos) {
        this.level = level;
        this.chunk = chunkPos;
    }

    private LevelChunk chunk() {
        return level.getChunk(chunk.x, chunk.z);
    }

    @Override
    public int getData() {
        return chunk().getData(AWAttachments.AETHER_AMOUNT.get());
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public ChunkPos getChunk() {
        return chunk;
    }

    @Override
    public void adjustData(int change) {
        LevelChunk levelChunk = chunk();
        int updated = Math.max(0, levelChunk.getData(AWAttachments.AETHER_AMOUNT.get()) + change);
        levelChunk.setData(AWAttachments.AETHER_AMOUNT.get(), updated);
        onChange(levelChunk, updated);
    }

    public void setDataNoUpdate(int set) {
        chunk().setData(AWAttachments.AETHER_AMOUNT.get(), Math.max(0, set));
    }

    private void onChange(LevelChunk levelChunk, int data) {
        if (level.isClientSide)
            return;

        levelChunk.setUnsaved(true);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunk, new MessageSyncAetheriometer(chunk, data));
    }
}

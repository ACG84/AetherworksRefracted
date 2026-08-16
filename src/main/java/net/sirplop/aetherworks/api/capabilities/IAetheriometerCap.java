package net.sirplop.aetherworks.api.capabilities;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Per-chunk aetherium reading. This is no longer a serializable capability provider - the value
 * is stored in a data attachment on the chunk, so nothing here needs to handle NBT itself.
 */
public interface IAetheriometerCap {
    int getData();
    Level getLevel();
    ChunkPos getChunk();
    void adjustData(int change);
}

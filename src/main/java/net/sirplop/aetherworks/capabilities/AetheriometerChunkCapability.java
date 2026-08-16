package net.sirplop.aetherworks.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.api.capabilities.IAetheriometerCap;
import net.sirplop.aetherworks.network.MessageSyncAetheriometer;
import net.sirplop.aetherworks.worldgen.MeteorPlacer;

public final class AetheriometerChunkCapability {

    /**
     * The ID of this data.
     */
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether");

    /**
     * Get the {@link IAetheriometerCap} for the {@link Level} and chunk position.
     */
    public static IAetheriometerCap getData(final Level level, final ChunkPos chunkPos) {
        return new AetheriometerChunk(level, chunkPos);
    }

    /**
     * Get the {@link IAetheriometerCap} for the chunk.
     */
    public static IAetheriometerCap getData(final LevelChunk chunk) {
        return new AetheriometerChunk(chunk.getLevel(), chunk.getPos());
    }

    @EventBusSubscriber(modid = Aetherworks.MODID)
    @SuppressWarnings("unused")
    private static class EventHandler {

        /**
         * Meteor generation records its readings before the chunk object exists, so the pending
         * values are drained onto the chunk the first time a player starts watching it.
         */
        @SubscribeEvent
        public static void chunkWatch(final ChunkWatchEvent.Watch event) {
            final var chunkPos = event.getPos();
            final var level = event.getLevel();

            if (MeteorPlacer.map.containsKey(chunkPos)) {
                int set = MeteorPlacer.map.remove(chunkPos);
                level.getChunk(chunkPos.x, chunkPos.z).setData(AWAttachments.AETHER_AMOUNT.get(), Math.max(0, set));
            }

            final int data = getData(level, chunkPos).getData();
            PacketDistributor.sendToPlayer(event.getPlayer(), new MessageSyncAetheriometer(chunkPos, data));
        }
    }
}

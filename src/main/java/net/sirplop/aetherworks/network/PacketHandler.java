package net.sirplop.aetherworks.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.sirplop.aetherworks.Aetherworks;

/**
 * 1.20.5 replaced SimpleChannel with typed CustomPacketPayloads registered against a versioned
 * registrar, so each message now carries its own Type and StreamCodec instead of being assigned
 * a numeric id here.
 */
public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Aetherworks.MODID).versioned(PROTOCOL_VERSION);

        registrar.playToClient(MessageHarvestNode.TYPE, MessageHarvestNode.STREAM_CODEC, MessageHarvestNode::handle);
        registrar.playBidirectional(MessageToggleItem.TYPE, MessageToggleItem.STREAM_CODEC, MessageToggleItem::handle);
        registrar.playToClient(MessageSyncItemEntityTag.TYPE, MessageSyncItemEntityTag.STREAM_CODEC, MessageSyncItemEntityTag::handle);
        registrar.playToClient(MessageFocusedStack.TYPE, MessageFocusedStack.STREAM_CODEC, MessageFocusedStack::handle);
        registrar.playToClient(MessageSurroundWIthParticles.TYPE, MessageSurroundWIthParticles.STREAM_CODEC, MessageSurroundWIthParticles::handle);
        registrar.playToClient(MessageFluidSync.TYPE, MessageFluidSync.STREAM_CODEC, MessageFluidSync::handle);
        registrar.playToClient(MessageSyncAetheriometer.TYPE, MessageSyncAetheriometer.STREAM_CODEC, MessageSyncAetheriometer::handle);
        registrar.playToClient(MessageShieldParticle.TYPE, MessageShieldParticle.STREAM_CODEC, MessageShieldParticle::handle);
        registrar.playToClient(MessageSyncEntityMotion.TYPE, MessageSyncEntityMotion.STREAM_CODEC, MessageSyncEntityMotion::handle);
    }
}

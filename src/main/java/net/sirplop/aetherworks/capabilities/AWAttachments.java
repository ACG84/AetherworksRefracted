package net.sirplop.aetherworks.capabilities;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sirplop.aetherworks.Aetherworks;

/**
 * AttachCapabilitiesEvent was removed in 1.21; arbitrary data hung off a chunk is now a data
 * attachment instead. The aetheriometer reading is just an int, so it attaches directly.
 */
public class AWAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Aetherworks.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> AETHER_AMOUNT =
            ATTACHMENT_TYPES.register("aether_amount", () -> AttachmentType
                    .builder(() -> 0)
                    .serialize(Codec.INT)
                    .build());
}

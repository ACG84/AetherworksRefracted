package net.sirplop.aetherworks.enchantment;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.sirplop.aetherworks.Aetherworks;

import java.util.Optional;

/**
 * Might come back to this and change the aetheric items to use this instead.
 * <p>
 * 1.21 made enchantments data-driven, so there is no Enchantment subclass to extend any more:
 * an enchantment is a datapack entry and code only ever refers to it by key. Aetheric is still
 * not shipped as a datapack entry (aetherium items simply self-repair), so this key resolves to
 * nothing unless a pack defines it - which is why every lookup here is optional.
 */
public class AethericEnchantment {
    public static final ResourceKey<Enchantment> AETHERIC = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aetheric"));

    public static final int MAX_LEVEL = 2;

    /** Resolves the Aetheric enchantment, or empty when no datapack provides it. */
    public static Optional<Holder.Reference<Enchantment>> get(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ENCHANTMENT).get(AETHERIC);
    }
}

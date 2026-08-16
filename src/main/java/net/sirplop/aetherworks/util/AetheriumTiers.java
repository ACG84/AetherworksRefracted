package net.sirplop.aetherworks.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.sirplop.aetherworks.datagen.AWItemTags;

public class AetheriumTiers {
    // 1.21 dropped TierSortingRegistry: a tier is now described by the tag of blocks it
    // canNOT harvest. Aetherium sits above netherite, so it shares netherite's exclusion
    // tag and therefore mines everything netherite does (matching Embers' clockwork tools).
    public static final Tier AETHERIUM = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3841, 9.5f, 2f, 18,
            () -> Ingredient.of(AWItemTags.AETHERIUM_INGOT));
}

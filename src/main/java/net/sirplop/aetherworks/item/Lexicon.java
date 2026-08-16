package net.sirplop.aetherworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class Lexicon extends Item {

    public Lexicon(Properties pProperties) {
        super(pProperties);
    }
    public static final String LEXICON_ITEM = "lexicon_item";
    public static final String LEXICON_AMOUNT = "lexicon_amount";

    public static ItemStack getStoredItem(ItemStack stack) {
        if (stack.getOrCreateTag().contains(Lexicon.LEXICON_ITEM))
            return ItemStack.of(stack.getOrCreateTag().getCompound(LEXICON_ITEM));
        return ItemStack.EMPTY;
    }
    public static int getStoredItemCount(ItemStack stack) {
        if (stack.getOrCreateTag().contains(Lexicon.LEXICON_AMOUNT))
            return stack.getOrCreateTag().getInt(LEXICON_AMOUNT);
        return 0;
    }
    public static void setStoredItem(ItemStack lexicon, ItemStack toSet, int count) {
        lexicon.getOrCreateTag().put(LEXICON_ITEM, toSet.serializeNBT(registries));
        lexicon.getOrCreateTag().putInt(LEXICON_AMOUNT, count);
    }
    public static int setStoredAmount(ItemStack lexicon, int count) {
        int val = Math.max(0, count);
        lexicon.getOrCreateTag().putInt(LEXICON_AMOUNT, val);
        return val;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        ItemStack stored = getStoredItem(stack);
        if (stored.isEmpty())
            return;
        tooltip.add(stored.getDisplayName().copy().withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("  x" + getStoredItemCount(stack)).withStyle(ChatFormatting.GRAY));
    }
}

package net.sirplop.aetherworks.recipe;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingInput;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.item.AetherCrownItem;

import javax.annotation.Nullable;

public class PotionGemUnsocketRecipe implements CraftingRecipe {

    public static final Serializer SERIALIZER = new Serializer();


    public PotionGemUnsocketRecipe() {
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        ItemStack crown = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof AetherCrownItem) {
                if (!crown.isEmpty() || !AetherCrownItem.hasAttachedGem(stack))
                    return false;
                crown = stack;
                continue;
            }
            if (!stack.isEmpty())
                return false;
        }
        return !crown.isEmpty() && container.size() >= 1;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack crownStack = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); i++) {
            if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof AetherCrownItem) {
                crownStack = container.getItem(i).copy();
            }
        }
        if (!crownStack.isEmpty()) {
            AetherCrownItem.detachGem(crownStack);
        }
        return crownStack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput container) {
        NonNullList<ItemStack> gems = NonNullList.withSize(container.size(), ItemStack.EMPTY);
        int index = 0;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof AetherCrownItem) {
                    if (!AetherCrownItem.getAttachedGem(stack).isEmpty()) {
                        gems.set(index, AetherCrownItem.getAttachedGem(stack));
                        index++;
                    }
                }
            }
        }
        return gems;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return new ItemStack(AWRegistry.AETHER_CROWN.get());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    public static class Serializer implements RecipeSerializer<PotionGemUnsocketRecipe> {
        //These recipes have no serialised data, so both codecs are constants.
        private static final MapCodec<PotionGemUnsocketRecipe> CODEC = MapCodec.unit(PotionGemUnsocketRecipe::new);
        private static final StreamCodec<RegistryFriendlyByteBuf, PotionGemUnsocketRecipe> STREAM_CODEC = StreamCodec.unit(new PotionGemUnsocketRecipe());

        @Override
        public @NotNull MapCodec<PotionGemUnsocketRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, PotionGemUnsocketRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

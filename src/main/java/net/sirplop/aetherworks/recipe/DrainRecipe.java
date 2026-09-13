package net.sirplop.aetherworks.recipe;

import net.minecraft.core.HolderLookup;

import net.sirplop.aetherworks.AWDataComponents;
import com.google.gson.JsonObject;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.sirplop.aetherworks.AWConfig;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.item.tool.PrismarineShovel;
import net.sirplop.aetherworks.network.MessageFluidSync;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DrainRecipe implements CraftingRecipe {
    public static final DrainRecipe.Serializer SERIALIZER = new DrainRecipe.Serializer();


    public DrainRecipe() {
    }


    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        ItemStack shovel = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof PrismarineShovel) {
                    if (!shovel.isEmpty())
                        return false; //too many gems
                    shovel = stack;
                } else {
                    return false;
                }
            }
        }
        return !shovel.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack shovel = ItemStack.EMPTY;

        for (int i = 0; i < container.size(); i++) {
            if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof PrismarineShovel) {
                shovel = container.getItem(i).copyWithCount(1);
                break;
            }
        }
        if (!shovel.isEmpty()) {
            MessageFluidSync.setFluid(new FluidHandlerItemStack(AWDataComponents.FLUID_CONTENT, shovel, AWConfig.PRISMARINE_SHOVEL_CAPACITY.get() * 1000), FluidStack.EMPTY);
            return shovel;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
        return new ItemStack(AWRegistry.SHOVEL_PRISMARINE.get());
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

    public static class Serializer implements RecipeSerializer<DrainRecipe> {
        //These recipes have no serialised data, so both codecs are constants.
        private static final MapCodec<DrainRecipe> CODEC = MapCodec.unit(DrainRecipe::new);
        //StreamCodec.unit asserts the encoded value equals the instance handed to it, and these
        //recipes have no equals(), so syncing threw on join. Nothing needs writing, so encode is
        //a no-op and decode just builds a fresh instance.
        private static final StreamCodec<RegistryFriendlyByteBuf, DrainRecipe> STREAM_CODEC =
                StreamCodec.of((buf, recipe) -> {}, buf -> new DrainRecipe());

        @Override
        public @NotNull MapCodec<DrainRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, DrainRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

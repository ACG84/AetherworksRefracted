package net.sirplop.aetherworks.recipe;

import net.sirplop.aetherworks.AWDataComponents;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
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

    public final ResourceLocation id;

    public DrainRecipe(ResourceLocation id) {
        this.id = id;
    }


    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.EQUIPMENT;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack shovel = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
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
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack shovel = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof PrismarineShovel) {
                shovel = container.getItem(i).copyWithCount(1);
                break;
            }
        }
        if (!shovel.isEmpty()) {
            MessageFluidSync.setFluid(new FluidHandlerItemStack(AWDataComponents.FLUID_CONTENT.get(), shovel, AWConfig.PRISMARINE_SHOVEL_CAPACITY.get() * 1000), FluidStack.EMPTY);
            return shovel;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<DrainRecipe> {

        @Override
        public @NotNull DrainRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            return new DrainRecipe(recipeId);
        }

        @Override
        public @Nullable DrainRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            return new DrainRecipe(recipeId);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DrainRecipe recipe) { }
    }
}

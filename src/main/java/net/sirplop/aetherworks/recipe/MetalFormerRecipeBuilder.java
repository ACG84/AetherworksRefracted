package net.sirplop.aetherworks.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.recipe.FluidIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.sirplop.aetherworks.AWRegistry;

import java.util.function.Consumer;

public class MetalFormerRecipeBuilder {

    public ResourceLocation id;
    public Ingredient input = Ingredient.EMPTY;
    public FluidIngredient fluid = FluidIngredient.EMPTY;
    public int temperature = 0;
    public int craftTime = 300;
    public Either<ItemStack, MetalFormerRecipe.TagAmount> output;
    public boolean matchExactly = false;

    public static MetalFormerRecipeBuilder create(ItemStack itemStack) {
        MetalFormerRecipeBuilder builder = new MetalFormerRecipeBuilder();
        builder.output = Either.left(itemStack);
        builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        return builder;
    }
    public static MetalFormerRecipeBuilder create(Item item) {
        return create(new ItemStack(item));
    }
    public static MetalFormerRecipeBuilder create(TagKey<Item> tag, int amount) {
        MetalFormerRecipeBuilder builder = new MetalFormerRecipeBuilder();
        builder.output = Either.right(new MetalFormerRecipe.TagAmount(tag, amount));
        builder.id = tag.location();
        return builder;
    }

    public static MetalFormerRecipeBuilder create(TagKey<Item> tag) {
        return create(tag, 1);
    }
    public MetalFormerRecipeBuilder id(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public MetalFormerRecipeBuilder domain(String domain) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
        return this;
    }

    public MetalFormerRecipeBuilder id(String domain, String id) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, id);
        return this;
    }

    public MetalFormerRecipeBuilder folder(String folder) {
        this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
        return this;
    }

    public MetalFormerRecipeBuilder input(Ingredient input) {
        this.input = input;
        return this;
    }

    public MetalFormerRecipeBuilder input(ItemLike... input) {
        input(Ingredient.of(input));
        return this;
    }

    public MetalFormerRecipeBuilder input(TagKey<Item> tag) {
        input(Ingredient.of(tag));
        return this;
    }

    public MetalFormerRecipeBuilder mustMatchExactly() {
        matchExactly = true;
        return this;
    }

    public MetalFormerRecipeBuilder fluid(FluidIngredient fluid) {
        this.fluid = fluid;
        return this;
    }

    public MetalFormerRecipeBuilder fluid(Fluid fluid, int amount) {
        fluid(FluidIngredient.of(fluid, amount));
        return this;
    }

    public MetalFormerRecipeBuilder fluid(FluidStack stack) {
        fluid(FluidIngredient.of(stack));
        return this;
    }

    public MetalFormerRecipeBuilder fluid(TagKey<Fluid> fluid, int amount) {
        fluid(FluidIngredient.of(fluid, amount));
        return this;
    }

    public MetalFormerRecipeBuilder fluid(FluidIngredient... ingredients) {
        fluid(FluidIngredient.of(ingredients));
        return this;
    }

    public MetalFormerRecipeBuilder temperature(int temperature) {
        this.temperature = temperature;
        return this;
    }
    public MetalFormerRecipeBuilder craftTime(int craftTime) {
        this.craftTime = craftTime;
        return this;
    }
    public MetalFormerRecipe build() {
        return new MetalFormerRecipe(id, input, fluid, temperature, craftTime, output, matchExactly);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new Finished(build()));
    }

    public static class Finished implements FinishedRecipe {

        public final MetalFormerRecipe recipe;

        public Finished(MetalFormerRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonObject outputJson = new JsonObject();
            if (recipe.output.right().isPresent()) {
                outputJson.addProperty("tag", recipe.output.right().get().tag.location().toString());
            } else {
                outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(recipe.output.left().get().getItem()).toString());
                if (recipe.output.left().get().hasTag()) {
                    outputJson.addProperty("nbt", recipe.output.left().get().getTag().toString());
                }
            }
            json.add("output", outputJson);

            if (!recipe.input.isEmpty())
                json.add("input", recipe.input.toJson());
            if (recipe.fluid != FluidIngredient.EMPTY)
                json.add("fluid", recipe.fluid.serialize());
            json.addProperty("temperature", recipe.temperature);
            json.addProperty("craft_time", recipe.craftTime);
            json.addProperty("match_exactly", recipe.matchExactly);
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return AWRegistry.METAL_FORMING_SERIALIZER.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}

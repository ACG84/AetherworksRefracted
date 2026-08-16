package net.sirplop.aetherworks.recipe;

import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ToolStationRecipeBuilder {

    public ResourceLocation id;
    public List<Ingredient> inputs;
    public int temperature = 0;
    public double temperatureRate = 0;
    public ItemStack output;

    public static ToolStationRecipeBuilder create(ItemStack itemStack) {
        ToolStationRecipeBuilder builder = new ToolStationRecipeBuilder();
        builder.inputs = new ArrayList<>(5);
        for (int i = 0; i < 5; i++)
            builder.inputs.add(Ingredient.EMPTY);
        builder.output = itemStack;
        builder.id = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        return builder;
    }
    public static ToolStationRecipeBuilder create(Item item) {
        return create(new ItemStack(item));
    }

    public ToolStationRecipeBuilder id(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public ToolStationRecipeBuilder domain(String domain) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
        return this;
    }

    public ToolStationRecipeBuilder id(String domain, String id) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, id);
        return this;
    }

    public ToolStationRecipeBuilder folder(String folder) {
        this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
        return this;
    }

    public ToolStationRecipeBuilder input(Ingredient i1, Ingredient i2, Ingredient i3, Ingredient i4, Ingredient i5) {
        input(0, i1 == null ? Ingredient.EMPTY : i1);
        input(1, i2 == null ? Ingredient.EMPTY : i2);
        input(2, i3 == null ? Ingredient.EMPTY : i3);
        input(3, i4 == null ? Ingredient.EMPTY : i4);
        input(4, i5 == null ? Ingredient.EMPTY : i5);
        return this;
    }

    public ToolStationRecipeBuilder input(int slot, Ingredient input) {
        inputs.set(slot, input);
        return this;
    }

    public ToolStationRecipeBuilder input(int slot, ItemLike input) {
        input(slot, Ingredient.of(input));
        return this;
    }

    public ToolStationRecipeBuilder input(int slot, TagKey<Item> tag) {
        input(slot, Ingredient.of(tag));
        return this;
    }

    public ToolStationRecipeBuilder temperature(int temperature) {
        this.temperature = temperature;
        return this;
    }
    public ToolStationRecipeBuilder temperatureRate(double temperatureRate) {
        this.temperatureRate = temperatureRate;
        return this;
    }

    public ToolStationRecipe build() {
        return new ToolStationRecipe(id, inputs, temperature, temperatureRate, output);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new ToolStationRecipeBuilder.Finished(build()));
    }

    public static class Finished implements FinishedRecipe {

        public final ToolStationRecipe recipe;

        public Finished(ToolStationRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(recipe.output.getItem()).toString());
            json.add("output", outputJson);

            JsonArray inputs = new JsonArray();
            for (Ingredient input : recipe.inputs) {
                inputs.add(input.toJson());
            }
            json.add("inputs", inputs);
            json.addProperty("temperature", recipe.temperature);
            json.addProperty("temperature_rate", recipe.temperatureRate);
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return AWRegistry.TOOL_STATION_SERIALIZER.get();
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

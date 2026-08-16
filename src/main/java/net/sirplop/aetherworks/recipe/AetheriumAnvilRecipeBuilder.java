package net.sirplop.aetherworks.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.util.WeightedList;

import java.util.function.Consumer;

public class AetheriumAnvilRecipeBuilder {
    public ResourceLocation id;
    public Ingredient input = Ingredient.EMPTY;
    public int temperatureMin;
    public int temperatureMax;
    public int difficulty;
    public int emberPerHit;
    public int numberOfHits;
    public WeightedList<Either<ItemStack, TagKey<Item>>> output;

    public static AetheriumAnvilRecipeBuilder create(ItemStack input) {
        AetheriumAnvilRecipeBuilder builder = new AetheriumAnvilRecipeBuilder();
        builder.output = new WeightedList<>();
        builder.input = Ingredient.of(input);
        builder.id = ForgeRegistries.ITEMS.getKey(input.getItem());
        return builder;
    }
    public static AetheriumAnvilRecipeBuilder create(ItemLike item) {
        return create(new ItemStack(item));
    }
    public static AetheriumAnvilRecipeBuilder create(TagKey<Item> tag) {
        AetheriumAnvilRecipeBuilder builder = new AetheriumAnvilRecipeBuilder();
        builder.input = Ingredient.of(tag);
        builder.id = tag.location();
        return builder;
    }
    public AetheriumAnvilRecipeBuilder id(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public AetheriumAnvilRecipeBuilder domain(String domain) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
        return this;
    }

    public AetheriumAnvilRecipeBuilder id(String domain, String id) {
        this.id = ResourceLocation.fromNamespaceAndPath(domain, id);
        return this;
    }
    public AetheriumAnvilRecipeBuilder folder(String folder) {
        this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
        return this;
    }

    public AetheriumAnvilRecipeBuilder temperatureRange(int min, int max) {
        this.temperatureMin = min;
        this.temperatureMax = max;
        return this;
    }
    public AetheriumAnvilRecipeBuilder hitInfo(int numberOfHits, int emberPerHit) {
        this.numberOfHits = numberOfHits;
        this.emberPerHit = emberPerHit;
        return this;
    }
    public AetheriumAnvilRecipeBuilder difficulty(int difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public AetheriumAnvilRecipeBuilder result(ItemStack stack, double chance) {
        this.output.add(Either.left(stack), chance);
        return this;
    }
    public AetheriumAnvilRecipeBuilder result(ItemLike item, double chance) {
        this.output.add(Either.left(new ItemStack(item)), chance);
        return this;
    }
    public AetheriumAnvilRecipeBuilder result(TagKey<Item> tag, double chance) {
        this.output.add(Either.right(tag), chance);
        return this;
    }

    public AetheriumAnvilRecipe build() {
        return new AetheriumAnvilRecipe(id, input, temperatureMin, temperatureMax, difficulty, emberPerHit, numberOfHits, output);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new Finished(build()));
    }

    public static class Finished implements FinishedRecipe {

        public final AetheriumAnvilRecipe recipe;

        public Finished(AetheriumAnvilRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray outputJson = new JsonArray();
            for (Pair<Either<ItemStack, TagKey<Item>>, Double> pair : recipe.output.internalList) {
                JsonObject entry = new JsonObject();
                if (pair.getFirst().right().isPresent()) {
                    entry.addProperty("tag", pair.getFirst().right().get().location().toString());
                } else {
                    entry.addProperty("item", ForgeRegistries.ITEMS.getKey(pair.getFirst().left().get().getItem()).toString());
                    entry.addProperty("count", pair.getFirst().left().get().getCount());
                }
                entry.addProperty("chance", pair.getSecond());
                outputJson.add(entry);
            }
            json.add("result", outputJson);

            if (!recipe.input.isEmpty())
                json.add("input", recipe.input.toJson());

            json.addProperty("temperatureMin", recipe.temperatureMin);
            json.addProperty("temperatureMax", recipe.temperatureMax);
            json.addProperty("difficulty", recipe.difficulty);
            json.addProperty("emberPerHit", recipe.emberPerHit);
            json.addProperty("numberOfHits", recipe.numberOfHits);
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return AWRegistry.AETHERIUM_ANVIL_SERIALIZER.get();
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

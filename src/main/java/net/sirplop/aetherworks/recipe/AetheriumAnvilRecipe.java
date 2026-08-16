package net.sirplop.aetherworks.recipe;

import net.minecraft.core.HolderLookup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.rekindled.embers.util.Misc;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sirplop.aetherworks.util.WeightedList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AetheriumAnvilRecipe implements IAetheriumAnvilRecipe {
    public static final Serializer SERIALIZER = new Serializer();

    public final Ingredient input;
    public final int temperatureMin;
    public final int temperatureMax;
    public final int difficulty;
    public final int emberPerHit;
    public final int numberOfHits;

    public final WeightedList<Either<ItemStack, TagKey<Item>>> output;

    public AetheriumAnvilRecipe(Ingredient input, int temperatureMin, int temperatureMax,
                                int difficulty, int emberPerHit, int numberOfHits, WeightedList<Either<ItemStack, TagKey<Item>>> list) {
        this.input = input;
        this.output = list;

        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.difficulty = difficulty;
        this.emberPerHit = emberPerHit;
        this.numberOfHits = numberOfHits;
    }

    @Override
    public ItemStack getOutput(RecipeWrapper context) {
        return getResultItem();
    }

    @Override
    public ItemStack getResultItem() {
        var result = output.choose();
        if (result.left().isPresent())
            return result.left().get().copy();
        return new ItemStack(Misc.getTaggedItem(result.right().get()), 1);
    }

    @Override
    public List<ItemStack> getAllResults() {
        List<ItemStack> ret = new ArrayList<>();
        for (var pair : output.internalList) {
            if (pair.getFirst().right().isPresent()) {
                ret.add(new ItemStack(Misc.getTaggedItem(pair.getFirst().right().get())));
            } else {
                ret.add(pair.getFirst().left().get());
            }
        }
        return ret;
    }

    @Override
    public Ingredient getDisplayInput() {
        return input;
    }

    @Override
    public int getTemperatureMin() {
        return temperatureMin;
    }

    @Override
    public int getTemperatureMax() {
        return temperatureMax;
    }

    @Override
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public int getEmberPerHit() {
        return emberPerHit;
    }

    @Override
    public int getNumberOfHits() {
        return numberOfHits;
    }

    @Override
    public boolean matches(AetheriumAnvilContext context, Level level) {
        for (int i = 0; i < context.size(); i++) {
            if (input.test(context.getItem(i))) {
                if (context.temperature >= this.temperatureMin &&
                    context.temperature <= this.temperatureMax) {
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(AetheriumAnvilContext context, HolderLookup.Provider registryAccess) {
        for (int i = 0; i < context.size(); i++) {
            if (input.test(context.getItem(i))) {
                context.items.extractItem(i, 1, false);
                break;
            }
        }
        return this.getOutput(context);
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
    public static class Serializer implements RecipeSerializer<AetheriumAnvilRecipe> {

        //Each output entry is either a concrete stack or a tag, with its weight alongside.
        private static final Codec<Pair<Either<ItemStack, TagKey<Item>>, Double>> ENTRY_CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.mapEither(ItemStack.CODEC.fieldOf("result"),
                                TagKey.codec(Registries.ITEM).fieldOf("tag")).forGetter(Pair::getFirst),
                        Codec.DOUBLE.fieldOf("chance").forGetter(Pair::getSecond)
                ).apply(instance, Pair::of));

        private static final Codec<WeightedList<Either<ItemStack, TagKey<Item>>>> OUTPUT_CODEC =
                ENTRY_CODEC.listOf().xmap(AetheriumAnvilRecipe::toWeightedList, list -> list.internalList);

        private static final MapCodec<AetheriumAnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.optionalFieldOf("input", Ingredient.EMPTY).forGetter(r -> r.input),
                Codec.INT.fieldOf("temperatureMin").forGetter(r -> r.temperatureMin),
                Codec.INT.fieldOf("temperatureMax").forGetter(r -> r.temperatureMax),
                Codec.INT.fieldOf("difficulty").forGetter(r -> r.difficulty),
                Codec.INT.fieldOf("emberPerHit").forGetter(r -> r.emberPerHit),
                Codec.INT.fieldOf("numberOfHits").forGetter(r -> r.numberOfHits),
                OUTPUT_CODEC.fieldOf("result").forGetter(r -> r.output)
        ).apply(instance, AetheriumAnvilRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, Pair<Either<ItemStack, TagKey<Item>>, Double>> ENTRY_STREAM =
                StreamCodec.composite(
                        ByteBufCodecs.either(ItemStack.STREAM_CODEC,
                                ResourceLocation.STREAM_CODEC.map(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location)), Pair::getFirst,
                        ByteBufCodecs.DOUBLE, Pair::getSecond,
                        Pair::of);

        //StreamCodec.composite tops out at six components and this recipe has seven,
        //so the buffer is read and written by hand.
        private static final StreamCodec<RegistryFriendlyByteBuf, AetheriumAnvilRecipe> STREAM_CODEC =
                StreamCodec.of((buf, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
                    buf.writeVarInt(recipe.temperatureMin);
                    buf.writeVarInt(recipe.temperatureMax);
                    buf.writeVarInt(recipe.difficulty);
                    buf.writeVarInt(recipe.emberPerHit);
                    buf.writeVarInt(recipe.numberOfHits);
                    buf.writeVarInt(recipe.output.internalList.size());
                    for (Pair<Either<ItemStack, TagKey<Item>>, Double> entry : recipe.output.internalList) {
                        ENTRY_STREAM.encode(buf, entry);
                    }
                }, buf -> {
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int temperatureMin = buf.readVarInt();
                    int temperatureMax = buf.readVarInt();
                    int difficulty = buf.readVarInt();
                    int emberPerHit = buf.readVarInt();
                    int numberOfHits = buf.readVarInt();
                    int size = buf.readVarInt();
                    List<Pair<Either<ItemStack, TagKey<Item>>, Double>> entries = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        entries.add(ENTRY_STREAM.decode(buf));
                    }
                    return new AetheriumAnvilRecipe(input, temperatureMin, temperatureMax, difficulty,
                            emberPerHit, numberOfHits, toWeightedList(entries));
                });

        @Override
        public MapCodec<AetheriumAnvilRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AetheriumAnvilRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    private static WeightedList<Either<ItemStack, TagKey<Item>>> toWeightedList(List<Pair<Either<ItemStack, TagKey<Item>>, Double>> entries) {
        WeightedList<Either<ItemStack, TagKey<Item>>> list = new WeightedList<>();
        for (Pair<Either<ItemStack, TagKey<Item>>, Double> entry : entries) {
            list.add(entry.getFirst(), entry.getSecond());
        }
        return list;
    }
}

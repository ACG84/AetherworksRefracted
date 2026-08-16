package net.sirplop.aetherworks.recipe;

import net.minecraft.core.HolderLookup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.sirplop.aetherworks.Aetherworks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolStationRecipe implements IToolStationRecipe{
    public static final Serializer SERIALIZER = new Serializer();

    public final List<Ingredient> inputs;
    public final int temperature;
    public final double temperatureRate;

    public final ItemStack output;

    public ToolStationRecipe(List<Ingredient> inputs,  int temperature, double temperatureRate, ItemStack output) {
        this.inputs = inputs;
        this.temperature = temperature;
        this.temperatureRate = temperatureRate;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeWrapper context, Level level) {
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(context.getItem(i)))
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getOutput(RecipeWrapper context) {
        return getResultItem();
    }

    @Override
    public ItemStack assemble(RecipeWrapper context, HolderLookup.Provider registry) {
        //RecipeWrapper is a plain RecipeInput in 1.21, so consume through the item handler.
        if (context instanceof ToolStationContext station) {
            for (int i = 0; i < inputs.size(); i++) {
                if (inputs.get(i).test(context.getItem(i))) {
                    station.items.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
        return this.getOutput(context);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public List<Ingredient> getDisplayInputs() {
        return inputs;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }
    @Override
    public double getTemperatureRate() {
        return temperatureRate;
    }

    public static class Serializer implements RecipeSerializer<ToolStationRecipe> {

        private static final MapCodec<ToolStationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().optionalFieldOf("inputs", List.of()).forGetter(r -> r.inputs),
                Codec.INT.fieldOf("temperature").forGetter(r -> r.temperature),
                Codec.DOUBLE.fieldOf("temperature_rate").forGetter(r -> r.temperatureRate),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(instance, ToolStationRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ToolStationRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.inputs,
                ByteBufCodecs.VAR_INT, r -> r.temperature,
                ByteBufCodecs.DOUBLE, r -> r.temperatureRate,
                ItemStack.STREAM_CODEC, r -> r.output,
                ToolStationRecipe::new);

        @Override
        public MapCodec<ToolStationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ToolStationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

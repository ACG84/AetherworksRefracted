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
import com.rekindled.embers.recipe.FluidIngredient;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;

public class MetalFormerRecipe implements IMetalFormerRecipe{
    public static final Serializer SERIALIZER = new Serializer();

    public final Ingredient input;
    public final FluidIngredient fluid;
    public final int temperature;
    public final int craftTime;
    public final boolean matchExactly;

    public final Either<ItemStack, TagAmount> output;

    public MetalFormerRecipe(Ingredient input, FluidIngredient fluid, int temperature, int craftTime, TagAmount output,  boolean matchExactly) {
        this(input, fluid, temperature, craftTime, Either.right(output), matchExactly);
    }
    public MetalFormerRecipe(Ingredient input, FluidIngredient fluid, int temperature, int craftTime, ItemStack output, boolean matchExactly) {
        this(input, fluid, temperature, craftTime, Either.left(output), matchExactly);
    }
    public MetalFormerRecipe(Ingredient input, FluidIngredient fluid, int temperature, int craftTime, Either<ItemStack, TagAmount> output,  boolean matchExactly) {
        this.input = input;
        this.fluid = fluid;
        this.temperature = temperature;
        this.craftTime = craftTime;
        this.output = output;
        this.matchExactly = matchExactly;
    }

    @Override
    public boolean matches(MetalFormerContext context, Level pLevel) {
        if (context.temperature >= this.temperature && input.test(context.getItem(0))
                && (!matchExactly || ItemStack.isSameItemSameComponents(input.getItems()[0], context.getItem(0)))) {
            return fluid.test(context.fluids.getFluidInTank(0));
        }
        return false;
    }

    @Override
    public ItemStack getOutput(RecipeWrapper context) {
        return getResultItem();
    }

    @Override
    public ItemStack assemble(MetalFormerContext context, HolderLookup.Provider registry) {
        for (int i = 0; i < context.size(); i++) {
            if (input.test(context.getItem(i))) {
                context.items.extractItem(i, 1, false);
                break;
            }
        }
        for (FluidStack stack : fluid.getFluids()) {
            if (fluid.test(context.fluids.drain(stack, IFluidHandler.FluidAction.SIMULATE))) {
                context.fluids.drain(stack, IFluidHandler.FluidAction.EXECUTE);
                break;
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
        if (output.left().isPresent())
            return output.left().get().copy();
        return new ItemStack(Misc.getTaggedItem(output.right().get().tag), output.right().get().amount);
    }
    @Override
    public FluidIngredient getDisplayInputFluid() {
        return fluid;
    }

    @Override
    public Ingredient getDisplayInput() {
        return input;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }
    @Override
    public int getCraftTime() {
        return craftTime;
    }

    public static class TagAmount {
        public TagKey<Item> tag;
        public int amount;

        public TagAmount(TagKey<Item> tag, int amount) {
            this.tag = tag;
            this.amount = amount;
        }
    }
    public static class Serializer implements RecipeSerializer<MetalFormerRecipe> {

        //TagAmount is written as {"tag": ..., "count": ...}, matching the old hand-rolled JSON.
        private static final Codec<TagAmount> TAG_AMOUNT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(t -> t.tag),
                Codec.INT.optionalFieldOf("count", 1).forGetter(t -> t.amount)
        ).apply(instance, TagAmount::new));

        private static final Codec<Either<ItemStack, TagAmount>> OUTPUT_CODEC =
                Codec.either(ItemStack.CODEC, TAG_AMOUNT_CODEC);

        private static final MapCodec<MetalFormerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.optionalFieldOf("input", Ingredient.EMPTY).forGetter(r -> r.input),
                AWRecipeCodecs.FLUID_INGREDIENT.optionalFieldOf("fluid", FluidIngredient.EMPTY).forGetter(r -> r.fluid),
                Codec.INT.fieldOf("temperature").forGetter(r -> r.temperature),
                Codec.INT.fieldOf("craft_time").forGetter(r -> r.craftTime),
                OUTPUT_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.BOOL.fieldOf("match_exactly").forGetter(r -> r.matchExactly)
        ).apply(instance, MetalFormerRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TagAmount> TAG_AMOUNT_STREAM = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC.map(loc -> TagKey.create(Registries.ITEM, loc), TagKey::location), t -> t.tag,
                ByteBufCodecs.VAR_INT, t -> t.amount,
                TagAmount::new);

        private static final StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
                AWRecipeCodecs.FLUID_INGREDIENT_STREAM, r -> r.fluid,
                ByteBufCodecs.VAR_INT, r -> r.temperature,
                ByteBufCodecs.VAR_INT, r -> r.craftTime,
                ByteBufCodecs.either(ItemStack.STREAM_CODEC, TAG_AMOUNT_STREAM), r -> r.output,
                ByteBufCodecs.BOOL, r -> r.matchExactly,
                MetalFormerRecipe::new);

        @Override
        public MapCodec<MetalFormerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

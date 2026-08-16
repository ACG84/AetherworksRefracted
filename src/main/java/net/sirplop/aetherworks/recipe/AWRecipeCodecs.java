package net.sirplop.aetherworks.recipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.rekindled.embers.recipe.FluidIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

/**
 * Shared codecs for the Aetherworks recipe serializers.
 * <p>
 * 1.21 requires recipe serializers to expose a {@code MapCodec} and a {@code StreamCodec}, but
 * Embers' {@link FluidIngredient} is still Gson/FriendlyByteBuf based. These adapters bridge the
 * two so the recipe JSON on disk keeps its existing shape.
 */
public final class AWRecipeCodecs {

    private AWRecipeCodecs() {
    }

    /**
     * FluidIngredient serialises itself to a JsonElement, so the codec round-trips through
     * {@link ExtraCodecs#JSON} rather than describing the fields directly.
     */
    public static final Codec<FluidIngredient> FLUID_INGREDIENT = ExtraCodecs.JSON.xmap(
            json -> FluidIngredient.deserialize(json, "fluid"),
            FluidIngredient::serialize);

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> FLUID_INGREDIENT_STREAM =
            StreamCodec.of((buf, ingredient) -> ingredient.write(buf), FluidIngredient::read);
}

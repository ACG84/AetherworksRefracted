package net.sirplop.aetherworks.recipe;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.util.WeightedList;

import java.util.List;

/**
 * Round-trips every recipe serializer's StreamCodec the way the server does when it sends
 * clientbound/update_recipes on join - a failure there is otherwise invisible until a player
 * connects. Opt in with -Daetherworks.codecSelfTest=true; results are logged as AW-CODEC-SELFTEST.
 */
public class AWCodecSelfTest {

    public static void run(RegistryAccess registries) {
        int failures = 0;
        failures += check("DrainRecipe", DrainRecipe.SERIALIZER, new DrainRecipe(), registries);
        failures += check("PotionGemSocketRecipe", PotionGemSocketRecipe.SERIALIZER, new PotionGemSocketRecipe(), registries);
        failures += check("PotionGemUnsocketRecipe", PotionGemUnsocketRecipe.SERIALIZER, new PotionGemUnsocketRecipe(), registries);
        failures += check("PotionGemImbueRecipe", PotionGemImbueRecipe.SERIALIZER, new PotionGemImbueRecipe(), registries);
        failures += check("LexiconRecipe", LexiconRecipe.SERIALIZER, new LexiconRecipe(), registries);

        failures += check("MetalFormerRecipe", MetalFormerRecipe.SERIALIZER,
                new MetalFormerRecipe(Ingredient.of(Items.DIAMOND), com.rekindled.embers.recipe.FluidIngredient.EMPTY,
                        2600, 300, new ItemStack(Items.DIAMOND, 2), false), registries);
        failures += check("ToolStationRecipe", ToolStationRecipe.SERIALIZER,
                new ToolStationRecipe(List.of(Ingredient.of(Items.DIAMOND)), 2500, 10.0, new ItemStack(Items.STICK)), registries);

        WeightedList<Either<ItemStack, net.minecraft.tags.TagKey<net.minecraft.world.item.Item>>> out = new WeightedList<>();
        out.add(Either.left(new ItemStack(Items.RAW_GOLD)), 10.0);
        failures += check("AetheriumAnvilRecipe", AetheriumAnvilRecipe.SERIALIZER,
                new AetheriumAnvilRecipe(Ingredient.of(Items.DIAMOND), 700, 2100, 1, 10, 1, out), registries);

        if (failures == 0) {
            Aetherworks.LOGGER.info("AW-CODEC-SELFTEST: ALL STREAM CODECS ROUND-TRIPPED OK");
        } else {
            Aetherworks.LOGGER.error("AW-CODEC-SELFTEST: {} FAILURE(S)", failures);
        }
    }

    private static <T extends Recipe<?>> int check(String name, RecipeSerializer<T> serializer, T recipe, RegistryAccess registries) {
        try {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
            serializer.streamCodec().encode(buf, recipe);
            int written = buf.writerIndex();
            T back = serializer.streamCodec().decode(buf);
            if (back == null) throw new IllegalStateException("decoded null");
            Aetherworks.LOGGER.info("AW-CODEC-SELFTEST: OK   {} ({} bytes)", name, written);
            return 0;
        } catch (Throwable t) {
            Aetherworks.LOGGER.error("AW-CODEC-SELFTEST: FAIL {} -> {}", name, t.toString());
            return 1;
        }
    }
}

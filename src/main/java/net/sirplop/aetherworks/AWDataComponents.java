package net.sirplop.aetherworks;

import net.minecraft.core.component.DataComponents;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item data moved from free-form NBT to typed data components in 1.20.5, so every piece of
 * per-stack state this mod used to stash under a string key now needs a registered component.
 * The keys below deliberately match the old NBT names so existing worlds keep reading sensibly.
 */
public class AWDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Aetherworks.MODID);

    /** Current mode of a toggleable tool (was the "aw.toggle" byte). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> TOGGLE_MODE =
            COMPONENTS.registerComponentType("toggle_mode", builder -> builder
                    .persistent(Codec.BYTE)
                    .networkSynchronized(ByteBufCodecs.BYTE));

    /** Stack a tool is currently focused on (was the "aw.focus" compound). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> FOCUS =
            COMPONENTS.registerComponentType("focus", builder -> builder
                    .persistent(ItemStack.CODEC)
                    .networkSynchronized(ItemStack.STREAM_CODEC));

    /** Gem socketed into the Aether Crown (was the "gem" compound). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> CROWN_GEM =
            COMPONENTS.registerComponentType("crown_gem", builder -> builder
                    .persistent(ItemStack.CODEC)
                    .networkSynchronized(ItemStack.STREAM_CODEC));

    /** Item the Lexicon is filled with, plus how much of it. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStack>> LEXICON_ITEM =
            COMPONENTS.registerComponentType("lexicon_item", builder -> builder
                    .persistent(ItemStack.CODEC)
                    .networkSynchronized(ItemStack.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LEXICON_AMOUNT =
            COMPONENTS.registerComponentType("lexicon_amount", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    /**
     * Fluid held by the Prismarine Shovel. FluidHandlerItemStack is component-backed in 1.21,
     * so it needs a SimpleFluidContent component handed to it rather than writing its own NBT.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT =
            COMPONENTS.registerComponentType("fluid_content", builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    /** Cached display colour of a Potion Gem. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> POTION_COLOR =
            COMPONENTS.registerComponentType("potion_color", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT));
}

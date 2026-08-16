package net.sirplop.aetherworks.item;

import net.sirplop.aetherworks.AWDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.sirplop.aetherworks.AWConfig;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;

import java.util.*;

public class PotionGemItem extends Item {
    public PotionGemItem(Properties pProperties) {
        super(pProperties);
    }
    public static final String POTION_COLOR = "gem_color";
    public static final int DEFAULT_COLOR = 0x0b618f;

    public static int getColor(ItemStack stack) {
        return stack.getOrDefault(AWDataComponents.POTION_COLOR.get(), DEFAULT_COLOR);
    }

    public static List<MobEffectInstance> getEffects(ItemStack stack) {
        //Potion data is a POTION_CONTENTS component now rather than loose NBT.
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        List<MobEffectInstance> effects = new ArrayList<>();
        contents.getAllEffects().forEach(effects::add);
        return effects;
    }

    public void setEffects(ItemStack potion, ItemStack stack) {
        Map<Holder<MobEffect>, Holder<MobEffect>> repl = AWConfig.getPotionGemReplacements();
        PotionContents contents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        List<MobEffectInstance> source = new ArrayList<>();
        contents.getAllEffects().forEach(source::add);

        setEffects(replaceEffects(source, repl), stack);
    }

    public void setEffects(List<MobEffectInstance> effects, ItemStack stack) {
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance inst = effects.get(i);
            effects.set(i, new MobEffectInstance(inst.getEffect(), 200,
                    inst.getAmplifier(), true, inst.isVisible(), inst.showIcon()));
        }
        setEffectsForRecipe(effects, stack, PotionContents.getColor(effects));
    }

    public static void setEffectsForRecipe(List<MobEffectInstance> effects, ItemStack stack, int color) {
        stack.set(DataComponents.POTION_CONTENTS,
                new PotionContents(Optional.empty(), Optional.empty(), List.copyOf(effects)));
        stack.set(AWDataComponents.POTION_COLOR.get(), color);
    }

    public boolean hasEffect(ItemStack stack) {
        return !getEffects(stack).isEmpty();
    }

    private static class EffectHelper {
        List<Holder<MobEffect>> effects;
        List<Integer> levels;

        public EffectHelper(List<MobEffectInstance> instance) {
            effects= new ArrayList<>();
            levels = new ArrayList<>();
            for (MobEffectInstance eff : instance) {
                effects.add(eff.getEffect());
                levels.add(eff.getAmplifier());
            }
        }
        public boolean isEmpty() {
            return effects.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EffectHelper that)) return false;
            return Objects.equals(effects, that.effects) && Objects.equals(levels, that.levels);
        }

        @Override
        public int hashCode() {
            return Objects.hash(effects, levels);
        }
    }

    public static void getAllPotionGems(CreativeModeTab.Output output) {
        Map<Holder<MobEffect>, Holder<MobEffect>> repl = AWConfig.getPotionGemReplacements();
        Set<EffectHelper> dupeCheck = new HashSet<>();
        for (Potion pot : BuiltInRegistries.POTION) {
            List<MobEffectInstance> effects = replaceEffects(pot.getEffects(), repl);
            //check for duplicate (aka same effect level, different duration
            EffectHelper help = new EffectHelper(effects);
            if (help.isEmpty() || dupeCheck.contains(help)) {
                continue;
            }
            dupeCheck.add(help);

            ItemStack stack = new ItemStack(AWRegistry.POTION_GEM.get());
            ((PotionGemItem)stack.getItem()).setEffects(effects, stack);
            output.accept(stack);
        }
    }

    private static List<MobEffectInstance> replaceEffects(List<MobEffectInstance> list, Map<Holder<MobEffect>, Holder<MobEffect>> replacementEffects) {
        List<MobEffectInstance> repl = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MobEffectInstance inst = list.get(i);
            if (replacementEffects.containsKey(inst.getEffect())) {
                repl.add(new MobEffectInstance(replacementEffects.get(inst.getEffect()), 200, inst.getAmplifier(),inst.isAmbient(), inst.isVisible(), inst.showIcon()));
            }
            else
                repl.add(inst);
        }
        return repl;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(Component.translatable(Aetherworks.MODID + ".tooltip.gem_effect").withStyle(ChatFormatting.GRAY));
        addTooltip(getEffects(stack), tooltip);
    }

    public static final MutableComponent NO_EFFECT = Component.literal("  ").append(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));

    public static void addTooltip(List<MobEffectInstance> effects, List<Component> tooltip) {
        MutableComponent component;
        Holder<MobEffect> effect;
        if (effects.isEmpty()) {
            tooltip.add(NO_EFFECT);
        } else {
            for(Iterator<MobEffectInstance> iter = effects.iterator(); iter.hasNext(); tooltip.add(component.withStyle(effect.value().getCategory().getTooltipFormatting()))) {
                MobEffectInstance curEffect = iter.next();
                component = Component.literal("  ").append(Component.translatable(curEffect.getDescriptionId()));
                effect = curEffect.getEffect();
                if (curEffect.getAmplifier() > 0) {
                    component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + curEffect.getAmplifier()));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(ItemStack itemStack, int i) {
            if (i == 0 && itemStack.has(AWDataComponents.POTION_COLOR.get())) {
                return itemStack.get(AWDataComponents.POTION_COLOR.get());
            }
            return 0xFFFFFFFF;
        }
    }
}

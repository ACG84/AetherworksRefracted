package net.sirplop.aetherworks.item;

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
import net.minecraft.world.item.alchemy.PotionUtils;
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
        if (stack.getOrCreateTag().contains(PotionGemItem.POTION_COLOR))
            return stack.getOrCreateTag().getInt(PotionGemItem.POTION_COLOR);
        return DEFAULT_COLOR;
    }

    public static List<MobEffectInstance> getEffects(ItemStack stack) {
        return PotionUtils.getAllEffects(stack.getOrCreateTag());
    }

    public void setEffects(ItemStack potion, ItemStack stack) {
        Map<MobEffect, MobEffect> repl = AWConfig.getPotionGemReplacements();
        List<MobEffectInstance> effects = replaceEffects(PotionUtils.getPotion(potion).getEffects(), repl);
        effects.addAll(replaceEffects(PotionUtils.getCustomEffects(potion), repl));

        setEffects(effects, stack);
    }

    public void setEffects(List<MobEffectInstance> effects, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance inst = effects.get(i);
            effects.set(i, new MobEffectInstance(inst.getEffect(), 200,
                    inst.getAmplifier(), true, inst.isVisible(), inst.showIcon()));
        }
        PotionUtils.setCustomEffects(stack, effects);
        int color = PotionUtils.getColor(effects);
        tag.putInt(POTION_COLOR, color);
    }

    public static void setEffectsForRecipe(List<MobEffectInstance> effects, ItemStack stack, int color) {
        CompoundTag tag = stack.getOrCreateTag();
        PotionUtils.setCustomEffects(stack, effects);
        tag.putInt(POTION_COLOR, color);
    }

    public boolean hasEffect(ItemStack stack) {
        return !getEffects(stack).isEmpty();
    }

    private static class EffectHelper {
        List<MobEffect> effects;
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
        Map<MobEffect, MobEffect> repl = AWConfig.getPotionGemReplacements();
        Set<EffectHelper> dupeCheck = new HashSet<>();
        for (Potion pot : ForgeRegistries.POTIONS) {
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

    private static List<MobEffectInstance> replaceEffects(List<MobEffectInstance> list, Map<MobEffect, MobEffect> replacementEffects) {
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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(Component.translatable(Aetherworks.MODID + ".tooltip.gem_effect").withStyle(ChatFormatting.GRAY));
        addTooltip(PotionUtils.getMobEffects(stack), tooltip);
    }

    public static final MutableComponent NO_EFFECT = Component.literal("  ").append(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));

    public static void addTooltip(List<MobEffectInstance> effects, List<Component> tooltip) {
        MutableComponent component;
        MobEffect effect;
        if (effects.isEmpty()) {
            tooltip.add(NO_EFFECT);
        } else {
            for(Iterator<MobEffectInstance> iter = effects.iterator(); iter.hasNext(); tooltip.add(component.withStyle(effect.getCategory().getTooltipFormatting()))) {
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
            if (i == 0 && itemStack.getOrCreateTag().contains(PotionGemItem.POTION_COLOR)) {
                return itemStack.getOrCreateTag().getInt(PotionGemItem.POTION_COLOR);
            }
            return 0xFFFFFFFF;
        }
    }
}

package net.sirplop.aetherworks.datagen;

import com.rekindled.embers.datagen.EmbersDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sirplop.aetherworks.Aetherworks;

import java.util.concurrent.CompletableFuture;

public class AWDamageTypeTags extends TagsProvider<DamageType> {
    public AWDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Aetherworks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(AWDamageTypes.MOON_EMBER_KEY.location());
        //tag(DamageTypeTags.IS_FIRE).addOptional(AWDamageTypes.MOON_EMBER_KEY.location());
        tag(EmbersDamageTypeTags.HOLY_DAMAGE).addOptional(AWDamageTypes.MOON_EMBER_KEY.location());
        tag(DamageTypeTags.BYPASSES_RESISTANCE).addOptional(AWDamageTypes.MOON_EMBER_KEY.location());
        tag(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL).addOptional(AWDamageTypes.MOON_EMBER_KEY.location());
        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).addOptional(AWDamageTypes.MOON_EMBER_KEY.location()); //time to kill god
    }
}
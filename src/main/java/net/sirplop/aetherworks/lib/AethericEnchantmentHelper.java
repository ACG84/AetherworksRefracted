package net.sirplop.aetherworks.lib;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sirplop.aetherworks.enchantment.AethericEnchantment;
import net.sirplop.aetherworks.util.MoonlightRepair;

import java.util.Optional;

public class AethericEnchantmentHelper {

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        //Aetheric is data-driven now, so bail out entirely when no pack defines it.
        Optional<? extends Holder<Enchantment>> aetheric = AethericEnchantment.get(player.registryAccess());
        if (aetheric.isEmpty())
            return;

        //check if they have any aetheric items in their inventory.
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.items) {
            check(item, player, aetheric.get());
        }
        for (ItemStack item : player.getArmorSlots())
            check(item, player, aetheric.get());
        check(player.getOffhandItem(), player, aetheric.get());
    }

    private static void check(ItemStack item, ServerPlayer player, Holder<Enchantment> aetheric) {
        if (item.isDamageableItem()) { //quick n dirty filter, speeds things up.
            int level = EnchantmentHelper.getItemEnchantmentLevel(aetheric, item);
            if (level > 0) {
                MoonlightRepair.tryRepair(item, player.level(), player, level);
            }
        }
    }
}

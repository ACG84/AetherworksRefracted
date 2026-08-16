package net.sirplop.aetherworks.api.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.sirplop.aetherworks.AWDataComponents;
import net.sirplop.aetherworks.network.MessageToggleItem;
import net.sirplop.aetherworks.network.PacketHandler;

public interface IToggleItem {
    String KEY = "toggleState";

    byte getToggleMax();

    default void toggleItem(ItemStack stack, Player player, byte stateFlag) {
        MessageToggleItem.toggleItem(stack, player, stateFlag);
        PacketDistributor.sendToPlayer((ServerPlayer) player, new MessageToggleItem(stateFlag));
    }

    default byte getToggled(ItemStack stack) {
        if (stack != null)
            return stack.getOrDefault(AWDataComponents.TOGGLE_MODE.get(), (byte) 0);
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    void clientModeChanged(ItemStack stack, Player player, byte oldValue, byte newVal, byte stateFlag);
}

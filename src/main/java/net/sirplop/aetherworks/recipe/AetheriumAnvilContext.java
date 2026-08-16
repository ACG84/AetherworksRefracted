package net.sirplop.aetherworks.recipe;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class AetheriumAnvilContext extends RecipeWrapper {

    public int temperature;
    /** RecipeWrapper is only a RecipeInput in 1.21, so keep the handler for item consumption. */
    public final IItemHandlerModifiable items;

    public AetheriumAnvilContext(IItemHandlerModifiable inv, int temperature) {
        super(inv);
        this.items = inv;
        this.temperature = temperature;
    }
}

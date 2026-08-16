package net.sirplop.aetherworks.recipe;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

/**
 * RecipeWrapper is only a RecipeInput in 1.21 and no longer exposes Container's mutators, so the
 * tool station passes its handler along for recipes that need to consume ingredients.
 */
public class ToolStationContext extends RecipeWrapper {

    public final IItemHandlerModifiable items;

    public ToolStationContext(IItemHandlerModifiable inv) {
        super(inv);
        this.items = inv;
    }
}

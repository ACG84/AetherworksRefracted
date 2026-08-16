package net.sirplop.aetherworks.recipe;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class MetalFormerContext extends RecipeWrapper {

    public IFluidHandler fluids;
    public int temperature;
    /**
     * 1.21's RecipeWrapper is a bare RecipeInput (size/getItem only) rather than a Container,
     * so the handler is kept here for the recipes that need to consume from it.
     */
    public final IItemHandlerModifiable items;

    public MetalFormerContext(IItemHandlerModifiable inv, IFluidHandler fluids, int temperature) {
        super(inv);
        this.items = inv;
        this.fluids = fluids;
        this.temperature = temperature;
    }
}

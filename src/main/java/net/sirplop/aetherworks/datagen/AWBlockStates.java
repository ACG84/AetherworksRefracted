package net.sirplop.aetherworks.datagen;

import com.rekindled.embers.block.MechEdgeBlockBase;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sirplop.aetherworks.AWRegistry;
import net.sirplop.aetherworks.Aetherworks;
import net.sirplop.aetherworks.lib.OctFacingHorizontalProperty;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.function.Function;

public class AWBlockStates extends BlockStateProvider {

    public AWBlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Aetherworks.MODID, exFileHelper);
    }

    public static final Vector3f ZERO = new Vector3f(0, 0, 0);

    public static final Vector3f ROT_FPR = new Vector3f(0, 45, 0);
    public static final Vector3f ROT_TPR = new Vector3f(75, 45, 0);
    public static final Vector3f ROT_GUI = new Vector3f(30, 225, 0);

    public static final Vector3f TRANS_TPR = new Vector3f(0, 2.5f, 0);
    public static final Vector3f TRANS_GROUND = new Vector3f(0, 3, 0);

    public static final float SCALE_FPR = 0.4f;
    public static final float SCALE_TPR = 0.375f;
    public static final float SCALE_GUI = 0.625f;
    public static final float SCALE_GROUND = 0.25f;
    public static final float SCALE_FIXED = 0.5f;

    @Override
    protected void registerStatesAndModels() {
        //this is just to give them proper particles
        for (AWRegistry.FluidStuff fluid : AWRegistry.fluidList) {
            fluid(fluid.FLUID_BLOCK, fluid.name);
        }
        blockWithItem(AWRegistry.SUEVITE);
        blockWithItem(AWRegistry.SUEVITE_COBBLE);
        decoBlocks(AWRegistry.SUEVITE_COBBLE_DECO);
        blockWithItem(AWRegistry.SUEVITE_BRICKS);
        decoBlocks(AWRegistry.SUEVITE_BRICKS_DECO);
        blockWithItem(AWRegistry.SUEVITE_SMALL_BRICKS);
        decoBlocks(AWRegistry.SUEVITE_SMALL_BRICKS_DECO);
        blockWithItem(AWRegistry.SUEVITE_BIG_TILE);
        decoBlocks(AWRegistry.SUEVITE_BIG_TILE_DECO);
        blockWithItem(AWRegistry.SUEVITE_SMALL_TILE);
        decoBlocks(AWRegistry.SUEVITE_SMALL_TILE_DECO);
        blockWithRenderType(AWRegistry.GLASS_AETHERIUM, "glass_aetherium", "translucent");
        blockWithRenderType(AWRegistry.GLASS_AETHERIUM_BORDERLESS, "glass_aetherium_borderless", "translucent");
        blockWithItem(AWRegistry.AETHERIUM_ORE, "ore_aether");
        blockWithItem(AWRegistry.AETHERIUM_SHARD_BLOCK);
        blockWithItem(AWRegistry.AETHERIUM_BLOCK);
        blockWithItem(AWRegistry.PRISM_SUPPORT, "prism_support");
        blockWithItem(AWRegistry.PRISM, "prism");
        blockWithItem(AWRegistry.LEXICON_RECEPTACLE, "lexicon_receptacle");

        ItemModelBuilder moonlightAmplifier = horzBlockAndItemAdjust(AWRegistry.MOONLIGHT_AMPLIFIER, "moonlight_amplifier");
        itemWithAdjustment(moonlightAmplifier, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ROT_FPR, new Vector3f(0, 2, 2), SCALE_FPR);
        itemWithAdjustment(moonlightAmplifier, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ROT_TPR, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 2), SCALE_TPR);

        ItemModelBuilder controlMatrix = simpleBlockAndItemAdjust(AWRegistry.CONTROL_MATRIX, "aether_prism_control_matrix");
        itemWithAdjustment(controlMatrix, ItemDisplayContext.GUI, ROT_GUI, new Vector3f(0, -0.15f, 0), SCALE_GUI);
        itemWithAdjustment(controlMatrix, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ROT_FPR, new Vector3f(0, 0, 2), SCALE_FPR);
        itemWithAdjustment(controlMatrix, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ROT_TPR, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 2), SCALE_TPR);

        horizontalblockWithItem(AWRegistry.FORGE_HEATER, "forge_heater");
        horizontalblockWithItem(AWRegistry.FORGE_COOLER, "forge_cooler");

        ItemModelBuilder forgeAnvil = horzBlockAndItemAdjust(AWRegistry.FORGE_ANVIL, "anvil");
        itemWithAdjustment(forgeAnvil, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ROT_FPR, new Vector3f(0, 5, 2), SCALE_FPR);
        itemWithAdjustment(forgeAnvil, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ROT_TPR, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 2), SCALE_TPR);

        ItemModelBuilder metalFormer = horzBlockAndItemAdjust(AWRegistry.FORGE_METAL_FORMER, "metal_former");
        itemWithAdjustment(metalFormer, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ROT_FPR, new Vector3f(0, 5, 2), SCALE_FPR);
        itemWithAdjustment(metalFormer, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ROT_TPR, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 2), SCALE_TPR);

        ItemModelBuilder toolStation = horzBlockAndItemAdjust(AWRegistry.FORGE_TOOL_STATION, "tool_station");
        itemWithAdjustment(toolStation, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, ROT_FPR, new Vector3f(0, 5, 2), SCALE_FPR);
        itemWithAdjustment(toolStation, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ROT_TPR, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 2), SCALE_TPR);

        ItemModelBuilder forgeVent = horzBlockAndItemAdjust(AWRegistry.FORGE_VENT, "forge_vent");
        itemWithAdjustment(forgeVent, ItemDisplayContext.GUI, new Vector3f(ROT_GUI.x, 40, ROT_GUI.z), new Vector3f(3, -2f, 0), SCALE_GUI);
        itemWithAdjustment(forgeVent, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(0, -90, 25), new Vector3f( -1.13f, 3.2f, 1.13f), 0.55f);
        itemWithAdjustment(forgeVent, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, ZERO, new Vector3f(TRANS_TPR.x, TRANS_TPR.y, TRANS_TPR.z + 4), SCALE_TPR + 0.1f);

        ExistingModelFile forgeBottom = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether_forge_center"));
        ExistingModelFile forgeTop = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether_forge_top"));

        getVariantBuilder(AWRegistry.AETHER_FORGE.get()).forAllStates(state -> {
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);

            return ConfiguredModel.builder()
                    .modelFile(half == DoubleBlockHalf.LOWER ? forgeBottom : forgeTop)
                    .build();
        });
        simpleBlockItem(AWRegistry.AETHER_FORGE.get(), models().cubeAll("crate_aether_forge", ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "block/crate_aether_forge")));

        ExistingModelFile forgeEdgeModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether_forge_side"));
        ExistingModelFile forgeCornerModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether_forge_corner"));
        ExistingModelFile forgeConnectorModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "aether_forge_connector"));

        getMultipartBuilder(AWRegistry.AETHER_FORGE_EDGE.get())
                .part().modelFile(forgeEdgeModel).rotationY(0).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.NORTH).end()
                .part().modelFile(forgeEdgeModel).rotationY(90).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.EAST).end()
                .part().modelFile(forgeEdgeModel).rotationY(180).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.SOUTH).end()
                .part().modelFile(forgeEdgeModel).rotationY(270).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.WEST).end()
                .part().modelFile(forgeCornerModel).rotationY(0).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.NORTHEAST).end()
                .part().modelFile(forgeCornerModel).rotationY(90).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.SOUTHEAST).end()
                .part().modelFile(forgeCornerModel).rotationY(180).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.SOUTHWEST).end()
                .part().modelFile(forgeCornerModel).rotationY(270).addModel()
                .condition(MechEdgeBlockBase.EDGE, MechEdgeBlockBase.MechEdge.NORTHWEST).end()

                .part().modelFile(forgeConnectorModel).addModel()
                .condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(forgeConnectorModel).rotationY(90).addModel()
                .condition(BlockStateProperties.EAST, true).end()
                .part().modelFile(forgeConnectorModel).rotationY(180).addModel()
                .condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(forgeConnectorModel).rotationY(270).addModel()
                .condition(BlockStateProperties.WEST, true).end();

        dial(AWRegistry.HEAT_DIAL, "heat_dial");
    }
    public void blockWithItem(DeferredHolder<Block, ? extends Block> registryObject) {
        //block model
        simpleBlock(registryObject.get());
        //itemblock model
        simpleBlockItem(registryObject.get(), cubeAll(registryObject.get()));
    }
    public void blockWithItem(DeferredHolder<Block, ? extends Block> registryObject, String model) {
        ModelFile.ExistingModelFile modelFile = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, model));
        //block model
        simpleBlock(registryObject.get(), modelFile);
        //itemblock model
        simpleBlockItem(registryObject.get(), modelFile);
    }
    public void blockWithRenderType(DeferredHolder<Block, ? extends Block> registryObject, String texture, String renderType) {
        ModelFile modelFile = models().cubeAll(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(registryObject.get())).getPath(),
                ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "block/" + texture))
                .renderType(renderType);
        //block model
        simpleBlock(registryObject.get(), modelFile);
        //itemblock model
        simpleBlockItem(registryObject.get(), modelFile);
    }

    public void blockWithItemTexture(DeferredHolder<Block, ? extends Block> registryObject, String texture) {
        ModelFile modelFile = models().cubeAll(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(registryObject.get())).getPath(),
                ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "block/" + texture));
        //block model
        simpleBlock(registryObject.get(), modelFile);
        //itemblock model
        simpleBlockItem(registryObject.get(), modelFile);
    }
    public void horizontalblockWithItem(DeferredHolder<Block, ? extends Block> registryObject, String model) {
        ModelFile.ExistingModelFile modelFile = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, model));
        //block model
        horizontalBlock(registryObject.get(), modelFile);
        //itemblock model
        simpleBlockItem(registryObject.get(), modelFile);
    }
    public void decoBlocks(AWRegistry.StoneDecoBlocks deco) {
        ResourceLocation resourceLocation = this.blockTexture(deco.block.get());

        if (deco.stairs != null) {
            this.stairsBlock(deco.stairs.get(), resourceLocation);
            this.itemModels().stairs(deco.stairs.getId().getPath(), resourceLocation, resourceLocation, resourceLocation);
        }
        if (deco.slab != null) {
            this.slabBlock(deco.slab.get(), deco.block.getId(), resourceLocation);
            this.itemModels().slab(deco.slab.getId().getPath(), resourceLocation, resourceLocation, resourceLocation);
        }
        if (deco.wall != null) {
            this.wallBlock(deco.wall.get(), resourceLocation);
            this.itemModels().wallInventory(deco.wall.getId().getPath(), resourceLocation);
        }
    }

    public void dial(DeferredHolder<Block, ? extends Block> registryObject, String texture) {
        //block model
        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(registryObject.get());
        ModelFile model = models().withExistingParent(loc.toString(), ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "dial"))
                .texture("dial", ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "block/" + texture))
                .texture("particle", ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, "block/" + texture));
        directionalBlock(registryObject.get(), model);

        //item model
        flatItem(registryObject, texture);
    }

    public void flatItem(DeferredHolder<Block, ? extends Block> registryObject, String texture) {
        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(registryObject.get());
        itemModels().getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), "item/" + texture));
    }

    public void fluid(DeferredHolder<Block, ? extends Block> fluid, String name) {
        simpleBlock(fluid.get(), models().cubeAll(name, ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, ModelProvider.BLOCK_FOLDER + "/fluid/" + name + "_still")));
    }

    public static void itemWithAdjustment(ItemModelBuilder builder, ItemDisplayContext ctx,
                                          Vector3f rot, Vector3f trans, float scale) {
        builder.transforms().transform(ctx)
                .rotation(rot.x, rot.y, rot.z)
                .translation(trans.x, trans.y, trans.z)
                .scale(scale)
                .end().end();
    }
    public ItemModelBuilder simpleBlockAndItemAdjust(DeferredHolder<Block, ? extends Block> registryObject, String model) {
        ExistingModelFile file = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, model));
        simpleBlock(registryObject.get(), file);
        return itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(registryObject.get())
                .getPath()).parent(file);
    }
    public ItemModelBuilder horzBlockAndItemAdjust(DeferredHolder<Block, ? extends Block> registryObject, String model) {
        ExistingModelFile file = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(Aetherworks.MODID, model));
        horizontalBlock(registryObject.get(), file);
        return itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(registryObject.get())
                .getPath()).parent(file);
    }

    private void forgeStructure(Block block, Function<BlockState, ModelFile> modelFunc) {
        getVariantBuilder(block)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(modelFunc.apply(state))
                        .rotationY(state.getValue(OctFacingHorizontalProperty.OCT_DIRECTIONS).toBlockRot() % 360)
                        .build()
                );
    }
}

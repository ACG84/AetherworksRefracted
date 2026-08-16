package net.sirplop.aetherworks;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sirplop.aetherworks.blockentity.render.*;
import net.sirplop.aetherworks.capabilities.AWAttachments;
import net.sirplop.aetherworks.capabilities.AWCapabilities;
import net.sirplop.aetherworks.client.AWClientEvents;
import net.sirplop.aetherworks.client.AWKeybinds;
import net.sirplop.aetherworks.client.AetherShieldReflectHandler;
import net.sirplop.aetherworks.compat.curios.CuriosCompat;
import net.sirplop.aetherworks.datagen.*;
import net.sirplop.aetherworks.entity.render.DummyAetherCrownRender;
import net.sirplop.aetherworks.item.AetherCrownItem;
import net.sirplop.aetherworks.item.AetherEmberColorHandler;
import net.sirplop.aetherworks.item.PotionGemItem;
import net.sirplop.aetherworks.lib.AWHarvestHelper;
import net.sirplop.aetherworks.model.AetherCrownGemLayer;
import net.sirplop.aetherworks.model.AetherCrownModel;
import net.sirplop.aetherworks.network.PacketHandler;
import net.sirplop.aetherworks.research.AWResearch;
import org.slf4j.Logger;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Aetherworks.MODID)
public class Aetherworks
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "aetherworks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // NeoForge hands the mod bus and container to the constructor rather than exposing them statically.
    public Aetherworks(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(PacketHandler::registerPayloads);
        modEventBus.addListener(AWCapabilities::registerCapabilities);

        AWRegistry.BLOCKS.register(modEventBus);
        AWRegistry.ITEMS.register(modEventBus);
        AWDataComponents.COMPONENTS.register(modEventBus);
        AWAttachments.ATTACHMENT_TYPES.register(modEventBus);
        AWRegistry.FLUIDTYPES.register(modEventBus);
        AWRegistry.FLUIDS.register(modEventBus);
        AWRegistry.ENTITY_TYPES.register(modEventBus);
        AWRegistry.ATTRIBUTES.register(modEventBus);
        AWRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        AWRegistry.CREATIVE_MODE_TAB.register(modEventBus);
        //Aetheric is data-driven in 1.21 - aetherium items just self-repair.
        //AWRegistry.PARTICLE_TYPES.register(modEventBus);
        AWRegistry.MOB_EFFECTS.register(modEventBus);
        AWRegistry.SOUND_EVENTS.register(modEventBus);
        AWRegistry.RECIPE_TYPES.register(modEventBus);
        AWRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        AWRegistry.STRUCTURE_TYPES.register(modEventBus);
        AWRegistry.STRUCTURE_PIECES.register(modEventBus);
        AWSounds.init();

        AWConfig.register(modContainer);


        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.init();
        }

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.atInfo().log("Refracting Aetherium...");
        AWRegistry.init(event);
        event.enqueueWork(AWResearch::initResearch);

        NeoForge.EVENT_BUS.addListener(AWHarvestHelper::onServerTick);
        NeoForge.EVENT_BUS.addListener(AWHarvestHelper::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(AWHarvestHelper::onPlayerLeave);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeClient()) {
            gen.addProvider(true, new AWItemModels(output, existingFileHelper));
            gen.addProvider(true, new AWBlockStates(output, existingFileHelper));
            gen.addProvider(true, new AWSounds(output, existingFileHelper));
        } if (event.includeServer()) {
            gen.addProvider(true, new AWLootTables(output, lookupProvider));
            //AWRecipes is excluded from compilation - see the note in build.gradle.
            BlockTagsProvider blockTags = new AWBlockTags(output, lookupProvider, existingFileHelper);
            gen.addProvider(true, blockTags);
            gen.addProvider(true, new AWItemTags(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
            gen.addProvider(true, new AWFluidTags(output, lookupProvider, existingFileHelper));
            gen.addProvider(true, new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                    //.add(Registries.CONFIGURED_FEATURE, AWConfiguredFeatures::generate)
                    //.add(Registries.PLACED_FEATURE, AWPlacedFeatures::generate)
                    //.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AWBiomeModifiers::generate)
                    .add(Registries.DAMAGE_TYPE, AWDamageTypes::generate)
                    //.add(Registries.PROCESSOR_LIST, EmbersStructures::generateProcessors)
                    //.add(Registries.TEMPLATE_POOL, EmbersStructures::generatePools)
                    .add(Registries.STRUCTURE, AWStructures::generateStructures)
                    .add(Registries.STRUCTURE_SET, AWStructures::generateSets),
                    Set.of(MODID)));

            gen.addProvider(true, new AWBiomeTags(output, lookupProvider, existingFileHelper));
            gen.addProvider(true, new AWDamageTypeTags(output, lookupProvider, existingFileHelper));
            //gen.addProvider(true, new EmbersLootModifiers(output));
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            NeoForge.EVENT_BUS.addListener(AetherShieldReflectHandler::onUpdateEvent);
            NeoForge.EVENT_BUS.addListener(AetherShieldReflectHandler::onLevelUnload);
            NeoForge.EVENT_BUS.addListener(AetherShieldReflectHandler::onEntityLeaveEvent);

            event.enqueueWork(() -> {
                AWItemProperties.register();

                ItemBlockRenderTypes.setRenderLayer(AWRegistry.AETHERIUM_GAS.FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.AETHERIUM_GAS.FLUID_FLOW.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.AETHERIUM_GAS_IMPURE.FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.AETHERIUM_GAS_IMPURE.FLUID_FLOW.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.ALCHEMIC_PRECURSOR.FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.ALCHEMIC_PRECURSOR.FLUID_FLOW.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.SEETHING_AETHERIUM.FLUID.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(AWRegistry.SEETHING_AETHERIUM.FLUID_FLOW.get(), RenderType.translucent());
            });
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
            AWClientEvents.registerAdditionalModels(event);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void afterModelBake(ModelEvent.BakingCompleted event) {
            AWClientEvents.afterModelBake(event);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void overlayRegister(RegisterGuiLayersEvent event) {
            event.registerAboveAll(AWClientEvents.INGAME_OVERLAY_ID, AWClientEvents.INGAME_OVERLAY);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(AWRegistry.DUMMY_LOADER.get(), DummyAetherCrownRender::new);

            event.registerBlockEntityRenderer(AWRegistry.PRISM_BLOCK_ENTITY.get(), RenderPrism::new);
            event.registerBlockEntityRenderer(AWRegistry.AETHER_FORGE_BLOCK_ENTITY.get(), RenderAetherForge::new);
            event.registerBlockEntityRenderer(AWRegistry.METAL_FORMER_BLOCK_ENTITY.get(), RenderMetalFormer::new);
            event.registerBlockEntityRenderer(AWRegistry.AETHERIUM_ANVIL_BLOCK_ENTITY.get(), RenderAetherAnvil::new);
            event.registerBlockEntityRenderer(AWRegistry.TOOL_STATION_BLOCK_ENTITY.get(), RenderToolStation::new);
            event.registerBlockEntityRenderer(AWRegistry.LEXICON_RECEPTACLE_BLOCK_ENTITY.get(), RenderLexiconReceptacle::new);
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        static void registerLayers(EntityRenderersEvent.AddLayers event) {
            event.getSkins().forEach(skin ->
            {
                LivingEntityRenderer<?, ?> renderer = event.getSkin(skin);
                if (renderer != null)
                    renderer.addLayer(new AetherCrownGemLayer(renderer, event.getEntityModels()));
            });
            //The renderer map is private in 1.21, so walk the event's own entity types instead.
            event.getEntityTypes().forEach(type -> {
                if (event.getRenderer(type) instanceof LivingEntityRenderer<?, ?> renderer) {
                    renderer.addLayer(new AetherCrownGemLayer(renderer, event.getEntityModels()));
                }
            });
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(AetherCrownModel.CROWN_HEAD, () -> LayerDefinition.create(AetherCrownModel.createHeadMesh(), 32,32));
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event){
            event.register(new PotionGemItem.ColorHandler(), AWRegistry.POTION_GEM.get());
            event.register(new AetherCrownItem.ColorHandler(), AWRegistry.AETHER_CROWN.get());

            AetherEmberColorHandler emberColor = new AetherEmberColorHandler();

            if (ModList.get().isLoaded("curios"))
                CuriosCompat.registerColorHandler(event, emberColor);
            event.register(emberColor, AWRegistry.AETHER_EMBER_JAR.get(), AWRegistry.AETHER_EMBER_CARTRIDGE.get());
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {

            event.register(AWKeybinds.MODE_CHANGE);
        }
    }
}

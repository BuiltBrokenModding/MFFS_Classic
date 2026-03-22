package dev.su5ed.mffs.setup;

// =============================================================================
// 1.12.2 Backport: Client-side setup
// =============================================================================

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.client.ClientZoneTracker;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import dev.su5ed.mffs.render.BiometricIdentifierRenderer;
import dev.su5ed.mffs.render.CoercionDeriverRenderer;
import dev.su5ed.mffs.render.FortronCapacitorRenderer;
import dev.su5ed.mffs.render.InterdictionMatrixRenderer;
import dev.su5ed.mffs.render.ProjectorRenderer;
import dev.su5ed.mffs.render.model.ForceFieldBlockModel;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = MFFSMod.MODID, value = Side.CLIENT)
public final class ModClientSetup {

    private static final ResourceLocation COERCION_DERIVER_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/model/coercion_deriver_emissive.png");
    private static final ResourceLocation PROJECTOR_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/model/projector_emissive.png");
    private static final ResourceLocation FORTRON_CAPACITOR_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/model/fortron_capacitor_emissive.png");
    private static final ResourceLocation BIOMETRIC_IDENTIFIER_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/model/biometric_identifier_emissive.png");
    private static final ResourceLocation INTERDICTION_MATRIX_SIDE_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/block/interdiction_matrix_side_active_emissive.png");
    private static final ResourceLocation INTERDICTION_MATRIX_VERTICAL_EMISSIVE = new ResourceLocation(MFFSMod.MODID, "textures/block/interdiction_matrix_vertical_active_emissive.png");

    /**
     * Tracks the last world instance seen by {@link #onClientTick}.  When the reference
     * changes (world load, unload, dimension change, disconnect/reconnect) any positions
     * queued in {@link ForceFieldBlockEntity#PENDING_LIGHT_CHECKS} from the previous
     * session are discarded so they cannot corrupt lighting in the new world.
     */
    private static WorldClient lastClientWorld = null;

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // Bind TileEntity Special Renderers for rotating accents
        ClientRegistry.bindTileEntitySpecialRenderer(CoercionDeriverBlockEntity.class, new CoercionDeriverRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ProjectorBlockEntity.class, new ProjectorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(BiometricIdentifierBlockEntity.class, new BiometricIdentifierRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(FortronCapacitorBlockEntity.class, new FortronCapacitorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(InterdictionMatrixBlockEntity.class, new InterdictionMatrixRenderer());
        // NOTE: ForceFieldBlockEntity is intentionally NOT registered as a class-wide TESR.
        // A large force field can contain thousands of ForceFieldBlockEntity instances, and
        // registering a TESR for the class causes meldexun renderlib to run a frustum test
        // (and allocate a CallbackInfoReturnable via a third-party mixin) for every single one
        // on every frame

        // Block items
        registerBlockItemModel(ModBlocks.PROJECTOR);
        registerBlockItemModel(ModBlocks.COERCION_DERIVER);
        registerBlockItemModel(ModBlocks.FORTRON_CAPACITOR);
        registerBlockItemModel(ModBlocks.BIOMETRIC_IDENTIFIER);
        registerBlockItemModel(ModBlocks.INTERDICTION_MATRIX);
        // No ItemBlock for FORCE_FIELD — not player-obtainable

        // Tools, cards, materials
        registerItemModel(ModItems.REMOTE_CONTROLLER_ITEM);
        registerItemModel(ModItems.FREQUENCY_CARD);
        registerItemModel(ModItems.ID_CARD);
        registerItemModel(ModItems.BLANK_CARD);
        registerItemModel(ModItems.INFINITE_POWER_CARD);
        registerItemModel(ModItems.FOCUS_MATRIX);
        
        // Steel items - only register if not disabled
        if (!MFFSConfig.disableSteelItems) {
            registerItemModel(ModItems.STEEL_COMPOUND);
            registerItemModel(ModItems.STEEL_INGOT);
        }
        
        registerItemModel(ModItems.BATTERY);

        // Projector modes
        registerItemModel(ModItems.CUBE_MODE);
        registerItemModel(ModItems.SPHERE_MODE);
        registerItemModel(ModItems.TUBE_MODE);
        registerItemModel(ModItems.PYRAMID_MODE);
        registerItemModel(ModItems.CYLINDER_MODE);
        registerItemModel(ModItems.CUSTOM_MODE);

        // Field / general modules
        registerItemModel(ModItems.TRANSLATION_MODULE);
        registerItemModel(ModItems.SCALE_MODULE);
        registerItemModel(ModItems.ROTATION_MODULE);
        registerItemModel(ModItems.SPEED_MODULE);
        registerItemModel(ModItems.CAPACITY_MODULE);
        registerItemModel(ModItems.FUSION_MODULE);
        registerItemModel(ModItems.DOME_MODULE);
        registerItemModel(ModItems.CAMOUFLAGE_MODULE);
        registerItemModel(ModItems.DISINTEGRATION_MODULE);
        registerItemModel(ModItems.SHOCK_MODULE);
        registerItemModel(ModItems.GLOW_MODULE);
        registerItemModel(ModItems.SPONGE_MODULE);
        registerItemModel(ModItems.STABILIZATION_MODULE);
        registerItemModel(ModItems.COLLECTION_MODULE);
        registerItemModel(ModItems.INVERTER_MODULE);
        registerItemModel(ModItems.SILENCE_MODULE);

        // Interdiction matrix modules
        registerItemModel(ModItems.WARN_MODULE);
        registerItemModel(ModItems.BLOCK_ACCESS_MODULE);
        registerItemModel(ModItems.BLOCK_ALTER_MODULE);
        registerItemModel(ModItems.ANTI_FRIENDLY_MODULE);
        registerItemModel(ModItems.ANTI_HOSTILE_MODULE);
        registerItemModel(ModItems.ANTI_PERSONNEL_MODULE);
        registerItemModel(ModItems.ANTI_SPAWN_MODULE);
        registerItemModel(ModItems.CONFISCATION_MODULE);
    }

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(COERCION_DERIVER_EMISSIVE));
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(PROJECTOR_EMISSIVE));
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(FORTRON_CAPACITOR_EMISSIVE));
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(BIOMETRIC_IDENTIFIER_EMISSIVE));
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(INTERDICTION_MATRIX_SIDE_EMISSIVE));
        event.getMap().registerSprite(CodeChickenLibEmissiveCompat.toAtlasSpriteLocation(INTERDICTION_MATRIX_VERTICAL_EMISSIVE));
    }

    @SubscribeEvent
    public static void onBlockColorHandler(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(
            (state, access, pos, tintIndex) -> {
                if (access != null && pos != null) {
                    TileEntity te = access.getTileEntity(pos);
                    if (te instanceof ForceFieldBlockEntity forceField) {
                        IBlockState camo = forceField.getCamouflage();
                        if (camo != null) {
                            return event.getBlockColors().colorMultiplier(camo, access, pos, tintIndex);
                        }
                    }
                }
                return 0x34FEFF; // default cyan tint matching reference
            },
            ModBlocks.FORCE_FIELD
        );
    }

    /**
     * Wrap the force field block's baked model with {@link ForceFieldBlockModel}
     * to enable camouflage quad swapping during chunk meshing.
     */
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(ModBlocks.FORCE_FIELD.getRegistryName(), "normal");
        IBakedModel original = event.getModelRegistry().getObject(loc);
        if (original != null) {
            event.getModelRegistry().putObject(loc, new ForceFieldBlockModel(original));
        }
    }

    /**
     * Drain the deferred checkLight queue at a rate of {@link MFFSConfig#glowLightChecksPerTick}
     * positions per tick. This spreads the BFS cost of relighting Glow Module force fields over
     * multiple frames instead of spiking all at once when a chunk loads.
     *
     * Also clears the queue whenever the active world changes (join/leave/dimension switch)
     * so that positions queued for a previous session do not corrupt lighting in the new world.
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();

        // Discard stale light-check positions when the world changes.
        if (mc.world != lastClientWorld) {
            ForceFieldBlockEntity.PENDING_LIGHT_CHECKS.clear();
            ClientZoneTracker.clearAll();
            lastClientWorld = mc.world;
        }

        ClientZoneTracker.tick(mc);

        if (mc.world == null || ForceFieldBlockEntity.PENDING_LIGHT_CHECKS.isEmpty()) return;
        int limit = Math.max(1, MFFSConfig.glowLightChecksPerTick);
        for (int i = 0; i < limit; i++) {
            net.minecraft.util.math.BlockPos pos = ForceFieldBlockEntity.PENDING_LIGHT_CHECKS.poll();
            if (pos == null) break;
            mc.world.checkLight(pos);
        }
    }

    private static void registerItemModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0,
            new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerBlockItemModel(Block block) {
        Item item = Item.getItemFromBlock(block);
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }
    }

    private ModClientSetup() {}
}

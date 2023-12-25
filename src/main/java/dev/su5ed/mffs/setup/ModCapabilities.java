package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.card.FrequencyCard;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.blockentity.ElectricTileEntity;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.blockentity.InventoryBlockEntity;
import dev.su5ed.mffs.item.BatteryItem;
import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.item.ModuleItem;
import dev.su5ed.mffs.item.ProjectorModeItem;
import dev.su5ed.mffs.util.ItemEnergyStorage;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import one.util.streamex.StreamEx;

public final class ModCapabilities {
    // BlockEntity caps
    public static final BlockCapability<FortronStorage, Void> FORTRON = BlockCapability.createVoid(MFFSMod.location("fortron"), FortronStorage.class);
    public static final BlockCapability<Projector, Void> PROJECTOR = BlockCapability.createVoid(MFFSMod.location("projector"), Projector.class);
    public static final BlockCapability<BiometricIdentifier, Void> BIOMETRIC_IDENTIFIER = BlockCapability.createVoid(MFFSMod.location("biometric_identifier"), BiometricIdentifier.class);
    public static final BlockCapability<InterdictionMatrix, Void> INTERDICTION_MATRIX = BlockCapability.createVoid(MFFSMod.location("interdiction_matrix"), InterdictionMatrix.class);

    // Item caps
    public static final ItemCapability<ModuleType, Void> MODULE_TYPE = ItemCapability.createVoid(MFFSMod.location("module_type"), ModuleType.class);
    public static final ItemCapability<ProjectorMode, Void> PROJECTOR_MODE = ItemCapability.createVoid(MFFSMod.location("projector_mode"), ProjectorMode.class);
    public static final ItemCapability<IdentificationCard, Void> IDENTIFICATION_CARD = ItemCapability.createVoid(MFFSMod.location("identification_card"), IdentificationCard.class);
    public static final ItemCapability<FrequencyCard, Void> FREQUENCY_CARD = ItemCapability.createVoid(MFFSMod.location("frequency_card"), FrequencyCard.class);

    public static void registerCaps(RegisterCapabilitiesEvent event) {
        Block[] modMachines = new Block[]{ModBlocks.COERCION_DERIVER.get(), ModBlocks.FORTRON_CAPACITOR.get(), ModBlocks.PROJECTOR.get(), ModBlocks.BIOMETRIC_IDENTIFIER.get(), ModBlocks.INTERDICTION_MATRIX.get()};

        event.registerBlock(
            FORTRON,
            (level, pos, state, be, context) -> be != null ? ((FortronBlockEntity) be).fortronStorage : null,
            modMachines
        );
        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            (level, pos, state, be, context) -> be != null ? ((FortronBlockEntity) be).fortronStorage.getFortronTank() : null,
            modMachines
        );
        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            (level, pos, state, be, context) -> ((InventoryBlockEntity) be).getItems(),
            modMachines
        );
        event.registerBlockEntity(PROJECTOR, ModObjects.PROJECTOR_BLOCK_ENTITY.get(), (be, unused) -> be);
        event.registerBlockEntity(BIOMETRIC_IDENTIFIER, ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get(), (be, unused) -> be);
        event.registerBlockEntity(INTERDICTION_MATRIX, ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), (be, unused) -> be);
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(),
            ElectricTileEntity::getEnergy
        );

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, unused) -> {
            BatteryItem batteryItem = (BatteryItem) stack.getItem();
            return new ItemEnergyStorage(stack, batteryItem.getCapacity(), batteryItem.getMaxTransfer());
        }, ModItems.BATTERY);
        event.registerItem(FREQUENCY_CARD, (stack, unused) -> stack.getData(ModAttachmentTypes.FREQUENCY_CARD_DATE), ModItems.FREQUENCY_CARD);
        event.registerItem(IDENTIFICATION_CARD, (stack, unused) -> stack.getData(ModAttachmentTypes.IDENTIFICATION_CARD_DATA), ModItems.ID_CARD);
        event.registerItem(
            PROJECTOR_MODE,
            (stack, unused) -> ((ProjectorModeItem) stack.getItem()).getProjectorMode(),
            ModItems.CUBE_MODE, ModItems.SPHERE_MODE, ModItems.TUBE_MODE, ModItems.PYRAMID_MODE, ModItems.CYLINDER_MODE
        );
        event.registerItem(PROJECTOR_MODE, (stack, unused) -> ((CustomProjectorModeItem) stack.getItem()).new CustomProjectorModeCapability(stack), ModItems.CUSTOM_MODE);
        event.registerItem(MODULE_TYPE,
            (stack, unused) -> ((ModuleItem) stack.getItem()).getModule(),
            StreamEx.of(ModItems.ITEMS.getEntries())
                .map(DeferredHolder::get)
                .select(ModuleItem.class)
                .toArray(ModuleItem[]::new)
        );
    }

    private ModCapabilities() {}
}

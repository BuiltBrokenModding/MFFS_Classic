package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static dev.su5ed.mffs.MFFSMod.location;
import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModelsGen extends ModelProvider {

    public ModelsGen(PackOutput output) {
        super(output, MFFSMod.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        machineBlock(blockModels, ModBlocks.PROJECTOR.get(), true);
        machineBlock(blockModels, ModBlocks.COERCION_DERIVER.get(), true);
        machineBlock(blockModels, ModBlocks.FORTRON_CAPACITOR.get(), true);
        machineBlock(blockModels, ModBlocks.BIOMETRIC_IDENTIFIER.get(), false);
        machineBlock(blockModels, ModBlocks.INTERDICTION_MATRIX.get(), false);

        itemModels.itemModelOutput.accept(ModBlocks.BIOMETRIC_IDENTIFIER.asItem(), ItemModelUtils.composite(
            ItemModelUtils.plainModel(location("item/biometric_identifier_body")),
            ItemModelUtils.plainModel(location("item/biometric_identifier_screen"))
        ));
        itemModels.itemModelOutput.accept(ModBlocks.INTERDICTION_MATRIX.asItem(),
            ItemModelUtils.plainModel(location("block/interdiction_matrix")));

        blockModels.createTrivialBlock(ModBlocks.FORCE_FIELD.get(), TexturedModel.CUBE.updateTemplate(t -> t.extend()
            .renderType("translucent")
            .build())
        );

        // Items
        itemModels.generateFlatItem(ModItems.CAMOUFLAGE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CAPACITY_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SCALE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SPEED_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DISINTEGRATION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TRANSLATION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ROTATION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.GLOW_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.REMOTE_CONTROLLER_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SILENCE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SHOCK_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SPONGE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FUSION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.DOME_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.COLLECTION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.STABILIZATION_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.INVERTER_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.WARN_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BLOCK_ACCESS_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BLOCK_ALTER_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTI_FRIENDLY_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTI_HOSTILE_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTI_PERSONNEL_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ANTI_SPAWN_MODULE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CONFISCATION_MODULE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.CUBE_MODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SPHERE_MODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.TUBE_MODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PYRAMID_MODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CYLINDER_MODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.CUSTOM_MODE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.FOCUS_MATRIX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BATTERY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.STEEL_COMPOUND.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BLANK_CARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.ID_CARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.INFINITE_POWER_CARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.FREQUENCY_CARD.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(ModItems.REDSTONE_TORCH_OFF.get(),
            ItemModelUtils.plainModel(
                ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(ModItems.REDSTONE_TORCH_OFF.get()),
                    TextureMapping.layer0(ResourceLocation.withDefaultNamespace("block/redstone_torch_off")),
                    itemModels.modelOutput)
            ));
    }

    private void machineBlock(BlockModelGenerators blockModels, Block block, boolean item) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation inactiveLocation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath());
        ResourceLocation activeLocation = inactiveLocation.withSuffix("_active");

        MultiVariant plain = plainVariant(inactiveLocation);
        MultiVariant active = plainVariant(activeLocation);

        MultiVariantGenerator gen = MultiVariantGenerator.dispatch(block)
            .with(createBooleanModelDispatch(ProjectorBlock.ACTIVE, active, plain));
        if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            gen.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
        }

        blockModels.blockStateOutput.accept(gen);
        if (item) {
            blockModels.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block.asItem()));
        }
    }
}

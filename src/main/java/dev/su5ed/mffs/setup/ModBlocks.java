package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.*;
import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MFFSMod.MODID);

    public static final DeferredBlock<ProjectorBlock> PROJECTOR = block("projector", ProjectorBlock::new);
    public static final DeferredBlock<CoercionDeriverBlock> COERCION_DERIVER = block("coercion_deriver", CoercionDeriverBlock::new);
    public static final DeferredBlock<FortronCapacitorBlock> FORTRON_CAPACITOR = block("fortron_capacitor", FortronCapacitorBlock::new);
    public static final DeferredBlock<ForceFieldBlockImpl> FORCE_FIELD = BLOCKS.registerBlock("force_field", ForceFieldBlockImpl::new);
    public static final DeferredBlock<BiometricIdentifierBlock> BIOMETRIC_IDENTIFIER = block("biometric_identifier", BiometricIdentifierBlock::new);
    public static final DeferredBlock<BaseEntityBlock> INTERDICTION_MATRIX = baseEntityBlock("interdiction_matrix", () -> ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
    }

    private static DeferredBlock<BaseEntityBlock> baseEntityBlock(String name, Supplier<Supplier<? extends BlockEntityType<? extends BaseBlockEntity>>> beTypeProvider) {
        return BLOCKS.registerBlock(name, properties -> new BaseEntityBlock(properties.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(3F).requiresCorrectToolForDrops(), beTypeProvider.get()));
    }

    private static <T extends Block> DeferredBlock<T> block(String name, Function<BlockBehaviour.Properties, T> factory) {
        return BLOCKS.registerBlock(name, properties -> factory.apply(properties.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(3F).requiresCorrectToolForDrops()));
    }

    private ModBlocks() {}
}

package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.block.CoercionDeriverBlock;
import dev.su5ed.mffs.block.ForceFieldBlockImpl;
import dev.su5ed.mffs.block.FortronCapacitorBlock;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MFFSMod.MODID);
    private static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();

    public static final RegistryObject<ProjectorBlock> PROJECTOR = block("projector", ProjectorBlock::new);
    public static final RegistryObject<CoercionDeriverBlock> COERCION_DERIVER = block("coercion_deriver", CoercionDeriverBlock::new);
    public static final RegistryObject<FortronCapacitorBlock> FORTRON_CAPACITOR = block("fortron_capacitor", FortronCapacitorBlock::new);
    public static final RegistryObject<ForceFieldBlockImpl> FORCE_FIELD = BLOCKS.register("force_field", ForceFieldBlockImpl::new);

    public static void init(final IEventBus bus) {
        BLOCKS.register(bus);
    }

    private static RegistryObject<BaseEntityBlock> baseEntityBlock(String name, Supplier<BlockEntityType<? extends BaseBlockEntity>> beTypeProvider) {
        return BLOCKS.register(name, () -> new BaseEntityBlock(BLOCK_PROPERTIES, beTypeProvider));
    }

    private static <T extends Block> RegistryObject<T> block(String name, Function<BlockBehaviour.Properties, T> factory) {
        return BLOCKS.register(name, () -> factory.apply(BLOCK_PROPERTIES));
    }

    private ModBlocks() {}
}

package dev.su5ed.mffs.init;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.MachineBlockEntity;
import dev.su5ed.mffs.blockentity.AnimatedBlockEntity;
import dev.su5ed.mffs.render.BeamParticleOptions;
import dev.su5ed.mffs.render.ModParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModObjects {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MFFSMod.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MFFSMod.MODID);

    public static final RegistryObject<BlockEntityType<MachineBlockEntity>> MACHINE_BLOCK_ENTITY = blockEntity("machine", MachineBlockEntity::new, ModBlocks.MACHINE_BLOCK::get);
    public static final RegistryObject<BlockEntityType<AnimatedBlockEntity>> PROJECTOR_BLOCK_ENTITY = blockEntity("projector", AnimatedBlockEntity::new, ModBlocks.PROJECTOR::get);
    public static final RegistryObject<BlockEntityType<CoercionDeriverBlockEntity>> COERCION_DERIVER_BLOCK_ENTITY = blockEntity("coercion_deriver", CoercionDeriverBlockEntity::new, ModBlocks.COERCION_DERIVER::get);

    public static final RegistryObject<ModParticleType<BeamParticleOptions>> BEAM_PARTICLE = PARTICLES.register("beam", () -> new ModParticleType<>(true, BeamParticleOptions.DESERIALIZER, BeamParticleOptions.CODEC));

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_ENTITIES.register(bus);
        PARTICLES.register(bus);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> blockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    private ModObjects() {}
}

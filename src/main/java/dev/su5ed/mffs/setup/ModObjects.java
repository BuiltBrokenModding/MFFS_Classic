package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.ModParticleType;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.render.particle.MovingHologramParticleOptions;
import dev.su5ed.mffs.util.DamageSourceTrigger;
import dev.su5ed.mffs.util.FieldShapeTrigger;
import dev.su5ed.mffs.util.MenuInventoryTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public final class ModObjects {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MFFSMod.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MFFSMod.MODID);

    public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = blockEntity("projector", ProjectorBlockEntity::new, ModBlocks.PROJECTOR::get);
    public static final RegistryObject<BlockEntityType<CoercionDeriverBlockEntity>> COERCION_DERIVER_BLOCK_ENTITY = blockEntity("coercion_deriver", CoercionDeriverBlockEntity::new, ModBlocks.COERCION_DERIVER::get);
    public static final RegistryObject<BlockEntityType<FortronCapacitorBlockEntity>> FORTRON_CAPACITOR_BLOCK_ENTITY = blockEntity("fortron_capacitor", FortronCapacitorBlockEntity::new, ModBlocks.FORTRON_CAPACITOR::get);
    public static final RegistryObject<BlockEntityType<ForceFieldBlockEntity>> FORCE_FIELD_BLOCK_ENTITY = blockEntity("force_field", ForceFieldBlockEntity::new, ModBlocks.FORCE_FIELD::get);
    public static final RegistryObject<BlockEntityType<BiometricIdentifierBlockEntity>> BIOMETRIC_IDENTIFIER_BLOCK_ENTITY = blockEntity("biometric_identifier", BiometricIdentifierBlockEntity::new, ModBlocks.BIOMETRIC_IDENTIFIER::get);
    public static final RegistryObject<BlockEntityType<InterdictionMatrixBlockEntity>> INTERDICTION_MATRIX_BLOCK_ENTITY = blockEntity("interdiction_matrix", InterdictionMatrixBlockEntity::new, ModBlocks.INTERDICTION_MATRIX::get);

    public static final RegistryObject<ModParticleType<BeamParticleOptions>> BEAM_PARTICLE = PARTICLES.register("beam", () -> new ModParticleType<>(true, BeamParticleOptions.DESERIALIZER, BeamParticleOptions.CODEC));
    public static final RegistryObject<ModParticleType<MovingHologramParticleOptions>> MOVING_HOLOGRAM_PARTICLE = PARTICLES.register("moving_hologram", () -> new ModParticleType<>(true, MovingHologramParticleOptions.DESERIALIZER, MovingHologramParticleOptions.CODEC));

    public static final DamageSource FIELD_SHOCK = new DamageSource("mffs.field_shock").bypassArmor();
    public static final Lazy<DamageSourceTrigger> DAMAGE_TRIGGER = Lazy.of(() -> new DamageSourceTrigger(MFFSMod.location("damage_source")));
    public static final Lazy<FieldShapeTrigger> FIELD_SHAPE_TRIGGER = Lazy.of(() -> new FieldShapeTrigger(MFFSMod.location("field_shape")));
    public static final Lazy<MenuInventoryTrigger> MENU_INVENTORY_TRIGGER = Lazy.of(() -> new MenuInventoryTrigger(MFFSMod.location("menu_inventory")));

    public static final List<DamageSource> DAMAGE_SOURCES = List.of(FIELD_SHOCK);

    public static void init(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
        PARTICLES.register(bus);
    }
    
    public static void initCriteriaTriggers() {
        CriteriaTriggers.register(DAMAGE_TRIGGER.get());
        CriteriaTriggers.register(FIELD_SHAPE_TRIGGER.get());
        CriteriaTriggers.register(MENU_INVENTORY_TRIGGER.get());
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> blockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITY_TYPES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    private ModObjects() {}
}

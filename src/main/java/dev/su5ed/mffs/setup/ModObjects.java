package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.*;
import dev.su5ed.mffs.render.ModParticleType;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.render.particle.MovingHologramParticleOptions;
import dev.su5ed.mffs.util.loot.DamageSourceTrigger;
import dev.su5ed.mffs.util.loot.FieldShapeTrigger;
import dev.su5ed.mffs.util.loot.GuideBookTrigger;
import dev.su5ed.mffs.util.loot.MenuInventoryTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModObjects {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MFFSMod.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MFFSMod.MODID);
    private static final DeferredRegister<CriterionTrigger<?>> CRITERION_TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, MFFSMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = blockEntity("projector", ProjectorBlockEntity::new, ModBlocks.PROJECTOR::get);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoercionDeriverBlockEntity>> COERCION_DERIVER_BLOCK_ENTITY = blockEntity("coercion_deriver", CoercionDeriverBlockEntity::new, ModBlocks.COERCION_DERIVER::get);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FortronCapacitorBlockEntity>> FORTRON_CAPACITOR_BLOCK_ENTITY = blockEntity("fortron_capacitor", FortronCapacitorBlockEntity::new, ModBlocks.FORTRON_CAPACITOR::get);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ForceFieldBlockEntity>> FORCE_FIELD_BLOCK_ENTITY = blockEntity("force_field", ForceFieldBlockEntity::new, ModBlocks.FORCE_FIELD::get);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BiometricIdentifierBlockEntity>> BIOMETRIC_IDENTIFIER_BLOCK_ENTITY = blockEntity("biometric_identifier", BiometricIdentifierBlockEntity::new, ModBlocks.BIOMETRIC_IDENTIFIER::get);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InterdictionMatrixBlockEntity>> INTERDICTION_MATRIX_BLOCK_ENTITY = blockEntity("interdiction_matrix", InterdictionMatrixBlockEntity::new, ModBlocks.INTERDICTION_MATRIX::get);

    public static final DeferredHolder<ParticleType<?>, ModParticleType<BeamParticleOptions>> BEAM_PARTICLE = PARTICLES.register("beam", () -> new ModParticleType<>(true, BeamParticleOptions.CODEC.fieldOf("options"), BeamParticleOptions.STREAM_CODEC));
    public static final DeferredHolder<ParticleType<?>, ModParticleType<MovingHologramParticleOptions>> MOVING_HOLOGRAM_PARTICLE = PARTICLES.register("moving_hologram", () -> new ModParticleType<>(true, MovingHologramParticleOptions.CODEC.fieldOf("options"), MovingHologramParticleOptions.STREAM_CODEC));

    public static final ResourceKey<DamageType> FIELD_SHOCK_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE, MFFSMod.location("field_shock"));
    public static final DeferredHolder<CriterionTrigger<?>, DamageSourceTrigger> DAMAGE_TRIGGER = CRITERION_TRIGGERS.register("damage_source", DamageSourceTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, FieldShapeTrigger> FIELD_SHAPE_TRIGGER = CRITERION_TRIGGERS.register("field_shape", FieldShapeTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, MenuInventoryTrigger> MENU_INVENTORY_TRIGGER = CRITERION_TRIGGERS.register("menu_inventory", MenuInventoryTrigger::new);
    public static final DeferredHolder<CriterionTrigger<?>, GuideBookTrigger> GUIDEBOOK_TRIGGER = CRITERION_TRIGGERS.register("guidebook", GuideBookTrigger::new);

    public static void init(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
        PARTICLES.register(bus);
        CRITERION_TRIGGERS.register(bus);
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> blockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITY_TYPES.register(name, () -> new BlockEntityType<>(factory, block.get()));
    }

    private ModObjects() {}
}

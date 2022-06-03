package dev.su5ed.mffs;

import dev.su5ed.mffs.block.MachineBlock;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.blockentity.MachineBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.BeamParticleOptions;
import dev.su5ed.mffs.render.ModParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModObjects {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MFFSMod.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MFFSMod.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MFFSMod.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MFFSMod.MODID);

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MFFSMod.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.APPLE);
        }
    };

    public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(ITEM_GROUP);

    public static final RegistryObject<MachineBlock> MACHINE_BLOCK = BLOCKS.register("machine_block", () -> new MachineBlock(BLOCK_PROPERTIES));
    public static final RegistryObject<Item> MACHINE_ITEM = fromBlock(MACHINE_BLOCK);
    public static final RegistryObject<BlockEntityType<MachineBlockEntity>> MACHINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("machine", () -> BlockEntityType.Builder.of(MachineBlockEntity::new, MACHINE_BLOCK.get()).build(null));

    public static final RegistryObject<ProjectorBlock> PROJECTOR_BLOCK = BLOCKS.register("projector_block", () -> new ProjectorBlock(BLOCK_PROPERTIES));
    public static final RegistryObject<Item> PROJECTOR_ITEM = fromBlock(PROJECTOR_BLOCK);
    public static final RegistryObject<BlockEntityType<ProjectorBlockEntity>> PROJECTOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("projector", () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, PROJECTOR_BLOCK.get()).build(null));

    public static final RegistryObject<ModParticleType<BeamParticleOptions>> BEAM_PARTICLE = PARTICLES.register("beam", () -> new ModParticleType<>(true, BeamParticleOptions.DESERIALIZER, BeamParticleOptions.CODEC));

    public static void init() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        PARTICLES.register(bus);
    }

    public static <B extends Block> RegistryObject<Item> fromBlock(final RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }

    private ModObjects() {
    }
}

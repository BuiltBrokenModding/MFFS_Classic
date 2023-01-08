package dev.su5ed.mffs.block;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.Permission;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ForceFieldBlockImpl extends AbstractGlassBlock implements ForceFieldBlock, EntityBlock {
    private static final VoxelShape COLLIDABLE_BLOCK = Shapes.create(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public ForceFieldBlockImpl() {
        super(Properties.of(Material.GLASS)
            .destroyTime(-1)
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isRedstoneConductor((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false));
    }

    @Nullable
    @Override
    public Projector getProjector(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof ForceFieldBlockEntity forceField ? forceField.getProjector() : null;
    }

    @Override
    public void weakenForceField(Level level, BlockPos pos, int joules) {
        Projector projector = getProjector(level, pos);
        if (projector instanceof FortronStorage storage) {
            storage.insertFortron(joules, false);
        }
        level.removeBlock(pos, false);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos))
            .map(be -> be instanceof ForceFieldBlockEntity f ? f.getClientBlockLight() : null)
            .orElseGet(() -> super.getLightEmission(state, level, pos));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return getShape(state, reader, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Projector projector = getProjector(level, pos);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (projector != null && level instanceof EntityGetter entityGetter) {
            BiometricIdentifier bioIndentified = projector.getBiometricIdentifier();
            List<Player> entities = entityGetter.getEntitiesOfClass(Player.class, Shapes.box(x, y, z, x + 1, y + 0.9, z + 1).bounds());

            for (Player player : entities) {
                if (player.isShiftKeyDown() && (player.isCreative() || bioIndentified != null && bioIndentified.isAccessGranted(player.getGameProfile().getName(), Permission.FORCE_FIELD_WARP))) {
                    return Shapes.empty();
                }
            }
        }
        return COLLIDABLE_BLOCK;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (level.getBlockEntity(pos) instanceof ForceFieldBlockEntity) {
            Projector projector = getProjector(level, pos);
            if (projector != null) {
                for (ItemStack stack : projector.getModuleStacks()) {
                    if (((Module) stack.getItem()).onCollideWithForceField(level, pos, entity, stack)) {
                        return;
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModObjects.FORCE_FIELD_BLOCK_ENTITY.get().create(pos, state);
    }
}

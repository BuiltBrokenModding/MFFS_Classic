package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ForceFieldBlockEntity extends BlockEntity {
    private BlockPos projector;

	public ForceFieldBlockEntity(BlockPos pos, BlockState state) {
		super(ModObjects.FORCE_FIELD_BLOCK_ENTITY.get(), pos, state);
	}

    public void setProjector(BlockPos position) {
        this.projector = position;
    }

    /**
     * @return Gets the projector block controlling this force field. Removes the force field if no
     * projector can be found.
     */
    @Nullable
    public ProjectorBlockEntity getProjector() {
        if (getProjectorSafe() != null) {
            return getProjectorSafe();
        }
        else if (!this.level.isClientSide) {
            this.level.removeBlock(this.worldPosition, false);
        }
        return null;
    }

    public ProjectorBlockEntity getProjectorSafe() {
        if (this.projector != null && !this.level.isClientSide && this.level.getBlockEntity(this.projector) instanceof ProjectorBlockEntity projectorBe && projectorBe.getCalculatedField().contains(this.worldPosition)) {
            return projectorBe;
        }
        return null;
    }

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		
		if (this.projector != null) {
            tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        }
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		
		if (tag.contains("projector")) {
            this.projector = NbtUtils.readBlockPos(tag.getCompound("projector"));
        }
	}
}

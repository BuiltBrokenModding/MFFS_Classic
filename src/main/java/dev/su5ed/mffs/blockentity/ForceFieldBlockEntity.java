package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ForceFieldBlockEntity extends BlockEntity {
    private BlockPos projector;
    private Block camouflage;

    public ForceFieldBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.FORCE_FIELD_BLOCK_ENTITY.get(), pos, state);
    }

    public void setProjector(BlockPos position) {
        this.projector = position;
        setChanged();
    }

    public void setCamouflage(Block camouflage) {
        this.camouflage = camouflage;
        setChanged();
    }

    @Override
    public ModelData getModelData() {
        if (this.camouflage != null) {
            return ModelData.builder()
                .with(ForceFieldBlock.CAMOUFLAGE_BLOCK, this.camouflage)
                .build();
        }
        return super.getModelData();
    }

    /**
     * @return Gets the projector block controlling this force field. Removes the force field if no
     * projector can be found.
     */
    @Nullable
    public ProjectorBlockEntity getProjector() {
        // TODO make return optional
        if (getProjectorSafe() != null) {
            return getProjectorSafe();
        } else if (!this.level.isClientSide) {
            this.level.removeBlock(this.worldPosition, false);
        }
        return null;
    }

    @Nullable
    public ProjectorBlockEntity getProjectorSafe() {
        return !this.level.isClientSide ? getProjectorSafe(this.level) : null;
    }

    @Nullable
    public ProjectorBlockEntity getProjectorSafe(BlockGetter level) {
        if (this.projector != null && level.getBlockEntity(this.projector) instanceof ProjectorBlockEntity projectorBe && projectorBe.getCalculatedField().contains(this.worldPosition)) {
            return projectorBe;
        }
        return null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        updateRenderClient();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (this.projector != null) {
            tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        }
        if (this.camouflage != null) {
            tag.putString("camouflage", ForgeRegistries.BLOCKS.getKey(this.camouflage).toString());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("projector")) {
            this.projector = NbtUtils.readBlockPos(tag.getCompound("projector"));
        }
        if (tag.contains("camouflage")) {
            this.camouflage = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("camouflage")));
        }
    }

    public void updateRenderClient() {
        requestModelDataUpdate();
        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_IMMEDIATE);
    }
}

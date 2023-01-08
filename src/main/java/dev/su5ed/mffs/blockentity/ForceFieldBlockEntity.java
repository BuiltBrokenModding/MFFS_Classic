package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.network.InitialDataRequestPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
    private int clientBlockLight;

    public ForceFieldBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.FORCE_FIELD_BLOCK_ENTITY.get(), pos, state);
    }

    public int getClientBlockLight() {
        return this.clientBlockLight;
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
    public void onLoad() {
        super.onLoad();
        
        if (this.level.isClientSide) {
            InitialDataRequestPacket packet = new InitialDataRequestPacket(this.worldPosition);
            Network.INSTANCE.sendToServer(packet);
        }
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
        if (getProjectorSafe() != null) {
            return getProjectorSafe();
        } else if (!this.level.isClientSide) {
            this.level.removeBlock(this.worldPosition, false);
        }
        return null;
    }

    @Nullable
    public ProjectorBlockEntity getProjectorSafe() {
        return getProjectorSafe(this.level);
    }

    @Nullable
    public ProjectorBlockEntity getProjectorSafe(BlockGetter level) {
        if (this.projector != null && level.getBlockEntity(this.projector) instanceof ProjectorBlockEntity projectorBe && (this.level.isClientSide || projectorBe.getCalculatedField().contains(this.worldPosition))) {
            return projectorBe;
        }
        return null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        tag.putInt("clientBlockLight", Math.round((float) Math.min(getProjectorSafe().getModuleCount(ModItems.GLOW_MODULE.get()), 64) / 64 * 15));
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
        this.projector = NbtUtils.readBlockPos(tag.getCompound("projector"));
        this.clientBlockLight = tag.getInt("clientBlockLight");

        updateRenderClient();
        this.level.getLightEngine().checkBlock(this.worldPosition);
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
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }
}

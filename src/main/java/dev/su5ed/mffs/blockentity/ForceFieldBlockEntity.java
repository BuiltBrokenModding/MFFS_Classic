package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.block.ForceFieldBlockImpl;
import dev.su5ed.mffs.network.InitialDataRequestPacket;
import dev.su5ed.mffs.render.BlockEntityRenderDelegate;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class ForceFieldBlockEntity extends BlockEntity {
    private BlockPos projector;
    private BlockState camouflage;
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

    public BlockState getCamouflage() {
        return this.camouflage;
    }

    public void setCamouflage(BlockState camouflage) {
        this.camouflage = camouflage;
        this.level.setBlock(
            worldPosition,
            getBlockState()
                .setValue(ForceFieldBlockImpl.PROPAGATES_SKYLIGHT, camouflage.propagatesSkylightDown())
                .setValue(ForceFieldBlockImpl.SOLID, !camouflage.getOcclusionShape().isEmpty()),
            Block.UPDATE_ALL
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.level.isClientSide) {
            InitialDataRequestPacket packet = new InitialDataRequestPacket(this.worldPosition);
            PacketDistributor.sendToServer(packet);
            if (this.camouflage != null) {
                BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
            }
        }
    }

    @Override
    public void setRemoved() {
        if (this.level.isClientSide) {
            BlockEntityRenderDelegate.INSTANCE.removeDelegateOf(this);
        }
        super.setRemoved();
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

    public Optional<Projector> getProjector() {
        return this.projector != null ? Optional.ofNullable(this.level.getCapability(ModCapabilities.PROJECTOR, this.projector, null)) : Optional.empty();
    }

    // Manual handling of update tags so that we send data when the BE is first created, rather than only on world load
    public CompoundTag getCustomUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        if (this.projector != null) {
            tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        }
        int clientBlockLight = getProjector().map(projector -> Math.round((float) Math.min(projector.getModuleCount(ModModules.GLOW), 64) / 64 * 15)).orElse(0);
        tag.putInt("clientBlockLight", clientBlockLight);
        return tag;
    }

    public void handleCustomUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        loadAdditional(tag, provider);
        this.projector = NbtUtils.readBlockPos(tag, "projector").orElse(null);
        this.clientBlockLight = tag.getInt("clientBlockLight");

        updateRenderClient();
        this.level.getLightEngine().checkBlock(this.worldPosition);
        if (this.camouflage != null) {
            BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.projector != null) {
            tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        }
        if (this.camouflage != null) {
            tag.put("camouflage", NbtUtils.writeBlockState(this.camouflage));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        NbtUtils.readBlockPos(tag, "projector")
            .ifPresent(p -> this.projector = p);
        if (tag.contains("camouflage")) {
            this.camouflage = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("camouflage"));
        }
    }

    public void updateRenderClient() {
        requestModelDataUpdate();
        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }
}

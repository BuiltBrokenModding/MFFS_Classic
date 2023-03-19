package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.ForceFieldBlock;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.network.InitialDataRequestPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.render.BlockEntityRenderDelegate;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

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

    public Block getCamouflage() {
        return this.camouflage;
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
            if (this.camouflage != null) {
                BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage.defaultBlockState());
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
        return this.projector != null
            ? Optional.ofNullable(this.level.getBlockEntity(this.projector))
                .flatMap(be -> be.getCapability(ModCapabilities.PROJECTOR).resolve())
            : Optional.empty();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        if (this.projector != null) {
            tag.put("projector", NbtUtils.writeBlockPos(this.projector));
        }
        int clientBlockLight = getProjector().map(projector -> Math.round((float) Math.min(projector.getModuleCount(ModModules.GLOW), 64) / 64 * 15)).orElse(0);
        tag.putInt("clientBlockLight", clientBlockLight);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
        this.projector = tag.contains("projector") ? NbtUtils.readBlockPos(tag.getCompound("projector")) : null;
        this.clientBlockLight = tag.getInt("clientBlockLight");

        updateRenderClient();
        this.level.getLightEngine().checkBlock(this.worldPosition);
        if (this.camouflage != null) {
            BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, camouflage.defaultBlockState());
        }
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

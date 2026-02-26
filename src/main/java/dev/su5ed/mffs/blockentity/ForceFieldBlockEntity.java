package dev.su5ed.mffs.blockentity;

import com.mojang.logging.LogUtils;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.model.data.ModelData;
import org.slf4j.Logger;

import java.util.Optional;

public class ForceFieldBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

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

        if (this.level.isClientSide()) {
            InitialDataRequestPacket packet = new InitialDataRequestPacket(this.worldPosition);
            ClientPacketDistributor.sendToServer(packet);
            if (this.camouflage != null) {
                BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
            }
        }
    }

    @Override
    public void setRemoved() {
        if (this.level.isClientSide()) {
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
        CompoundTag tag;
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(scopedCollector, provider);
            saveAdditional(output);

            if (this.projector != null) {
                output.store("projector", BlockPos.CODEC, this.projector);
            }
            int clientBlockLight = getProjector().map(projector -> Math.round((float) Math.min(projector.getModuleCount(ModModules.GLOW), 64) / 64 * 15)).orElse(0);
            output.putInt("clientBlockLight", clientBlockLight);

            tag = output.buildResult();
        }
        return tag;
    }

    public void handleCustomUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            ValueInput input = TagValueInput.create(scopedCollector, provider, tag);

            super.handleUpdateTag(input);
            loadAdditional(input);

            this.projector = input.read("projector", BlockPos.CODEC).orElse(null);
            this.clientBlockLight = tag.getInt("clientBlockLight").orElse(0);

            updateRenderClient();
            this.level.getLightEngine().checkBlock(this.worldPosition);
            if (this.camouflage != null) {
                BlockEntityRenderDelegate.INSTANCE.putDelegateFor(this, this.camouflage);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.projector != null) {
            output.store("projector", BlockPos.CODEC, this.projector);
        }
        if (this.camouflage != null) {
            output.store("camouflage", BlockState.CODEC, this.camouflage);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read("projector", BlockPos.CODEC)
            .ifPresent(p -> this.projector = p);

        input.read("camouflage", BlockState.CODEC)
            .ifPresent(p -> this.camouflage = p);
    }

    public void updateRenderClient() {
        requestModelDataUpdate();
        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }
}

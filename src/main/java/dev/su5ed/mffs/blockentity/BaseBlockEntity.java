package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.block.BaseEntityBlock;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleModePacketClient;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

public abstract class BaseBlockEntity extends BlockEntity {
    protected boolean enabled;
    private long tickCounter;

    protected BaseBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public long getTicks() {
        return this.tickCounter;
    }

    public boolean isEnabled() {
        return this.enabled || this.level.hasNeighborSignal(this.worldPosition);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public void tickClient() {
        ++this.tickCounter;
    }

    public void tickServer() {
        ++this.tickCounter;

        BlockState state = getBlockState();
        boolean enabled = isEnabled();
        if (state.getValue(BaseEntityBlock.ENABLED) != enabled) {
            this.level.setBlock(this.worldPosition, state.setValue(BaseEntityBlock.ENABLED, enabled), Block.UPDATE_ALL);

            if (!this.level.isClientSide()) {
                sendToChunk(new ToggleModePacketClient(this.worldPosition, isEnabled()));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        loadCommonTag(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadCommonTag(tag);
    }

    @Override
    public final void load(CompoundTag tag) {
        super.load(tag);
        loadCommonTag(tag);
        loadTag(tag);
    }

    @Override
    protected final void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveCommonTag(tag);
        saveTag(tag);
    }

    protected void loadCommonTag(CompoundTag tag) {
        this.enabled = tag.getBoolean("enabled");
    }

    protected void saveCommonTag(CompoundTag tag) {
        tag.putBoolean("enabled", this.enabled);
    }

    protected void loadTag(CompoundTag tag) {}

    protected void saveTag(CompoundTag tag) {}

    public <T> void sendToChunk(T msg) {
        Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), msg);
    }
}

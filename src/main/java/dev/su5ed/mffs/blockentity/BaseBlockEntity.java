package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

public abstract class BaseBlockEntity extends BlockEntity {
    private long tickCounter;

    protected BaseBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public long getTicks() {
        return this.tickCounter;
    }

    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public void tickClient() {
        ++this.tickCounter;
    }

    public void tickServer() {
        ++this.tickCounter;
    }
    
    public void blockRemoved() {}

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveCommonTag(tag);
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

    protected void loadCommonTag(CompoundTag tag) {}

    protected void saveCommonTag(CompoundTag tag) {}

    protected void loadTag(CompoundTag tag) {}

    protected void saveTag(CompoundTag tag) {}

    public <T> void sendToChunk(T msg) {
        Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), msg);
    }
}

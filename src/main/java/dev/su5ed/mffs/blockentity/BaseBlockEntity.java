package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.NetworkHooks;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public abstract class BaseBlockEntity extends BlockEntity implements MenuProvider {
    private long tickCounter;

    protected BaseBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public long getTicks() {
        return this.tickCounter;
    }

    public void tickClient() {
        ++this.tickCounter;
    }

    public void tickServer() {
        ++this.tickCounter;
    }

    public void beforeBlockRemove() {}

    public void provideAdditionalDrops(List<? super ItemStack> drops) {}

    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

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

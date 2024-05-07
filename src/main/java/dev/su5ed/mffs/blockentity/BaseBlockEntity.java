package dev.su5ed.mffs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            player.openMenu(this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveCommonTag(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        loadCommonTag(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        loadCommonTag(tag, provider);
        loadTag(tag, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        saveCommonTag(tag, provider);
        saveTag(tag, provider);
    }

    protected void loadCommonTag(CompoundTag tag, HolderLookup.Provider provider) {}

    protected void saveCommonTag(CompoundTag tag, HolderLookup.Provider provider) {}

    protected void loadTag(CompoundTag tag, HolderLookup.Provider provider) {}

    protected void saveTag(CompoundTag tag, HolderLookup.Provider provider) {}

    public <T extends CustomPacketPayload> void sendToChunk(T msg) {
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) this.level, this.level.getChunkAt(this.worldPosition).getPos(), msg);
    }
}

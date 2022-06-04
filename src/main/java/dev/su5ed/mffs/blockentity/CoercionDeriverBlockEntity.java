package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleActivationPacketClient;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class CoercionDeriverBlockEntity extends AnimatedBlockEntity implements MenuProvider {

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        
        if (!this.level.isClientSide()) {
            sendToChunk(new ToggleActivationPacketClient(this.worldPosition, isActive()));
        }
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CoercionDeriverContainer(containerId, player, this.worldPosition);
    }

    public <T> void sendToChunk(T msg) {
        Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), msg);
    }
}

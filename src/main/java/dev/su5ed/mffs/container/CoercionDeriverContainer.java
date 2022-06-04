package dev.su5ed.mffs.container;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModContainers;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CoercionDeriverContainer extends AbstractContainerMenu {
    public final CoercionDeriverBlockEntity blockEntity;
    private final Player player;

    public CoercionDeriverContainer(int containerId, Player player, BlockPos pos) {
        super(ModContainers.POWERGEN_CONTAINER.get(), containerId);

        this.player = player;
        this.blockEntity = player.getCommandSenderWorld().getBlockEntity(pos, ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get()).orElseThrow();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos()), this.player, ModBlocks.COERCION_DERIVER.get());
    }
}

package dev.su5ed.mffs.network;

import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

public final class ClientPacketHandler {
    
    public static void handleToggleActivationPacket(ToggleModePacketClient packet) {
        runBlockEntityTask(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), packet.pos(), be -> be.setEnabled(packet.enabled()));
    }

    private static <T extends BlockEntity> void runBlockEntityTask(BlockEntityType<T> type, BlockPos pos, Consumer<T> consumer) {
        Minecraft mc = Minecraft.getInstance();
        mc.doRunTask(() -> Network.findBlockEntity(type, mc.level, pos).ifPresent(consumer));
    }

    private ClientPacketHandler() {}
}

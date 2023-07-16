package dev.su5ed.mffs.network;

import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SwitchTransferModePacket(BlockPos pos, TransferMode mode) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.mode);
    }

    public static SwitchTransferModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        TransferMode enabled = buf.readEnum(TransferMode.class);
        return new SwitchTransferModePacket(pos, enabled);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().level();
        Network.findBlockEntity(ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setTransferMode(this.mode));
    }
}

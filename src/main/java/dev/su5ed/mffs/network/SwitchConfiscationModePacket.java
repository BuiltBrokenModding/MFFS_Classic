package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SwitchConfiscationModePacket(BlockPos pos, InterdictionMatrix.ConfiscationMode mode) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeEnum(this.mode);
    }

    public static SwitchConfiscationModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        InterdictionMatrix.ConfiscationMode mode = buf.readEnum(InterdictionMatrix.ConfiscationMode.class);
        return new SwitchConfiscationModePacket(pos, mode);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        Level level = ctx.get().getSender().level();
        Network.findBlockEntity(ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), level, this.pos)
            .ifPresent(be -> be.setConfiscationMode(this.mode));
    }
}

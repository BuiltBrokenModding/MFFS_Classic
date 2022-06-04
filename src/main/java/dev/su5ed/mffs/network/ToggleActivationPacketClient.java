package dev.su5ed.mffs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleActivationPacketClient extends ToggleActivationPacket {

    public ToggleActivationPacketClient(BlockPos pos, boolean active) {
        super(pos, active);
    }

    public static ToggleActivationPacketClient decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean active = buf.readBoolean();
        return new ToggleActivationPacketClient(pos, active);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleToggleActivationPacket(this)));
        ctx.get().setPacketHandled(true);
    }
}

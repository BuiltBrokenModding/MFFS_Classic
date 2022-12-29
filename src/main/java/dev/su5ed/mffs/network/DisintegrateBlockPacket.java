package dev.su5ed.mffs.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DisintegrateBlockPacket(Vec3 pos, Vec3 target, int type) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.pos.x());
        buf.writeDouble(this.pos.y());
        buf.writeDouble(this.pos.z());

        buf.writeDouble(this.target.x());
        buf.writeDouble(this.target.y());
        buf.writeDouble(this.target.z());

        buf.writeInt(this.type);
    }

    public static DisintegrateBlockPacket decode(FriendlyByteBuf buf) {
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 target = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int type = buf.readInt();
        return new DisintegrateBlockPacket(pos, target, type);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleDisintegrateBlockPacket(this));
    }
}

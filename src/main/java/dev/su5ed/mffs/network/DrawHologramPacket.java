package dev.su5ed.mffs.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DrawHologramPacket(Vec3 pos, Vec3 target, Type type) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.pos.x());
        buf.writeDouble(this.pos.y());
        buf.writeDouble(this.pos.z());

        buf.writeDouble(this.target.x());
        buf.writeDouble(this.target.y());
        buf.writeDouble(this.target.z());

        buf.writeEnum(this.type);
    }

    public static DrawHologramPacket decode(FriendlyByteBuf buf) {
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 target = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Type type = buf.readEnum(Type.class);
        return new DrawHologramPacket(pos, target, type);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleDrawHologramPacket(this));
    }
    
    public enum Type {
        CONSTRUCT,
        DESTROY
    }
}

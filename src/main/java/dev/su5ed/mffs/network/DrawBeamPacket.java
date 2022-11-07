package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.particle.BeamColor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DrawBeamPacket(Vec3 target, Vec3 position, BeamColor color, int lifetime) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.target.x());
        buf.writeDouble(this.target.y());
        buf.writeDouble(this.target.z());

        buf.writeDouble(this.position.x());
        buf.writeDouble(this.position.y());
        buf.writeDouble(this.position.z());

        buf.writeEnum(this.color);
        buf.writeInt(this.lifetime);
    }

    public static DrawBeamPacket decode(FriendlyByteBuf buf) {
        Vec3 target = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        BeamColor color = buf.readEnum(BeamColor.class);
        int lifetime = buf.readInt();
        return new DrawBeamPacket(target, position, color, lifetime);
    }

    public void processClientPacket(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleDrawBeamPacket(this));
    }
}

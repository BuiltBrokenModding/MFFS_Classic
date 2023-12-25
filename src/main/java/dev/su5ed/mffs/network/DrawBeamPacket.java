package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.particle.ParticleColor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public record DrawBeamPacket(Vec3 target, Vec3 position, ParticleColor color, int lifetime) {
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
        ParticleColor color = buf.readEnum(ParticleColor.class);
        int lifetime = buf.readInt();
        return new DrawBeamPacket(target, position, color, lifetime);
    }

    public void processClientPacket(NetworkEvent.Context ctx) {
        if (FMLEnvironment.dist.isClient()) {
            ClientPacketHandler.handleDrawBeamPacket(this);
        }
    }
}

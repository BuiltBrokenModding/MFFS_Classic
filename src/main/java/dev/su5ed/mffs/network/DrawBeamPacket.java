package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.render.particle.ParticleColor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record DrawBeamPacket(Vec3 target, Vec3 position, ParticleColor color, int lifetime) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("draw_beam");

    public DrawBeamPacket(FriendlyByteBuf buf) {
        this(
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            buf.readEnum(ParticleColor.class),
            buf.readInt()
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.target.x());
        buf.writeDouble(this.target.y());
        buf.writeDouble(this.target.z());

        buf.writeDouble(this.position.x());
        buf.writeDouble(this.position.y());
        buf.writeDouble(this.position.z());

        buf.writeEnum(this.color);
        buf.writeInt(this.lifetime);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}

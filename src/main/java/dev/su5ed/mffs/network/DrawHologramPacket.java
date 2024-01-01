package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record DrawHologramPacket(Vec3 pos, Vec3 target, Type type) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("draw_hologram");

    public DrawHologramPacket(FriendlyByteBuf buf) {
        this(
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            buf.readEnum(Type.class)
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.pos.x());
        buf.writeDouble(this.pos.y());
        buf.writeDouble(this.pos.z());

        buf.writeDouble(this.target.x());
        buf.writeDouble(this.target.y());
        buf.writeDouble(this.target.z());

        buf.writeEnum(this.type);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public enum Type {
        CONSTRUCT,
        DESTROY
    }
}

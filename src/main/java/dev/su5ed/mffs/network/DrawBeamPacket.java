package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.particle.ParticleColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DrawBeamPacket implements IMessage {
    private Vec3d target;
    private Vec3d position;
    private ParticleColor color;
    private int lifetime;

    public DrawBeamPacket() {}

    public DrawBeamPacket(Vec3d target, Vec3d position, ParticleColor color, int lifetime) {
        this.target   = target;
        this.position = position;
        this.color    = color;
        this.lifetime = lifetime;
    }

    public Vec3d getTarget() { return this.target; }
    public Vec3d getPosition() { return this.position; }
    public ParticleColor getColor() { return this.color; }
    public int getLifetime() { return this.lifetime; }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.target   = readVec3d(buf);
        this.position = readVec3d(buf);
        this.color    = ParticleColor.values()[buf.readInt()];
        this.lifetime = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVec3d(buf, this.target);
        writeVec3d(buf, this.position);
        buf.writeInt(this.color.ordinal());
        buf.writeInt(this.lifetime);
    }

    private static Vec3d readVec3d(ByteBuf buf) {
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    private static void writeVec3d(ByteBuf buf, Vec3d v) {
        buf.writeDouble(v.x);
        buf.writeDouble(v.y);
        buf.writeDouble(v.z);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<DrawBeamPacket, IMessage> {
        @Override
        public IMessage onMessage(DrawBeamPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> ClientPacketHandler.handleDrawBeamPacket(message));
            return null;
        }
    }
}

package dev.su5ed.mffs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DrawHologramPacket implements IMessage {
    private Vec3d pos;
    private Vec3d target;
    private HoloType holoType;

    public enum HoloType {
        CONSTRUCT,
        DESTROY
    }

    public DrawHologramPacket() {}

    public DrawHologramPacket(Vec3d pos, Vec3d target, HoloType holoType) {
        this.pos      = pos;
        this.target   = target;
        this.holoType = holoType;
    }

    public Vec3d getPos() { return this.pos; }
    public Vec3d getTarget() { return this.target; }
    public HoloType getHoloType() { return this.holoType; }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos      = readVec3d(buf);
        this.target   = readVec3d(buf);
        this.holoType = HoloType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeVec3d(buf, this.pos);
        writeVec3d(buf, this.target);
        buf.writeInt(this.holoType.ordinal());
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
    public static class Handler implements IMessageHandler<DrawHologramPacket, IMessage> {
        @Override
        public IMessage onMessage(DrawHologramPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> ClientPacketHandler.handleDrawHologramPacket(message));
            return null;
        }
    }
}

package dev.su5ed.mffs.network;

import dev.su5ed.mffs.client.ClientZoneTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sent server → client whenever the Interdiction Matrix's zone configuration changes
 * (activation, module change) or as a periodic heartbeat.  The client stores this data
 * in {@link ClientZoneTracker} and uses it to render local proximity warnings without any
 * further server involvement.
 */
public class IMAZoneSyncPacket implements IMessage {

    /** Zone type constants — shared between server (IM) and client (ClientZoneTracker). */
    public static final byte ZONE_DEFENSE     = 0;
    public static final byte ZONE_CONFISCATION = 1;
    public static final byte ZONE_KILL         = 2;

    private int x, y, z;
    private int actionRange;
    private int warningRange;
    private byte zoneType;
    private boolean active;

    /** Required no-arg constructor for packet deserialization. */
    public IMAZoneSyncPacket() {}

    public IMAZoneSyncPacket(BlockPos pos, int actionRange, int warningRange, byte zoneType, boolean active) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.actionRange  = actionRange;
        this.warningRange = warningRange;
        this.zoneType = zoneType;
        this.active = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.actionRange  = buf.readInt();
        this.warningRange = buf.readInt();
        this.zoneType = buf.readByte();
        this.active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.actionRange);
        buf.writeInt(this.warningRange);
        buf.writeByte(this.zoneType);
        buf.writeBoolean(this.active);
    }

    @SideOnly(Side.CLIENT)
    public static class Handler implements IMessageHandler<IMAZoneSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(IMAZoneSyncPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> ClientZoneTracker.updateZone(
                new BlockPos(message.x, message.y, message.z),
                message.actionRange,
                message.warningRange,
                message.zoneType,
                message.active
            ));
            return null;
        }
    }
}

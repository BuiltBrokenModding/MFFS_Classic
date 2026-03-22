package dev.su5ed.mffs.network;

import dev.su5ed.mffs.item.CustomProjectorModeItem;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StructureDataRequestPacket implements IMessage {
    private String structId;

    public StructureDataRequestPacket() {}

    public StructureDataRequestPacket(String structId) {
        this.structId = structId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.structId = new PacketBuffer(buf).readString(256);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        new PacketBuffer(buf).writeString(this.structId);
    }

    public static class Handler implements IMessageHandler<StructureDataRequestPacket, IMessage> {
        @Override
        public IMessage onMessage(StructureDataRequestPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = (WorldServer) player.world;
            world.addScheduledTask(() -> {
                CustomStructureSavedData data = ((CustomProjectorModeItem) ModItems.CUSTOM_MODE).getOrCreateData(world);
                CustomStructureSavedData.Structure structure = data.get(message.structId);
                Network.sendTo(
                    new SetStructureShapePacket(world.provider.getDimension(), message.structId,
                        structure != null ? structure.shape() : null),
                    player
                );
            });
            return null;
        }
    }
}


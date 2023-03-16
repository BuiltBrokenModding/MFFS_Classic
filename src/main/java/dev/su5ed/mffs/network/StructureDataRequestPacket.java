package dev.su5ed.mffs.network;

import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record StructureDataRequestPacket(String id) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
    }

    public static StructureDataRequestPacket decode(FriendlyByteBuf buf) {
        String id = buf.readUtf();
        return new StructureDataRequestPacket(id);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ServerLevel level = context.getSender().getLevel();
        CustomStructureSavedData data = ModItems.CUSTOM_MODE.get().getOrCreateData(level);
        CustomStructureSavedData.Structure structure = data.get(this.id);
        Network.INSTANCE.reply(new SetStructureShapePacket(level.dimension(), this.id, structure != null ? structure.shape() : null), context);
    }
}

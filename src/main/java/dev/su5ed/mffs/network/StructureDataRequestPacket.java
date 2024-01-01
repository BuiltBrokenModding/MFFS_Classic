package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record StructureDataRequestPacket(String structId) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("structure_data");

    public StructureDataRequestPacket(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.structId);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ServerLevel level = (ServerLevel) ctx.player().orElseThrow().level();
        CustomStructureSavedData data = ModItems.CUSTOM_MODE.get().getOrCreateData(level);
        CustomStructureSavedData.Structure structure = data.get(this.structId);
        ctx.replyHandler().send(new SetStructureShapePacket(level.dimension(), this.structId, structure != null ? structure.shape() : null));
    }
}

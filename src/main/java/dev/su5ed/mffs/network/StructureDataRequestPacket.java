package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.projector.CustomStructureSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StructureDataRequestPacket(String structId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StructureDataRequestPacket> TYPE = new CustomPacketPayload.Type<>(MFFSMod.location("structure_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StructureDataRequestPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        StructureDataRequestPacket::structId,
        StructureDataRequestPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ServerLevel level = (ServerLevel) ctx.player().level();
        CustomStructureSavedData data = ModItems.CUSTOM_MODE.get().getOrCreateData(level);
        CustomStructureSavedData.Structure structure = data.get(this.structId);
        ctx.reply(new SetStructureShapePacket(level.dimension(), this.structId, structure != null ? structure.shape() : null));
    }
}

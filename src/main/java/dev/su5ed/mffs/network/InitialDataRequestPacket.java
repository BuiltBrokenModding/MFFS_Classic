package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record InitialDataRequestPacket(BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = MFFSMod.location("initial_data");

    public InitialDataRequestPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        Level level = ctx.player().orElseThrow().level();
        if (level.isLoaded(this.pos)) {
            level.getBlockEntity(this.pos, ModObjects.FORCE_FIELD_BLOCK_ENTITY.get()).ifPresent(be -> {
                CompoundTag data = be.getCustomUpdateTag();
                UpdateBlockEntityPacket packet = new UpdateBlockEntityPacket(this.pos, data);
                ctx.replyHandler().send(packet);
            });
        }
    }
}

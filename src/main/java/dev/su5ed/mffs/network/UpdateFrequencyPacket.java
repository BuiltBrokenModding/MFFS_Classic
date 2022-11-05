package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateFrequencyPacket {
    public final BlockPos pos;
    public final int frequency;

    public UpdateFrequencyPacket(BlockPos pos, int frequency) {
        this.pos = pos;
        this.frequency = frequency;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.frequency);
    }

    public static UpdateFrequencyPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int frequency = buf.readInt();
        return new UpdateFrequencyPacket(pos, frequency);
    }

    public void processServerPacket(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().getLevel();
            Network.findBlockEntity(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), level, this.pos)
                .ifPresent(this::process);
        });
        ctx.get().setPacketHandled(true);
    }

    public void process(CoercionDeriverBlockEntity blockEntity) {
        blockEntity.setFrequency(this.frequency);
    }
}

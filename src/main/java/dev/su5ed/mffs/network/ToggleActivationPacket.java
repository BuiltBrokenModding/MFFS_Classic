package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleActivationPacket {
    public final BlockPos pos;
    public final boolean active;

    public ToggleActivationPacket(BlockPos pos, boolean active) {
        this.pos = pos;
        this.active = active;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.active);
    }

    public static ToggleActivationPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean active = buf.readBoolean();
        return new ToggleActivationPacket(pos, active);
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
        blockEntity.setActive(this.active);
    }
}

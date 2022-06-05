package dev.su5ed.mffs.network;

import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.setup.ModObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleModePacket {
    public final BlockPos pos;
    public final boolean enabled;

    public ToggleModePacket(BlockPos pos, boolean enabled) {
        this.pos = pos;
        this.enabled = enabled;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeBoolean(this.enabled);
    }

    public static ToggleModePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean enabled = buf.readBoolean();
        return new ToggleModePacket(pos, enabled);
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
        blockEntity.setEnabled(this.enabled);
    }
}

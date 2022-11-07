package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MFFSMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;

        INSTANCE.messageBuilder(ToggleModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ToggleModePacket::encode)
            .decoder(ToggleModePacket::decode)
            .consumerMainThread(ToggleModePacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(UpdateFrequencyPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(UpdateFrequencyPacket::encode)
            .decoder(UpdateFrequencyPacket::decode)
            .consumerMainThread(UpdateFrequencyPacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(ToggleEnergyModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ToggleEnergyModePacket::encode)
            .decoder(ToggleEnergyModePacket::decode)
            .consumerMainThread(ToggleEnergyModePacket::processServerPacket)
            .add();

        INSTANCE.messageBuilder(ToggleModePacketClient.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(ToggleModePacketClient::encode)
            .decoder(ToggleModePacketClient::decode)
            .consumerMainThread(ToggleModePacketClient::processClientPacket)
            .add();
        INSTANCE.messageBuilder(DrawBeamPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(DrawBeamPacket::encode)
            .decoder(DrawBeamPacket::decode)
            .consumerMainThread(DrawBeamPacket::processClientPacket)
            .add();
    }

    public static <T extends BlockEntity> Optional<T> findBlockEntity(BlockEntityType<T> type, Level level, BlockPos pos) {
        return level.isLoaded(pos) ? level.getBlockEntity(pos, type) : Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> findBlockEntity(Class<T> type, Level level, BlockPos pos) {
        return level.isLoaded(pos) ? Optional.ofNullable(level.getBlockEntity(pos)).map(be -> type.isInstance(be) ? (T) be : null) : Optional.empty();
    }

    private Network() {}
}

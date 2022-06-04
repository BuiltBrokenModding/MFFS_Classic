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

        INSTANCE.registerMessage(id++,
            ToggleActivationPacket.class,
            ToggleActivationPacket::encode,
            ToggleActivationPacket::decode,
            ToggleActivationPacket::processServerPacket,
            Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        INSTANCE.registerMessage(id++,
            ToggleActivationPacketClient.class,
            ToggleActivationPacketClient::encode,
            ToggleActivationPacketClient::decode,
            ToggleActivationPacketClient::processClientPacket,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }
    
    public static <T extends BlockEntity> Optional<T> findBlockEntity(BlockEntityType<T> type, Level level, BlockPos pos) {
        return level.isLoaded(pos) ? level.getBlockEntity(pos, type) : Optional.empty();
    }

    private Network() {}
}

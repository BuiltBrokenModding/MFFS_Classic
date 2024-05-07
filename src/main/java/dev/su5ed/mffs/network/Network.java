package dev.su5ed.mffs.network;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MFFSMod.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToServer(ToggleModePacket.TYPE, ToggleModePacket.STREAM_CODEC, mainThreadHandler(ToggleModePacket::handle));
        registrar.playToServer(UpdateFrequencyPacket.TYPE, UpdateFrequencyPacket.STREAM_CODEC, mainThreadHandler(UpdateFrequencyPacket::handle));
        registrar.playToServer(SwitchEnergyModePacket.TYPE, SwitchEnergyModePacket.STREAM_CODEC, mainThreadHandler(SwitchEnergyModePacket::handle));
        registrar.playToServer(SwitchTransferModePacket.TYPE, SwitchTransferModePacket.STREAM_CODEC, mainThreadHandler(SwitchTransferModePacket::handle));
        registrar.playToServer(InitialDataRequestPacket.TYPE, InitialDataRequestPacket.STREAM_CODEC, mainThreadHandler(InitialDataRequestPacket::handle));
        registrar.playToServer(ToggleFieldPermissionPacket.TYPE, ToggleFieldPermissionPacket.STREAM_CODEC, mainThreadHandler(ToggleFieldPermissionPacket::handle));
        registrar.playToServer(SwitchConfiscationModePacket.TYPE, SwitchConfiscationModePacket.STREAM_CODEC, mainThreadHandler(SwitchConfiscationModePacket::handle));
        registrar.playToServer(SetItemInSlotPacket.TYPE, SetItemInSlotPacket.STREAM_CODEC, mainThreadHandler(SetItemInSlotPacket::handle));
        registrar.playToServer(StructureDataRequestPacket.TYPE, StructureDataRequestPacket.STREAM_CODEC, mainThreadHandler(StructureDataRequestPacket::handle));

        registrar.playToClient(UpdateBlockEntityPacket.TYPE, UpdateBlockEntityPacket.STREAM_CODEC, mainThreadHandler(() -> ClientPacketHandler::handleBlockEntityUpdatePacket));
        registrar.playToClient(SetStructureShapePacket.TYPE, SetStructureShapePacket.STREAM_CODEC, mainThreadHandler(() -> ClientPacketHandler::handleSetStructureShapePacket));
        registrar.playToClient(DrawBeamPacket.TYPE, DrawBeamPacket.STREAM_CODEC, mainThreadHandler(() -> ClientPacketHandler::handleDrawBeamPacket));
        registrar.playToClient(UpdateAnimationSpeed.TYPE, UpdateAnimationSpeed.STREAM_CODEC, mainThreadHandler(() -> ClientPacketHandler::handleUpdateAnimationSpeedPacket));
        registrar.playToClient(DrawHologramPacket.TYPE, DrawHologramPacket.STREAM_CODEC, mainThreadHandler(() -> ClientPacketHandler::handleDrawHologramPacket));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> mainThreadHandler(Supplier<IPayloadHandler<T>> supplier) {
        return mainThreadHandler((payload, context) -> supplier.get().handle(payload, context));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> mainThreadHandler(IPayloadHandler<T> handler) {
        return (payload, context) -> context.enqueueWork(() -> handler.handle(payload, context))
            .exceptionally(thr -> {
                LOGGER.error("Error handling payload", thr);
                return null;
            });
    }

    public static <T extends BlockEntity> Optional<T> findBlockEntity(BlockEntityType<T> type, Level level, BlockPos pos) {
        return level.isLoaded(pos) ? level.getBlockEntity(pos, type) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> findBlockEntity(Class<T> type, Level level, BlockPos pos) {
        return findBlockEntity(level, pos).map(be -> type.isInstance(be) ? (T) be : null);
    }

    public static <T> Optional<T> findBlockEntity(BlockCapability<T, ?> type, Level level, BlockPos pos) {
        return findBlockEntity(level, pos).flatMap(be -> Optional.ofNullable(level.getCapability(type, pos, null)));
    }

    public static Optional<BlockEntity> findBlockEntity(Level level, BlockPos pos) {
        return level.isLoaded(pos) ? Optional.ofNullable(level.getBlockEntity(pos)) : Optional.empty();
    }

    private Network() {
    }
}

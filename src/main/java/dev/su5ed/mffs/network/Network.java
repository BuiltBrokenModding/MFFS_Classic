package dev.su5ed.mffs.network;

import com.mojang.logging.LogUtils;
import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public final class Network {
    private static final String PROTOCOL_VERSION = "1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void registerPackets(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MFFSMod.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.play(ToggleModePacket.ID, ToggleModePacket::new, handler -> handler.server(mainThreadHandler(ToggleModePacket::handle)));
        registrar.play(UpdateFrequencyPacket.ID, UpdateFrequencyPacket::new, handler -> handler.server(mainThreadHandler(UpdateFrequencyPacket::handle)));
        registrar.play(SwitchEnergyModePacket.ID, SwitchEnergyModePacket::new, handler -> handler.server(mainThreadHandler(SwitchEnergyModePacket::handle)));
        registrar.play(SwitchTransferModePacket.ID, SwitchTransferModePacket::new, handler -> handler.server(mainThreadHandler(SwitchTransferModePacket::handle)));
        registrar.play(InitialDataRequestPacket.ID, InitialDataRequestPacket::new, handler -> handler.server(mainThreadHandler(InitialDataRequestPacket::handle)));
        registrar.play(ToggleFieldPermissionPacket.ID, ToggleFieldPermissionPacket::new, handler -> handler.server(mainThreadHandler(ToggleFieldPermissionPacket::handle)));
        registrar.play(SwitchConfiscationModePacket.ID, SwitchConfiscationModePacket::new, handler -> handler.server(mainThreadHandler(SwitchConfiscationModePacket::handle)));
        registrar.play(SetItemInSlotPacket.ID, SetItemInSlotPacket::new, handler -> handler.server(mainThreadHandler(SetItemInSlotPacket::handle)));
        registrar.play(StructureDataRequestPacket.ID, StructureDataRequestPacket::new, handler -> handler.server(mainThreadHandler(StructureDataRequestPacket::handle)));

        registrar.play(UpdateBlockEntityPacket.ID, UpdateBlockEntityPacket::new, handler -> handler.client(mainThreadHandler(() -> ClientPacketHandler::handleBlockEntityUpdatePacket)));
        registrar.play(SetStructureShapePacket.ID, SetStructureShapePacket::new, handler -> handler.client(mainThreadHandler(() -> ClientPacketHandler::handleSetStructureShapePacket)));
        registrar.play(DrawBeamPacket.ID, DrawBeamPacket::new, handler -> handler.client(mainThreadHandler(() -> ClientPacketHandler::handleDrawBeamPacket)));
        registrar.play(UpdateAnimationSpeed.ID, UpdateAnimationSpeed::new, handler -> handler.client(mainThreadHandler(() -> ClientPacketHandler::handleUpdateAnimationSpeedPacket)));
        registrar.play(DrawHologramPacket.ID, DrawHologramPacket::new, handler -> handler.client(mainThreadHandler(() -> ClientPacketHandler::handleDrawHologramPacket)));
    }

    private static <T extends CustomPacketPayload> IPlayPayloadHandler<T> mainThreadHandler(Supplier<IPlayPayloadHandler<T>> supplier) {
        return mainThreadHandler((payload, context) -> supplier.get().handle(payload, context));
    }

    private static <T extends CustomPacketPayload> IPlayPayloadHandler<T> mainThreadHandler(IPlayPayloadHandler<T> handler) {
        return (payload, context) -> context.workHandler()
            .submitAsync(() -> handler.handle(payload, context))
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

    private Network() {}
}

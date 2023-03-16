package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.Capability;
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
        INSTANCE.messageBuilder(SwitchEnergyModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SwitchEnergyModePacket::encode)
            .decoder(SwitchEnergyModePacket::decode)
            .consumerMainThread(SwitchEnergyModePacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(SwitchTransferModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SwitchTransferModePacket::encode)
            .decoder(SwitchTransferModePacket::decode)
            .consumerMainThread(SwitchTransferModePacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(InitialDataRequestPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(InitialDataRequestPacket::encode)
            .decoder(InitialDataRequestPacket::decode)
            .consumerMainThread(InitialDataRequestPacket::processPacket)
            .add();
        INSTANCE.messageBuilder(ToggleFieldPermissionPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ToggleFieldPermissionPacket::encode)
            .decoder(ToggleFieldPermissionPacket::decode)
            .consumerMainThread(ToggleFieldPermissionPacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(SwitchConfiscationModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SwitchConfiscationModePacket::encode)
            .decoder(SwitchConfiscationModePacket::decode)
            .consumerMainThread(SwitchConfiscationModePacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(SetItemInSlotPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SetItemInSlotPacket::encode)
            .decoder(SetItemInSlotPacket::decode)
            .consumerMainThread(SetItemInSlotPacket::processServerPacket)
            .add();
        INSTANCE.messageBuilder(StructureDataRequestPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(StructureDataRequestPacket::encode)
            .decoder(StructureDataRequestPacket::decode)
            .consumerMainThread(StructureDataRequestPacket::processServerPacket)
            .add();

        INSTANCE.messageBuilder(DrawBeamPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(DrawBeamPacket::encode)
            .decoder(DrawBeamPacket::decode)
            .consumerMainThread(DrawBeamPacket::processClientPacket)
            .add();
        INSTANCE.messageBuilder(UpdateAnimationSpeed.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(UpdateAnimationSpeed::encode)
            .decoder(UpdateAnimationSpeed::decode)
            .consumerMainThread(UpdateAnimationSpeed::processClientPacket)
            .add();
        INSTANCE.messageBuilder(DrawHologramPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(DrawHologramPacket::encode)
            .decoder(DrawHologramPacket::decode)
            .consumerMainThread(DrawHologramPacket::processClientPacket)
            .add();
        INSTANCE.messageBuilder(UpdateBlockEntityPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(UpdateBlockEntityPacket::encode)
            .decoder(UpdateBlockEntityPacket::decode)
            .consumerMainThread(UpdateBlockEntityPacket::processClientPacket)
            .add();
        INSTANCE.messageBuilder(SetStructureShapePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SetStructureShapePacket::encode)
            .decoder(SetStructureShapePacket::decode)
            .consumerMainThread(SetStructureShapePacket::processClientPacket)
            .add();
    }

    public static <T extends BlockEntity> Optional<T> findBlockEntity(BlockEntityType<T> type, Level level, BlockPos pos) {
        return level.isLoaded(pos) ? level.getBlockEntity(pos, type) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> findBlockEntity(Class<T> type, Level level, BlockPos pos) {
        return findBlockEntity(level, pos).map(be -> type.isInstance(be) ? (T) be : null);
    }

    public static <T> Optional<T> findBlockEntity(Capability<T> type, Level level, BlockPos pos) {
        return findBlockEntity(level, pos).flatMap(be -> be.getCapability(type).resolve());
    }

    public static Optional<BlockEntity> findBlockEntity(Level level, BlockPos pos) {
        return level.isLoaded(pos) ? Optional.ofNullable(level.getBlockEntity(pos)) : Optional.empty();
    }

    private Network() {}
}

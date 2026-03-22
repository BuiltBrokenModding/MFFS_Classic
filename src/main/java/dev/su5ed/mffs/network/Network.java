package dev.su5ed.mffs.network;

import dev.su5ed.mffs.MFFSMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public final class Network {
    public static SimpleNetworkWrapper CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MFFSMod.MODID);
        int id = 0;
        // Server-bound (sent from client to server)
        CHANNEL.registerMessage(ToggleModePacket.Handler.class,              ToggleModePacket.class,              id++, Side.SERVER);
        CHANNEL.registerMessage(UpdateFrequencyPacket.Handler.class,         UpdateFrequencyPacket.class,         id++, Side.SERVER);
        CHANNEL.registerMessage(SwitchEnergyModePacket.Handler.class,        SwitchEnergyModePacket.class,        id++, Side.SERVER);
        CHANNEL.registerMessage(SwitchTransferModePacket.Handler.class,      SwitchTransferModePacket.class,      id++, Side.SERVER);
        CHANNEL.registerMessage(InitialDataRequestPacket.Handler.class,      InitialDataRequestPacket.class,      id++, Side.SERVER);
        CHANNEL.registerMessage(ToggleFieldPermissionPacket.Handler.class,   ToggleFieldPermissionPacket.class,   id++, Side.SERVER);
        CHANNEL.registerMessage(SwitchConfiscationModePacket.Handler.class,  SwitchConfiscationModePacket.class,  id++, Side.SERVER);
        CHANNEL.registerMessage(SetItemInSlotPacket.Handler.class,           SetItemInSlotPacket.class,           id++, Side.SERVER);
        CHANNEL.registerMessage(StructureDataRequestPacket.Handler.class,    StructureDataRequestPacket.class,    id++, Side.SERVER);
        // Client-bound (sent from server to client)
        CHANNEL.registerMessage(UpdateBlockEntityPacket.Handler.class,       UpdateBlockEntityPacket.class,       id++, Side.CLIENT);
        CHANNEL.registerMessage(SetStructureShapePacket.Handler.class,       SetStructureShapePacket.class,       id++, Side.CLIENT);
        CHANNEL.registerMessage(DrawBeamPacket.Handler.class,                DrawBeamPacket.class,                id++, Side.CLIENT);
        CHANNEL.registerMessage(UpdateAnimationSpeed.Handler.class,          UpdateAnimationSpeed.class,          id++, Side.CLIENT);
        CHANNEL.registerMessage(DrawHologramPacket.Handler.class,            DrawHologramPacket.class,            id++, Side.CLIENT);
        CHANNEL.registerMessage(IMAZoneSyncPacket.Handler.class,             IMAZoneSyncPacket.class,             id++, Side.CLIENT);
    }

    /** Find a TileEntity by class at the given position. */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> findTileEntity(Class<T> type, World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return Optional.empty();
        TileEntity te = world.getTileEntity(pos);
        return type.isInstance(te) ? Optional.of((T) te) : Optional.empty();
    }

    /** Find any TileEntity at the given position. */
    public static Optional<TileEntity> findTileEntity(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) return Optional.empty();
        return Optional.ofNullable(world.getTileEntity(pos));
    }

    /** Send a packet from the client to the server. */
    public static void sendToServer(IMessage message) {
        CHANNEL.sendToServer(message);
    }

    /** Send a packet from the server to a specific player. */
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        CHANNEL.sendTo(message, player);
    }

    /** Send a packet from the server to all players within range of a position. */
    public static void sendToAllAround(IMessage message, World world, BlockPos pos, double range) {
        CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(
            world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range));
    }

    private Network() {}
}


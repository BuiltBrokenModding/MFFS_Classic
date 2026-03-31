package dev.su5ed.mffs.proxy;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.client.ClientZoneTracker;
import dev.su5ed.mffs.network.ClientPacketHandler;
import dev.su5ed.mffs.network.DrawBeamPacket;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.IMAZoneSyncPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SetStructureShapePacket;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.network.UpdateBlockEntityPacket;
import dev.su5ed.mffs.render.CustomProjectorModeClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void handleUpdateBlockEntity(UpdateBlockEntityPacket pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() ->
            Network.findTileEntity(ForceFieldBlockEntity.class, mc.world, pkt.getPos())
                .ifPresent(be -> be.handleCustomUpdateTag(pkt.getData())));
    }

    @Override
    public void handleUpdateAnimationSpeed(UpdateAnimationSpeed pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() ->
            Network.findTileEntity(ProjectorBlockEntity.class, mc.world, pkt.getPos())
                .ifPresent(be -> be.setClientAnimationSpeed(pkt.getAnimationSpeed())));
    }

    @Override
    public void handleSetStructureShape(SetStructureShapePacket pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() ->
            CustomProjectorModeClientHandler.setShape(pkt.getDimension(), pkt.getStructId(), pkt.getShape()));
    }

    @Override
    public void handleDrawBeam(DrawBeamPacket pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> ClientPacketHandler.handleDrawBeamPacket(pkt));
    }

    @Override
    public void handleDrawHologram(DrawHologramPacket pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> ClientPacketHandler.handleDrawHologramPacket(pkt));
    }

    @Override
    public void handleIMAZoneSync(IMAZoneSyncPacket pkt) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.addScheduledTask(() -> ClientZoneTracker.updateZone(
            pkt.getPos(),
            pkt.getActionRange(),
            pkt.getWarningRange(),
            pkt.getZoneType(),
            pkt.isActive()));
    }
}

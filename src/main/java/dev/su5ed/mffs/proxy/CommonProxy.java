package dev.su5ed.mffs.proxy;

import dev.su5ed.mffs.network.DrawBeamPacket;
import dev.su5ed.mffs.network.DrawHologramPacket;
import dev.su5ed.mffs.network.IMAZoneSyncPacket;
import dev.su5ed.mffs.network.SetStructureShapePacket;
import dev.su5ed.mffs.network.UpdateAnimationSpeed;
import dev.su5ed.mffs.network.UpdateBlockEntityPacket;

public class CommonProxy {
    public void handleUpdateBlockEntity(UpdateBlockEntityPacket pkt) {}
    public void handleUpdateAnimationSpeed(UpdateAnimationSpeed pkt) {}
    public void handleSetStructureShape(SetStructureShapePacket pkt) {}
    public void handleDrawBeam(DrawBeamPacket pkt) {}
    public void handleDrawHologram(DrawHologramPacket pkt) {}
    public void handleIMAZoneSync(IMAZoneSyncPacket pkt) {}
}

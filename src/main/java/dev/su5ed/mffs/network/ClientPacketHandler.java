package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public final class ClientPacketHandler {
    
    public static void handleToggleActivationPacket(ToggleModePacketClient packet) {
        Network.findBlockEntity(Activatable.class, Minecraft.getInstance().level, packet.pos())
            .ifPresent(be -> be.setActive(packet.active()));
    }
    
    public static void handleDrawBeamPacket(DrawBeamPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 pos = packet.position();
        minecraft.level.addParticle(new BeamParticleOptions(packet.target(), packet.color(), packet.lifetime()), pos.x(), pos.y(), pos.z(), 0, 0, 0);
    }

    private ClientPacketHandler() {}
}

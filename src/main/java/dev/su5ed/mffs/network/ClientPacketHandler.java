package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.particle.BeamColor;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.render.particle.MovingHologramParticleOptions;
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
    
    public static void handleUpdateAnimationSpeedPacket(UpdateAnimationSpeed packet) {
        Network.findBlockEntity(ProjectorBlockEntity.class, Minecraft.getInstance().level, packet.pos())
            .ifPresent(be -> be.clientAnimationSpeed = packet.animationSpeed());
    }
    
    public static void handleDisintegrateBlockPacket(DisintegrateBlockPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        int type = packet.type();
        Vec3 pos = packet.pos().add(0.5, 0.5, 0.5);
        Vec3 target = packet.target();
        Vec3 targetCenter = packet.target().add(0.5, 0.5, 0.5);
        if (type == 1) {
            minecraft.level.addParticle(new BeamParticleOptions(targetCenter, BeamColor.BLUE, 40), pos.x(), pos.y(), pos.z(), 0, 0, 0);
            minecraft.level.addParticle(new MovingHologramParticleOptions(target, BeamColor.WHITE, 40), target.x(), target.y(), target.z(), 0, 0, 0);
        }
        else if (type == 2) {
            minecraft.level.addParticle(new BeamParticleOptions(targetCenter, BeamColor.RED, 40), pos.x(), pos.y(), pos.z(), 0, 0, 0);
            minecraft.level.addParticle(new MovingHologramParticleOptions(target, BeamColor.RED, 40), target.x(), target.y(), target.z(), 0, 0, 0);
        }
    }

    private ClientPacketHandler() {}
}

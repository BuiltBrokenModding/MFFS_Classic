package dev.su5ed.mffs.network;

import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.render.particle.MovingHologramParticleOptions;
import dev.su5ed.mffs.render.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
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
            .ifPresent(be -> be.setClientAnimationSpeed(packet.animationSpeed()));
    }

    public static void handleDrawHologramPacket(DrawHologramPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        DrawHologramPacket.Type type = packet.type();
        Vec3 pos = packet.pos().add(0.5, 0.5, 0.5);
        Vec3 target = packet.target();
        Vec3 targetCenter = packet.target().add(0.5, 0.5, 0.5);
        if (type == DrawHologramPacket.Type.CONSTRUCT) {
            minecraft.level.addParticle(new BeamParticleOptions(targetCenter, ParticleColor.BLUE_BEAM, 40), pos.x(), pos.y(), pos.z(), 0, 0, 0);
            minecraft.level.addParticle(new MovingHologramParticleOptions(ParticleColor.BLUE_FIELD, 40), target.x(), target.y(), target.z(), 0, 0, 0);
        } else if (type == DrawHologramPacket.Type.DESTROY) {
            minecraft.level.addParticle(new BeamParticleOptions(targetCenter, ParticleColor.RED, 40), pos.x(), pos.y(), pos.z(), 0, 0, 0);
            minecraft.level.addParticle(new MovingHologramParticleOptions(ParticleColor.RED, 40), target.x(), target.y(), target.z(), 0, 0, 0);
        }
    }

    public static void handleBlockEntityUpdatePacket(UpdateBlockEntityPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockEntity be = minecraft.level.getBlockEntity(packet.pos());
        if (be != null) {
            be.handleUpdateTag(packet.data());
        }
    }

    private ClientPacketHandler() {}
}

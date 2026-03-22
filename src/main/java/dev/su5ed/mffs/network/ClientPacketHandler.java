package dev.su5ed.mffs.network;

import dev.su5ed.mffs.render.particle.BeamParticle;
import dev.su5ed.mffs.render.particle.MovingHologramParticle;
import dev.su5ed.mffs.render.particle.ParticleColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientPacketHandler {

    public static void handleDrawBeamPacket(DrawBeamPacket packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return;
        Vec3d pos = packet.getPosition();
        BeamParticle particle = new BeamParticle(
            mc.world, pos, packet.getTarget(), packet.getColor(), packet.getLifetime());
        mc.effectRenderer.addEffect(particle);
    }

    public static void handleDrawHologramPacket(DrawHologramPacket packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return;
        DrawHologramPacket.HoloType type = packet.getHoloType();
        Vec3d pos    = new Vec3d(packet.getPos().x + 0.5, packet.getPos().y + 0.5, packet.getPos().z + 0.5);
        Vec3d target = packet.getTarget();
        Vec3d tc     = new Vec3d(target.x + 0.5, target.y + 0.5, target.z + 0.5);
        if (type == DrawHologramPacket.HoloType.CONSTRUCT) {
            // Spawn blue beam from projector to target (neutral texture — color tint renders cleanly)
            BeamParticle beam = new BeamParticle(mc.world, pos, tc, ParticleColor.BLUE_BEAM, 40, BeamParticle.TEXTURE_NEUTRAL);
            mc.effectRenderer.addEffect(beam);
            // Spawn hologram cube at target position (materializing effect)
            MovingHologramParticle holo = new MovingHologramParticle(mc.world, target, ParticleColor.BLUE_FIELD, 40);
            mc.effectRenderer.addEffect(holo);
        } else if (type == DrawHologramPacket.HoloType.DESTROY) {
            // Spawn red beam from projector to target (neutral texture — color tint renders cleanly)
            BeamParticle beam = new BeamParticle(mc.world, pos, tc, ParticleColor.RED, 40, BeamParticle.TEXTURE_NEUTRAL);
            mc.effectRenderer.addEffect(beam);
            // Spawn hologram cube at target position (dematerializing effect)
            MovingHologramParticle holo = new MovingHologramParticle(mc.world, target, ParticleColor.RED, 40);
            mc.effectRenderer.addEffect(holo);
        }
    }

    private ClientPacketHandler() {}
}

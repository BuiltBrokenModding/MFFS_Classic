package dev.su5ed.mffs.render.particle;

import dev.su5ed.mffs.render.ModRenderType;
import net.minecraft.client.particle.ParticleRenderType;

public final class ModParticleRenderType {
    public static final ParticleRenderType HOLO = new ParticleRenderType("HOLO", ModRenderType.HOLO_PARTICLE, true);
    public static final ParticleRenderType BEAM = new ParticleRenderType("BEAM", ModRenderType.BEAM_PARTICLE, true);
}

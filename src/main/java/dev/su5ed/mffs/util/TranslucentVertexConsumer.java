package dev.su5ed.mffs.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.su5ed.mffs.render.particle.ParticleColor;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.Nullable;

public final class TranslucentVertexConsumer extends VertexConsumerWrapper {
    private final VertexConsumer wrapped;
    private final ParticleColor color;
    private final int alpha;

    public TranslucentVertexConsumer(VertexConsumer wrapped, @Nullable ParticleColor color, int alpha) {
        super(wrapped);
        this.wrapped = wrapped;
        this.color = color;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this.wrapped.setColor(
            this.color != null ? this.color.getRedAsInt() : red,
            this.color != null ? this.color.getGreenAsInt() : green,
            this.color != null ? this.color.getBlueAsInt() : blue,
            alpha * this.alpha / 0xFF
        );
    }

    @Override
    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        return super.setColor(
            this.color != null ? this.color.getRed() : red,
            this.color != null ? this.color.getGreen(): green,
            this.color != null ? this.color.getBlue() : blue,
            alpha * this.alpha / 0xFF
        );
    }

    @Override
    public VertexConsumer setColor(int packedColor) {
        return super.setColor(
            ARGB.color(ARGB.alpha(packedColor) * this.alpha / 0xFF,
                this.color != null ? this.color.getRedAsInt() : ARGB.red(packedColor),
                this.color != null ? this.color.getGreenAsInt() : ARGB.green(packedColor),
                this.color != null ? this.color.getBlueAsInt() : ARGB.blue(packedColor)
            ));
    }
}

package dev.su5ed.mffs.util;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class ModFluidType extends FluidType {
    public ModFluidType(FluidProperties properties) {
        super(properties.properties);
    }
    
    public record FluidRenderInfo(
        Identifier stillTexture,
        Identifier flowingTexture,
        Identifier overlayTexture,
        Identifier renderOverlayTexture,
        int tintColor
    ) {}

    public static class FluidProperties {
        private final Properties properties;

        private Identifier stillTexture;
        private Identifier flowingTexture;
        private Identifier overlayTexture;
        private Identifier renderOverlayTexture;
        private int tintColor = 0xFFFFFFFF;

        public FluidProperties() {
            this(Properties.create()
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY));
        }

        public FluidProperties(Properties properties) {
            this.properties = properties;
        }

        public FluidProperties density(int density) {
            this.properties.density(density);
            return this;
        }

        public FluidProperties lightLevel(int lightLevel) {
            this.properties.lightLevel(lightLevel);
            return this;
        }

        public FluidProperties texture(Identifier texture) {
            stillTexture(texture);
            flowingTexture(texture);
            return this;
        }

        public FluidProperties stillTexture(Identifier stillTexture) {
            this.stillTexture = stillTexture;
            return this;
        }

        public FluidProperties flowingTexture(Identifier flowingTexture) {
            this.flowingTexture = flowingTexture;
            return this;
        }

        public FluidProperties overlayTexture(Identifier overlayTexture) {
            this.overlayTexture = overlayTexture;
            return this;
        }

        public FluidProperties renderOverlayTexture(Identifier renderOverlayTexture) {
            this.renderOverlayTexture = renderOverlayTexture;
            return this;
        }

        public FluidProperties tintColor(int tintColor) {
            this.tintColor = tintColor;
            return this;
        }

        public FluidRenderInfo build() {
            return new FluidRenderInfo(stillTexture, flowingTexture, overlayTexture, renderOverlayTexture, tintColor);
        }
    }
}

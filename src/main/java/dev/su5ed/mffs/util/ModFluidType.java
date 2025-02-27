package dev.su5ed.mffs.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class ModFluidType extends FluidType {
    public ModFluidType(FluidProperties properties) {
        super(properties.properties);
    }
    
    public record FluidRenderInfo(
        ResourceLocation stillTexture,
        ResourceLocation flowingTexture,
        ResourceLocation overlayTexture,
        ResourceLocation renderOverlayTexture,
        int tintColor
    ) {}

    public static class FluidProperties {
        private final Properties properties;

        private ResourceLocation stillTexture;
        private ResourceLocation flowingTexture;
        private ResourceLocation overlayTexture;
        private ResourceLocation renderOverlayTexture;
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

        public FluidProperties texture(ResourceLocation texture) {
            stillTexture(texture);
            flowingTexture(texture);
            return this;
        }

        public FluidProperties stillTexture(ResourceLocation stillTexture) {
            this.stillTexture = stillTexture;
            return this;
        }

        public FluidProperties flowingTexture(ResourceLocation flowingTexture) {
            this.flowingTexture = flowingTexture;
            return this;
        }

        public FluidProperties overlayTexture(ResourceLocation overlayTexture) {
            this.overlayTexture = overlayTexture;
            return this;
        }

        public FluidProperties renderOverlayTexture(ResourceLocation renderOverlayTexture) {
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

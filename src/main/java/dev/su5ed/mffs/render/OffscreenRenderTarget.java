package dev.su5ed.mffs.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;

public class OffscreenRenderTarget extends RenderTarget {
   public OffscreenRenderTarget(int width, int height) {
      super(true);
      RenderSystem.assertOnRenderThreadOrInit();
      resize(width, height);
   }
}

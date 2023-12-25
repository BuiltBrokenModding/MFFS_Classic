package dev.su5ed.mffs.mixin;

import dev.su5ed.mffs.render.RenderPostProcessor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void onResizeDisplay(CallbackInfo ci) {
        RenderPostProcessor.resizeDisplay();
    }
}

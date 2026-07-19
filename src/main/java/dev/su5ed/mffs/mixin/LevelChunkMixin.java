package dev.su5ed.mffs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"))
    private void onRemoveBlockEntity(CallbackInfo ci, @Local BlockEntity be) {
        if (be instanceof BaseBlockEntity baseBe) {
            baseBe.beforeBlockEntityRemoved();
        }
    }
}

package dev.su5ed.mffs.mixin;

import dev.su5ed.mffs.blockentity.BaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {
    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;setRemoved()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onRemoveBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity be) {
        if (be instanceof BaseBlockEntity baseBe) {
            baseBe.beforeBlockEntityRemoved();
        }
    }
}

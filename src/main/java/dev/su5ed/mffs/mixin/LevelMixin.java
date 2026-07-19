package dev.su5ed.mffs.mixin;

import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "RETURN"))
    private void onSetBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            ModUtil.onSetBlock((Level) (Object) this, pPos, pState);
        }
    }
}

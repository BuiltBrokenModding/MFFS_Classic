package dev.su5ed.mffs.mixin;

import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class LevelMixin {
    @Inject(
        method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z",
        at = @At("TAIL")
    )
    private void onSetBlock(BlockPos pos, IBlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
        ModUtil.onSetBlock((World)(Object)this, pos, state);
    }
}

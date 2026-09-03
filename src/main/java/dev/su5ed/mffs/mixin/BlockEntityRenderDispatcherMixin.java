package dev.su5ed.mffs.mixin;

import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {
    @Inject(method = "getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity, S extends BlockEntityRenderState> void getRendererMixin(E blockEntity, CallbackInfoReturnable<BlockEntityRenderer<E, S>> cir) {
        if (blockEntity instanceof ForceFieldBlockEntity forceFieldBlockEntity) {
            if (forceFieldBlockEntity.getCamouflage() == null || !(forceFieldBlockEntity.getCamouflage().getBlock() instanceof EntityBlock)) {
                cir.setReturnValue(null);
            }
        }
    }
}

package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.ModObjects;
import dev.su5ed.mffs.render.BeamParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MachineBlockEntity extends BlockEntity {
    private boolean enabled = false;
    private long tickCounter;

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.MACHINE_BLOCK_ENTITY.get(), pos, state);
    }
    
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        this.enabled = !this.enabled;
        return InteractionResult.SUCCESS;
    }
    
    public void tickClient() {
        if (this.enabled && this.tickCounter % 10 == 0 && this.level instanceof ClientLevel clientLevel) {
            addBeamParticle(clientLevel);
        }
        
        ++this.tickCounter;
    }
    
    public void tickServer() {
        
    }
    
    private void addBeamParticle(ClientLevel clientLevel) {
        Vec3 target = new Vec3(this.worldPosition.getX() + 8.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5);
        clientLevel.addParticle(new BeamParticleOptions(target, 20), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 0, 0, 0);   
    }
}

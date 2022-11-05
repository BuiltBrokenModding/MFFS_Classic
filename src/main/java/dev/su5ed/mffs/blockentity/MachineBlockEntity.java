package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MachineBlockEntity extends BaseBlockEntity {
    private boolean enabled = false;

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.MACHINE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        this.enabled = !this.enabled;
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tickClient() {
        super.tickClient();

        if (this.enabled && getTicks() % 10 == 0 && this.level instanceof ClientLevel clientLevel) {
            addBeamParticle(clientLevel);
        }
    }

    private void addBeamParticle(ClientLevel clientLevel) {
        Vec3 target = new Vec3(this.worldPosition.getX() + 8.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5);
        clientLevel.addParticle(new BeamParticleOptions(target, 20), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 0, 0, 0);
    }
}

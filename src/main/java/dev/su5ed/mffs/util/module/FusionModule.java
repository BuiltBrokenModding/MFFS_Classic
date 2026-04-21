package dev.su5ed.mffs.util.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.TargetPosPair;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.FrequencyGrid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FusionModule extends BaseModule {
    private final ConcurrentLinkedQueue<BlockPos> removingBlocks = new ConcurrentLinkedQueue<>();

    public FusionModule(ModuleType<?> type, ItemStack stack) {
        super(type, stack);
    }

    @Override
    public void beforeSelect(Projector projector, Collection<? extends TargetPosPair> field) {
        TileEntity be = projector.be();
        FortronStorage myFortron = be.getCapability(ModCapabilities.FORTRON, null);
        if (myFortron == null) return;
        int frequency = myFortron.getFrequency();
        World world = be.getWorld();
        for (FortronStorage storage : FrequencyGrid.instance(world.isRemote).get(frequency)) {
            TileEntity owner = storage.getOwner();
            if (!owner.hasCapability(ModCapabilities.PROJECTOR, null)) continue;
            Projector compareProjector = owner.getCapability(ModCapabilities.PROJECTOR, null);
            if (compareProjector != null && compareProjector != projector
                && owner.getWorld() == world && compareProjector.isCachedActive()
                && compareProjector.getMode().isPresent()) {
                for (Iterator<? extends TargetPosPair> it = field.iterator(); it.hasNext(); ) {
                    BlockPos pos = it.next().pos();
                    if (compareProjector.getMode().get().isInField(compareProjector, new Vec3d(pos))) {
                        this.removingBlocks.add(pos);
                        it.remove();
                    }
                }
            }
        }
    }

    @Override
    public void beforeProject(Projector projector) {
        World world = projector.be().getWorld();
        BlockPos pos;
        while ((pos = this.removingBlocks.poll()) != null) {
            if (world.getBlockState(pos).getBlock() == ModBlocks.FORCE_FIELD) {
                world.setBlockToAir(pos);
            }
        }
    }
}

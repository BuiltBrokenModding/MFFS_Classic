package dev.su5ed.mffs.util.projector;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Set;

public class CustomProjectorMode implements ProjectorMode {
    @Override
    public Set<Vec3> getExteriorPoints(Projector projector) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Vec3> getInteriorPoints(Projector projector) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isInField(Projector projector, Vec3 position) {
        throw new NotImplementedException();
    }

    public Map<BlockPos, Block> getFieldBlockMap(Projector projector, ItemStack itemStack) {
        throw new NotImplementedException();
    }
}

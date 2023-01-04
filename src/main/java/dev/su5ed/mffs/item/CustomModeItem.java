package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.Set;

public class CustomModeItem extends ProjectorModeItem {

    public CustomModeItem(Properties properties) {
        super(properties);
    }

    @Override
    public <T extends BlockEntity & Projector> Set<Vec3> getExteriorPoints(T projector) {
        throw new NotImplementedException();
    }

    @Override
    public Set<BlockPos> getInteriorPoints(Projector projector) {
        throw new NotImplementedException();
    }
    
    public Map<BlockPos, Block> getFieldBlockMap(Projector projector, ItemStack itemStack) {
        throw new NotImplementedException();
    }
}

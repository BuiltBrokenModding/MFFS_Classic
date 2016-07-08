package com.mffs.common.items.modules.projector;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.ItemModule;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Calclavia
 */
public class ModuleArray extends ItemModule {

    public ModuleArray() {
        setCost(3F);
    }

    /**
     * @param projector   The projector associated with these blocks.
     * @param fieldBlocks A set of blocks given by the vectors.
     * @return
     */
    @Override
    public Set<Vector3D> onPreCalculate(IFieldInteraction projector, Set<Vector3D> fieldBlocks) {
        Set<Vector3D> newField = new HashSet(fieldBlocks);
        Set<Vector3D> originalField = new HashSet(fieldBlocks);

        HashMap<ForgeDirection, Integer> longestDirectional = getDirectionWidthMap(originalField);

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int copyAmount = projector.getSidedModuleCount(this.getClass(), direction);
            int directionalDisplacement = Math.abs(longestDirectional.get(direction)) + Math.abs(longestDirectional.get(direction.getOpposite())) + 1;
            int directionalDisplacementScale;
            for (int i = 0; i < copyAmount; i++) {
                directionalDisplacementScale = directionalDisplacement * (i + 1);

                for (Vector3D originalFieldBlock : originalField) {
                    Vector3D newFieldBlock = originalFieldBlock.clone().translate(direction.offsetX, direction.offsetY, direction.offsetZ).scale(directionalDisplacementScale);
                    newField.add(newFieldBlock);
                }
            }
        }

        return newField;
    }

    /**
     * @param field
     * @return
     */
    public HashMap<ForgeDirection, Integer> getDirectionWidthMap(Set<Vector3D> field) {
        HashMap<ForgeDirection, Integer> longestDirectional = new HashMap();
        longestDirectional.put(ForgeDirection.DOWN, 0);
        longestDirectional.put(ForgeDirection.UP, 0);
        longestDirectional.put(ForgeDirection.NORTH, 0);
        longestDirectional.put(ForgeDirection.SOUTH, 0);
        longestDirectional.put(ForgeDirection.WEST, 0);
        longestDirectional.put(ForgeDirection.EAST, 0);

        for (Vector3D fieldPosition : field) {
            if ((fieldPosition.x > 0) && (fieldPosition.x > longestDirectional.get(ForgeDirection.EAST))) {
                longestDirectional.put(ForgeDirection.EAST, (int) Math.floor(fieldPosition.x));
            } else if ((fieldPosition.x < 0) && (fieldPosition.x < longestDirectional.get(ForgeDirection.WEST))) {
                longestDirectional.put(ForgeDirection.WEST, (int) Math.floor(fieldPosition.x));
            }

            if ((fieldPosition.y > 0) && (fieldPosition.y > longestDirectional.get(ForgeDirection.UP))) {
                longestDirectional.put(ForgeDirection.UP, (int) Math.floor(fieldPosition.y));
            } else if ((fieldPosition.y < 0) && (fieldPosition.y < longestDirectional.get(ForgeDirection.DOWN))) {
                longestDirectional.put(ForgeDirection.DOWN, (int) Math.floor(fieldPosition.y));
            }

            if ((fieldPosition.z > 0) && (fieldPosition.z > longestDirectional.get(ForgeDirection.SOUTH))) {
                longestDirectional.put(ForgeDirection.SOUTH, (int) Math.floor(fieldPosition.z));
            } else if ((fieldPosition.z < 0) && (fieldPosition.z < longestDirectional.get(ForgeDirection.NORTH))) {
                longestDirectional.put(ForgeDirection.NORTH, (int) Math.floor(fieldPosition.z));
            }
        }

        return longestDirectional;
    }

    @Override
    public float getFortronCost(float amplifier) {
        return super.getFortronCost(amplifier) + super.getFortronCost(amplifier) * amplifier / 100.0F;
    }

}

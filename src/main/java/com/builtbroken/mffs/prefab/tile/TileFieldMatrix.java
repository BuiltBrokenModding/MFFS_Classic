package com.builtbroken.mffs.prefab.tile;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.vector.Point;
import com.builtbroken.mffs.api.IFieldInteraction;
import com.builtbroken.mffs.api.event.EventTimedTask;
import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.modules.IProjectorMode;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.modules.projector.ItemModuleInvert;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleTranslate;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author Calclavia, DarkCow
 */
public abstract class TileFieldMatrix extends TileModuleAcceptor implements IFieldInteraction
{

    /* Rotation matrix */
    public static final int[][] RELATIVE_MATRIX = {{3, 2, 1, 0, 5, 4}, {4, 5, 0, 1, 2, 3}, {0, 1, 3, 2, 4, 5}, {0, 1, 2, 3, 5, 4}, {0, 1, 5, 4, 3, 2}, {0, 1, 4, 5, 2, 3}};
    /* Center of the matrix */
    public static final Point MATRIX_CENTER = new Point(110, 55);
    /* The start slot for the module */
    protected static final int MODULE_SLOT_ID = 1;
    /* SLot ids based on direction ordinal */
    private static final int[][] SLOT_DIRECTIONS = {
            {12, 13},
            {10, 11},
            {4, 5},
            {2, 3},
            {6, 7},
            {8, 9}
    };
    /* The slots the modules occupy */
    public static int[] MODULE_SLOTS = {
            /* Upgrade Module Slots */
            //0, 1, 2, 3, 4, 5,
            /* Scale Slots */
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24
    };
    /* Holds information on positions that have been finished */
    protected final Set<Vector3D> calculatedFields = Collections.synchronizedSet(new HashSet<Vector3D>());
    /* Tasks that have been stored */
    private final List<EventTimedTask> eventsQueued = new LinkedList<>();  //TODO implement as QUEUE instead
    /** Ignores machine rotation when set to true */
    public boolean useAbsoluteDirection = true;
    /** Are we calculating the blocks for the field */
    protected boolean isCalculatingField;
    /** Are we done calculating the field shape */
    protected boolean isFinishedCalculatingField;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote)
        {
            //Handle events TODO phase out for delay action system, maybe?
            Iterator<EventTimedTask> iterator = eventsQueued.iterator();
            while (iterator.hasNext())
            {
                //Next
                EventTimedTask task = iterator.next();

                //Do event
                task.tick();

                //Remove
                if (!task.isActive())
                {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Calculates the forcefield locations.
     */
    public void triggerFieldCalculation()
    {
        if (!isCalculatingField && !getWorldObj().isRemote)
        {
            synchronized (this.calculatedFields) //TODO move to worker thread?
            {
                if (getMode() != null)
                {
                    //Reset
                    this.calculatedFields.clear();

                    //TODO phase this out, we should not create a new thread each time we want to do work
                    new Thread(() -> {

                        final TileFieldMatrix entity = TileFieldMatrix.this;

                        //Start
                        entity.setCalculating(true);

                        //DO work
                        try
                        {
                            IProjectorMode mode = entity.getMode();
                            if (mode != null)
                            {
                                //Generate data
                                Set<Vector3D> positions = entity.getModuleCount(ItemModuleInvert.class) > 0 ? mode.getInteriorPoints(entity) : mode.getExteriorPoints(entity);

                                //Get translation point
                                Vector3D translation = entity.getTranslation();

                                //Trigger modules pre
                                for (IFieldModule module : entity.getModules())
                                {
                                    positions = module.onPreCalculate(entity, positions);
                                }

                                //Merge data, and translate based on position
                                for (Vector3D position : positions)
                                {
                                    //Offset by projector
                                    position.translate(new Vector3D((IPos3D) entity));

                                    //Offset by translation
                                    position.translate(translation);

                                    //Ensure is inside world
                                    if (position.intY() <= entity.getWorldObj().getHeight())
                                    {
                                        //Add to calculated list
                                        entity.getCalculatedField().add(position.round());
                                    }
                                }

                                //Trigger modules post
                                for (IFieldModule module : entity.getModules())
                                {
                                    module.onCalculate(entity, entity.getCalculatedField());
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                        //Prevent editing self
                        entity.getCalculatedField().remove(new Vector3D((IPos3D) entity));

                        //Stop
                        entity.setCalculating(false);

                        //Mark field is ready to generate
                        entity.setCalculated(true);

                        //Complete
                        entity.onCalculationCompletion();

                    }).start();
                }
            }
        }
    }

    public void onCalculationCompletion()
    {
    }

    @Override
    public IProjectorMode getMode()
    {
        ItemStack modeStack = getModeStack();
        if (modeStack != null)
        {
            return ((IProjectorMode) modeStack.getItem());
        }
        return null;
    }

    @Override
    public ItemStack getModeStack()
    {
        ItemStack stack = getStackInSlot(MODULE_SLOT_ID);
        if (stack != null && stack.getItem() instanceof IProjectorMode)
        {
            return stack;
        }
        return null;
    }

    @Override
    public int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection)
    {
        if (paramForgeDirection.ordinal() > SLOT_DIRECTIONS.length)
        {
            return new int[0];
        }
        return SLOT_DIRECTIONS[paramForgeDirection.ordinal()];
    }

    @Override
    public int[] getModuleSlots()
    {
        return MODULE_SLOTS;
    }

    @Override
    public int getSidedModuleCount(Class<? extends IFieldModule> module, ForgeDirection... paramVarArgs)
    {
        int count = 0;
        ForgeDirection[] dirs = (paramVarArgs != null && paramVarArgs.length > 0) ? paramVarArgs : ForgeDirection.VALID_DIRECTIONS;
        for (ForgeDirection dir : dirs)
        {
            count += getModuleCount(module, getSlotsBasedOnDirection(dir));
        }
        return count;
    }

    @Override
    public Vector3D getTranslation()
    {
        ForgeDirection dir = getDirection();
        if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN)
        {
            dir = ForgeDirection.NORTH;
        }
        int zNeg = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.NORTH : getOrient(dir, ForgeDirection.NORTH)));
        int zPos = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.SOUTH : getOrient(dir, ForgeDirection.SOUTH)));
        int xNeg = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.WEST : getOrient(dir, ForgeDirection.WEST)));
        int xPos = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.EAST : getOrient(dir, ForgeDirection.EAST)));
        int yNeg = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(ForgeDirection.DOWN));
        int yPos = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(ForgeDirection.UP));
        return new Vector3D(xPos - xNeg, yPos - yNeg, zPos - zNeg);
    }

    protected ForgeDirection getOrient(ForgeDirection d1, ForgeDirection d2)
    {
        return ForgeDirection.getOrientation(RELATIVE_MATRIX[d1.ordinal()][d2.ordinal()]);
    }

    @Override
    public Vector3D getPositiveScale()
    {
        ForgeDirection direction = getDirection();
        if (!useAbsoluteDirection && (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN))
        {
            direction = ForgeDirection.NORTH;
        }

        //Gets scale in position directions, combined with negative direction for full size
        int zScalePos = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.SOUTH : getOrient(direction, ForgeDirection.SOUTH)));
        int xScalePos = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.EAST : getOrient(direction, ForgeDirection.EAST)));
        int yScalePos = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(ForgeDirection.UP));

        int omnidirectionalScale = getModuleCount(ItemModuleScale.class, getModuleSlots());

        Vector3D positiveScale = new Vector3D(xScalePos + omnidirectionalScale, yScalePos + omnidirectionalScale, zScalePos + omnidirectionalScale);

        return positiveScale;
    }

    @Override
    public Vector3D getNegativeScale()
    {
        ForgeDirection direction = getDirection();
        if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
        {
            direction = ForgeDirection.NORTH;
        }

        //Gets scale in negative directions, combined with positive direction for full size
        int zNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.NORTH : getOrient(direction, ForgeDirection.NORTH)));
        int xNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(useAbsoluteDirection ? ForgeDirection.WEST : getOrient(direction, ForgeDirection.WEST)));
        int yNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(ForgeDirection.DOWN));

        int omnidirectionalScale = getModuleCount(ItemModuleScale.class, getModuleSlots());

        Vector3D negScale = new Vector3D(xNeg + omnidirectionalScale, yNeg + omnidirectionalScale, zNeg + omnidirectionalScale);

        return negScale;
    }

    @Override
    public Set<Vector3D> getCalculatedField()
    {
        return this.calculatedFields;
    }

    @Override
    public Set<Vector3D> getInteriorPoints()
    {
        Set<Vector3D> newField = getMode().getInteriorPoints(this);
        Set<Vector3D> returnField = new HashSet();

        Vector3D translation = getTranslation();

        Vector3D thisField = new Vector3D((IPos3D) this);
        for (Vector3D position : newField)
        {
            Vector3D newPosition = position.clone();

            newPosition.translate(thisField);
            newPosition.translate(translation);

            returnField.add(newPosition);
        }

        return returnField;
    }

    @Override
    public void setCalculating(boolean paramBoolean)
    {
        this.isCalculatingField = paramBoolean;
    }

    @Override
    public void setCalculated(boolean paramBoolean)
    {
        this.isFinishedCalculatingField = paramBoolean;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("isAbs", useAbsoluteDirection);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.useAbsoluteDirection = nbt.getBoolean("isAbs");
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeBoolean(useAbsoluteDirection);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        useAbsoluteDirection = buf.readBoolean();
    }

    public List<EventTimedTask> getEventsQueued()
    {
        return eventsQueued;
    }
}

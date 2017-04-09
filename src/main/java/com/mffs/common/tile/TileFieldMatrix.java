package com.mffs.common.tile;

import com.builtbroken.mc.imp.transform.vector.Point;
import com.mffs.api.IFieldInteraction;
import com.mffs.api.event.EventTimedTask;
import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IProjectorMode;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.projector.ItemModuleInvert;
import com.mffs.common.items.modules.upgrades.ItemModuleRotate;
import com.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.mffs.common.items.modules.upgrades.ItemModuleTranslate;
import com.mffs.common.net.packet.EntityToggle;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author Calclavia
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
    private final List<EventTimedTask> eventsQueued = new LinkedList<>();
    /* Deteremines if the machine is absolute */
    public boolean isAbs = true;
    /* Holds the state of this machine */
    protected boolean isCalc, isFinished;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.worldObj.isRemote)
        {
            return; //events only need to process serverside!
        }
        for (Iterator<EventTimedTask> it$ = eventsQueued.iterator(); it$.hasNext(); )
        {
            EventTimedTask task = it$.next();
            task.tick();
            if (!task.isActive())
            {
                it$.remove();
            }
        }
    }

    /**
     * Calculates the forcefield locations.
     */
    public void calculatedForceField()
    {
        if (!isCalc && !getWorldObj().isRemote)
        {
            synchronized (this.calculatedFields)
            {
                if (getMode() != null)
                {
                    this.calculatedFields.clear();
                    new Thread(() -> {
                        TileFieldMatrix entity = TileFieldMatrix.this;
                        entity.setCalculating(true);
                        try
                        {
                            IProjectorMode mode = entity.getMode();
                            if (mode != null)
                            {

                                Set<Vector3D> blocks = entity.getModuleCount(ItemModuleInvert.class) > 0 ? mode.getInteriorPoints(entity) : mode.getExteriorPoints(entity);
                                Vector3D translation = entity.getTranslation();

                                int rotationYaw = entity.getRotationYaw();
                                int rotationPitch = entity.getRotationPitch();

                                for (IModule module : entity.getModules())
                                {
                                    blocks = module.onPreCalculate(entity, blocks);
                                }

                                for (Vector3D position : blocks)
                                {
                                    if ((rotationYaw != 0) || (rotationPitch != 0))
                                    {
                                        position.rotate(rotationYaw, rotationPitch);
                                    }

                                    position.translate(new Vector3D(entity));
                                    position.translate(translation);

                                    if (position.intY() <= entity.getWorldObj().getHeight())
                                    {
                                        entity.getCalculatedField().add(position.round());
                                    }
                                }

                                for (IModule module : entity.getModules())
                                {
                                    module.onCalculate(entity, entity.getCalculatedField());
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        entity.getCalculatedField().remove(new Vector3D(entity)); //we do not want to overplace this
                        entity.setCalculating(false);
                        entity.setCalculated(true);
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
    public int getSidedModuleCount(Class<? extends IModule> module, ForgeDirection... paramVarArgs)
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
        int zNeg = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(dir, ForgeDirection.NORTH)));
        int zPos = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(dir, ForgeDirection.SOUTH)));
        int xNeg = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(dir, ForgeDirection.WEST)));
        int xPos = getModuleCount(ItemModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(dir, ForgeDirection.EAST)));
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
        if (!isAbs && (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN))
        {
            direction = ForgeDirection.NORTH;
        }

        //Gets scale in position directions, combined with negative direction for full size
        int zScalePos = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(direction, ForgeDirection.SOUTH)));
        int xScalePos = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(direction, ForgeDirection.EAST)));
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
        int zNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(direction, ForgeDirection.NORTH)));
        int xNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(direction, ForgeDirection.WEST)));
        int yNeg = getModuleCount(ItemModuleScale.class, getSlotsBasedOnDirection(ForgeDirection.DOWN));

        int omnidirectionalScale = getModuleCount(ItemModuleScale.class, getModuleSlots());

        Vector3D negScale = new Vector3D(xNeg + omnidirectionalScale, yNeg + omnidirectionalScale, zNeg + omnidirectionalScale);

        return negScale;
    }

    @Override
    public int getRotationYaw()
    {
        ForgeDirection dir = getDirection();
        int yawValue;
        yawValue = getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(dir, ForgeDirection.EAST)));
        yawValue -= getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(dir, ForgeDirection.WEST)));
        yawValue += getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(dir, ForgeDirection.SOUTH)));
        yawValue -= getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(dir, ForgeDirection.NORTH)));
        return yawValue * 2;
    }

    @Override
    public int getRotationPitch()
    {
        return (getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(ForgeDirection.UP)) - getModuleCount(ItemModuleRotate.class, getSlotsBasedOnDirection(ForgeDirection.DOWN))) * 2;
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
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();

        Vector3D thisField = new Vector3D(this);
        for (Vector3D position : newField)
        {
            Vector3D newPosition = position.clone();

            if ((rotationYaw != 0) || (rotationPitch != 0))
            {
                newPosition.rotate(rotationYaw, rotationPitch);
            }

            newPosition.translate(thisField);
            newPosition.translate(translation);

            returnField.add(newPosition);
        }

        return returnField;
    }

    @Override
    public void setCalculating(boolean paramBoolean)
    {
        this.isCalc = paramBoolean;
    }

    @Override
    public void setCalculated(boolean paramBoolean)
    {
        this.isFinished = paramBoolean;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("isAbs", isAbs);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.isAbs = nbt.getBoolean("isAbs");
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage)
    {
        if (imessage instanceof EntityToggle)
        {
            EntityToggle pkt = (EntityToggle) imessage;
            if (pkt.toggle_opcode == EntityToggle.ABSOLUTE_TOGGLE)
            {
                this.isAbs = !this.isAbs;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); //we need to signal that this entity has been changed serverside!
                return null;
            }
        }
        return super.handleMessage(imessage);
    }

    public List<EventTimedTask> getEventsQueued()
    {
        return eventsQueued;
    }
}

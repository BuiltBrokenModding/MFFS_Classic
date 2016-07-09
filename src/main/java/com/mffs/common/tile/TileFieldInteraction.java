package com.mffs.common.tile;

import com.mffs.api.IFieldInteraction;
import com.mffs.api.event.EventTimedTask;
import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IProjectorMode;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.items.modules.upgrades.ModuleInvert;
import com.mffs.common.items.modules.upgrades.ModuleRotate;
import com.mffs.common.items.modules.upgrades.ModuleScale;
import com.mffs.common.items.modules.upgrades.ModuleTranslate;
import com.mffs.common.net.packet.EntityToggle;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author Calclavia
 */
public abstract class TileFieldInteraction extends TileModuleAcceptor implements IFieldInteraction {

    /* Rotation matrix */
    public static final int[][] RELATIVE_MATRIX = {{3, 2, 1, 0, 5, 4}, {4, 5, 0, 1, 2, 3}, {0, 1, 3, 2, 4, 5}, {0, 1, 2, 3, 5, 4}, {0, 1, 5, 4, 3, 2}, {0, 1, 4, 5, 2, 3}};
    /* The start slot for the module */
    protected static final int MODULE_SLOT_ID = 2;
    /* SLot ids based on direction ordinal */
    private static final int[][] SLOT_DIRECTIONS = {
            {6, 14},
            {3, 11},
            {7, 9},
            {8, 10},
            {4, 5},
            {12, 13}
    };
    /* The slots the modules occupy */
    public static int[] MODULE_SLOTS = {15, 16, 17, 18, 19, 20};
    /* Holds information on positions that have been finished */
    protected final Set<Vector3D> calculatedFields = Collections.synchronizedSet(new HashSet<Vector3D>());
    /* Tasks that have been stored */
    private final List<EventTimedTask> delayedEvents = new LinkedList<>();
    private final List<EventTimedTask> eventsQueued = new LinkedList<>();
    /* Deteremines if the machine is absolute */
    public boolean isAbs;
    /* Holds the state of this machine */
    protected boolean isCalc, isFinished;

    @Override
    public void updateEntity() {
        super.updateEntity();
        for (Iterator<EventTimedTask> it$ = eventsQueued.iterator(); it$.hasNext(); ) {
            EventTimedTask task = it$.next();
            task.tick();
            if (!task.isActive()) {
                it$.remove();
            }
        }
        //Infinite, infinite loop of adding to list!
    }

    /**
     * Calculates the forcefield locations.
     */
    public void calculatedForceField() {
        if (!isCalc && !getWorldObj().isRemote) {
            synchronized (this.calculatedFields) {
                if (getMode() != null) {
                    this.calculatedFields.clear();
                    new Thread(() -> {
                        TileFieldInteraction entity = TileFieldInteraction.this;
                        entity.setCalculating(true);
                        try {
                            IProjectorMode mode = entity.getMode();
                            if (mode != null) {
                                Set<Vector3D> blocks = entity.getModuleCount(ModuleInvert.class) > 0 ? mode.getInteriorPoints(entity) : mode.getExteriorPoints(entity);
                                Vector3D translation = entity.getTranslation();
                                int rotationYaw = entity.getRotationYaw();
                                int rotationPitch = entity.getRotationPitch();

                                for (IModule module : entity.getModules())
                                    blocks = module.onPreCalculate(entity, blocks);

                                for (Vector3D position : blocks) {
                                    if ((rotationYaw != 0) || (rotationPitch != 0)) {
                                        position.rotate(rotationYaw, rotationPitch);
                                    }

                                    position.translate(new Vector3D(entity));
                                    position.translate(translation);

                                    if (position.intY() <= entity.getWorldObj().getHeight()) {
                                        entity.getCalculatedField().add(position.round());
                                    }
                                }

                                for (IModule module : entity.getModules()) {
                                    module.onCalculate(entity, entity.getCalculatedField());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        entity.setCalculating(false);
                        entity.setCalculated(true);
                        entity.onCalculationCompletion();
                    }).start();
                }
            }
        }
    }

    public void onCalculationCompletion() {

    }

    @Override
    public IProjectorMode getMode() {
        ItemStack modeStack = getModeStack();
        if (modeStack != null) {
            return ((IProjectorMode) modeStack.getItem());
        }
        return null;
    }

    @Override
    public ItemStack getModeStack() {
        ItemStack stack = getStackInSlot(MODULE_SLOT_ID);
        if (stack != null && stack.getItem() instanceof IProjectorMode) {
            return stack;
        }
        return null;
    }

    @Override
    public int[] getSlotsBasedOnDirection(ForgeDirection paramForgeDirection) {
        if (paramForgeDirection.ordinal() > SLOT_DIRECTIONS.length) {
            return new int[0];
        }
        return SLOT_DIRECTIONS[paramForgeDirection.ordinal()];
    }

    @Override
    public int[] getModuleSlots() {
        return MODULE_SLOTS;
    }

    @Override
    public int getSidedModuleCount(Class<? extends IModule> module, ForgeDirection... paramVarArgs) {
        int count = 0;
        ForgeDirection[] dirs = (paramVarArgs != null && paramVarArgs.length > 0) ? paramVarArgs : ForgeDirection.VALID_DIRECTIONS;
        for (ForgeDirection dir : dirs) {
            count += getModuleCount(module, getSlotsBasedOnDirection(dir));
        }
        return count;
    }

    @Override
    public Vector3D getTranslation() {
        ForgeDirection dir = getDirection();
        if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN)
            dir = ForgeDirection.NORTH;
        int zNeg = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(dir, ForgeDirection.NORTH)));
        int zPos = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(dir, ForgeDirection.SOUTH)));
        int xNeg = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(dir, ForgeDirection.WEST)));
        int xPos = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(dir, ForgeDirection.EAST)));
        int yNeg = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.UP : getOrient(dir, ForgeDirection.UP)));
        int yPos = getModuleCount(ModuleTranslate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.DOWN : getOrient(dir, ForgeDirection.DOWN)));
        return new Vector3D(xPos - xNeg, yPos - yNeg, zPos - zNeg);
    }

    protected ForgeDirection getOrient(ForgeDirection d1, ForgeDirection d2) {
        return ForgeDirection.getOrientation(RELATIVE_MATRIX[d1.ordinal()][d2.ordinal()]);
    }

    @Override
    public Vector3D getPositiveScale() {
        ForgeDirection direction = getDirection();
        if (!isAbs && (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN))
            direction = ForgeDirection.NORTH;

        int zScalePos = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(direction, ForgeDirection.SOUTH)));
        int xScalePos = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(direction, ForgeDirection.EAST)));
        int yScalePos = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(ForgeDirection.UP));

        int omnidirectionalScale = getModuleCount(ModuleScale.class, getModuleSlots());

        Vector3D positiveScale = new Vector3D(xScalePos + omnidirectionalScale, yScalePos + omnidirectionalScale, zScalePos + omnidirectionalScale);

        return positiveScale;
    }

    @Override
    public Vector3D getNegativeScale() {
        ForgeDirection direction = getDirection();
        if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
            direction = ForgeDirection.NORTH;

        int zNeg = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(direction, ForgeDirection.NORTH)));
        int xNeg = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(direction, ForgeDirection.WEST)));
        int yNeg = getModuleCount(ModuleScale.class, getSlotsBasedOnDirection(ForgeDirection.DOWN));

        int omnidirectionalScale = getModuleCount(ModuleScale.class, getModuleSlots());

        Vector3D negScale = new Vector3D(xNeg + omnidirectionalScale, yNeg + omnidirectionalScale, zNeg + omnidirectionalScale);

        return negScale;
    }

    @Override
    public int getRotationYaw() {
        ForgeDirection dir = getDirection();
        int yawValue;
        yawValue = getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.EAST : getOrient(dir, ForgeDirection.EAST)));
        yawValue -= getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.WEST : getOrient(dir, ForgeDirection.WEST)));
        yawValue += getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.SOUTH : getOrient(dir, ForgeDirection.SOUTH)));
        yawValue -= getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(isAbs ? ForgeDirection.NORTH : getOrient(dir, ForgeDirection.NORTH)));
        return yawValue * 2;
    }

    @Override
    public int getRotationPitch() {
        return (getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(ForgeDirection.UP)) - getModuleCount(ModuleRotate.class, getSlotsBasedOnDirection(ForgeDirection.DOWN))) * 2;
    }

    @Override
    public Set<Vector3D> getCalculatedField() {
        return this.calculatedFields;
    }

    @Override
    public Set<Vector3D> getInteriorPoints() {
        Set<Vector3D> newField = getMode().getInteriorPoints(this);
        Set<Vector3D> returnField = new HashSet();

        Vector3D translation = getTranslation();
        int rotationYaw = getRotationYaw();
        int rotationPitch = getRotationPitch();

        Vector3D thisField = new Vector3D(this);
        for (Vector3D position : newField) {
            Vector3D newPosition = position.clone();

            if ((rotationYaw != 0) || (rotationPitch != 0)) {
                newPosition.rotate(rotationYaw, rotationPitch);
            }

            newPosition.translate(thisField);
            newPosition.translate(translation);

            returnField.add(newPosition);
        }

        return returnField;
    }

    @Override
    public void setCalculating(boolean paramBoolean) {
        this.isCalc = paramBoolean;
    }

    @Override
    public void setCalculated(boolean paramBoolean) {
        this.isFinished = paramBoolean;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isAbs", isAbs);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.isAbs = nbt.getBoolean("isAbs");
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage) {
        if (imessage instanceof EntityToggle) {
            EntityToggle pkt = (EntityToggle) imessage;
            if (pkt.toggle_opcode == EntityToggle.ABSOLUTE_TOGGLE) {
                this.isAbs = !this.isAbs;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); //we need to signal that this entity has been changed serverside!
                return null;
            }
        }
        return super.handleMessage(imessage);
    }

    public List<EventTimedTask> getEventsQueued() {
        return eventsQueued;
    }
}

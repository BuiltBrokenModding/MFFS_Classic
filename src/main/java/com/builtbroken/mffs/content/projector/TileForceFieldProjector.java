package com.builtbroken.mffs.content.projector;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.vector.BlockPos;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.MFFSSettings;
import com.builtbroken.mffs.api.IProjector;
import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.modules.IProjectorMode;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.modules.projector.ItemModuleDisintegration;
import com.builtbroken.mffs.common.items.modules.projector.ItemModuleSilence;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleSpeed;
import com.builtbroken.mffs.common.net.packet.BeamRequest;
import com.builtbroken.mffs.common.net.packet.ForcefieldCalculation;
import com.builtbroken.mffs.content.field.BlockForceField;
import com.builtbroken.mffs.content.field.TileForceField;
import com.builtbroken.mffs.prefab.ModuleInventory;
import com.builtbroken.mffs.prefab.item.ItemMode;
import com.builtbroken.mffs.prefab.tile.TileFieldMatrix;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Calclavia, DarkCow
 */
public class TileForceFieldProjector extends TileFieldMatrix implements IProjector
{
    /** List of field blocks placed into the world */
    protected final Set<BlockPos> placedBlocks = new HashSet();

    public boolean modulesRequireTick; //TODO document
    public boolean markFieldUpdate = true; //TODO document
    /* Flag indicating if this entity has finished */ //TODO finished what?
    private boolean isFieldCompleted;

    public TileForceFieldProjector()
    {
        this.moduleInventory = new ModuleInventory(this, 1, getSizeInventory());
    }

    @Override
    public void start()
    {
        super.start();
        triggerFieldCalculation();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        destroyField();
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (isActive() && getMode() != null && (!MFFSSettings.PROJECTOR_USE_POWER || requestFortron(getFortronCost(), false) >= getFortronCost())) //TODO instead of failing start removing blocks randomly
        {
            if (MFFSSettings.PROJECTOR_USE_POWER)
            {
                int cost = getFortronCost();
                //TODO separate out field cost from module cost
                //TODO if fail module cost, field should die slowly (decay vs instant death)
                //TODO if field cost fails, blocks should decay randomly
                requestFortron(cost, true);
            }

            if (!this.worldObj.isRemote)
            {
                if (this.ticks % 10 == 0 || markFieldUpdate || modulesRequireTick)
                {
                    if (!this.isFinishedCalculatingField)
                    {
                        triggerFieldCalculation();
                    }
                    else
                    {
                        projectField();
                    }
                }
            }
            else
            {
                this.animation += getFortronCost() / 10; //TODO why is this based on cost?

                if (this.ticks % 40 == 0 && getModuleCount(ItemModuleSilence.class) <= 0) //TODO move to event that can trigger audio and effects
                {
                    this.worldObj.playSoundEffect(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, MFFS.DOMAIN + ":field", 0.6F, 1.0F - this.worldObj.rand.nextFloat() * 0.1F);
                }
            }
        }
        else if (!this.worldObj.isRemote)
        {
            destroyField();
        }
    }

    /**
     * Calculates the forcefield locations.
     */
    @Override
    public void triggerFieldCalculation()
    {
        super.triggerFieldCalculation();
        this.isFieldCompleted = false;
        this.modulesRequireTick = false;

        Set<ItemStack> modules = getModuleStacks();
        for (ItemStack stack : modules)
        {
            if (((IFieldModule) stack.getItem()).doesRequireUpdate(stack)) //TODO seems to be unused?
            {
                modulesRequireTick = true;
                return;
            }
        }
    }

    @Override
    public void onCalculationCompletion()
    {
    }

    @Override
    public int getSizeInventory()
    {
        return 32;
    }

    @Override
    public int getProjectionSpeed()
    {
        return 28 + 28 * getModuleCount(ItemModuleSpeed.class, getModuleSlots()); //TODO add configs
    }

    @Override
    public long getTicks()
    {
        return this.ticks;
    }

    /**
     * @return
     */
    @Override
    public int calculateFortronCost()
    {
        IProjectorMode mode = getMode();
        if (mode != null)
        {
            //Cost just for the blocks
            int costForBlocks = Math.round(MFFSSettings.PROJECTOR_UPKEEP_COST * getForceFields().size());

            //Cost for the modules
            int moduleCost = super.calculateFortronCost() + mode.getFortronCost(getAmplifier());

            //Total
            return Math.round(costForBlocks + moduleCost);
        }
        return 0;
    }

    @Override
    public float getAmplifier()
    {
        return 1;
    }

    @Override
    public Set<BlockPos> getForceFields()
    {
        return placedBlocks;
    }

    /**
     * @param vec
     * @return
     */
    private boolean canReplace(Vector3D vec)
    {
        final Block block = vec.getBlock(this.worldObj);
        if (!(block instanceof BlockForceField))
        {
            if (block != null)
            {
                return block.isReplaceable(worldObj, vec.intX(), vec.intY(), vec.intZ())
                        || getModuleCount(ItemModuleDisintegration.class) > 0 && block.getBlockHardness(worldObj, vec.intX(), vec.intY(), vec.intZ()) >= 0;
            }
            return true; //No block, can place
        }
        return false;
    }

    @Override
    public void projectField()
    {
        if (this.isFinishedCalculatingField && !this.isCalculatingField && (!this.isFieldCompleted || this.markFieldUpdate || this.modulesRequireTick))
        {
            this.markFieldUpdate = false;

            //Get number of blocks we can place this cycle
            int placementLimit = Math.min(getProjectionSpeed(), MFFSSettings.PROJECTOR_BLOCKS_PER_TICK);

            synchronized (this.calculatedFields)
            {
                //Allow modules to modify list
                Set<Vector3D> fieldToBeProjected = this.calculatedFields;
                for (IFieldModule module : getModules(getModuleSlots()))
                {
                    if (module.prePlaceFieldBlock(this, fieldToBeProjected))
                    {
                        return;
                    }
                }
                //TODO sort list to improve placement visual


                //Get force field blocks
                Vector3D projector = new Vector3D((IPos3D) this); //TODO convert to block pos

                //Collect blocks to place TODO convert to queue, store as blocks left to place
                fieldToBeProjected = fieldToBeProjected.stream()
                        .filter(x -> !x.equals(projector) && canReplace(x))
                        .filter(w -> getWorldObj().getChunkFromBlockCoords(w.intX(), w.intZ()).isChunkLoaded) //TODO Chunk loading check likely doesn't work
                        .limit(placementLimit).collect(Collectors.toSet()); //TODO remove stream system, just iterate normally

                //Place force field blocks
                for (Vector3D vec : fieldToBeProjected)
                {
                    int flag = 0; //TODO what is this flag?

                    final int powerCost = MFFSSettings.PROJECTOR_CREATION_COST; //TODO maybe allow modules to change?

                    //Only place block if we have enough power
                    if (consumeFortron(powerCost, false))
                    {
                        //Check with modules if tile can be placed?
                        for (ItemStack stack : getModuleStacks(getModuleSlots()))
                        {
                            if (flag == 0 && stack != null && stack.getItem() instanceof IFieldModule) //TODO why check for zero if there is already a break?
                            {
                                flag = ((IFieldModule) stack.getItem()).prePlaceFieldBlock(this, vec);
                                if (flag != 0)
                                {
                                    break;
                                }
                            }
                        }

                        if (flag != 1 && flag != 2) //TODO what do these flags do?
                        {
                            //Place block
                            placeFieldBlock(vec.intX(), vec.intY(), vec.intZ());

                            //Consume power
                            consumeFortron(powerCost, true);
                        }
                    }
                    else
                    {
                        //No power, stop trying to place blocks
                        break;
                    }
                }

                //Update completed check
                this.isFieldCompleted = (fieldToBeProjected.size() == 0);
            }
        }
    }

    protected boolean consumeFortron(int amount, boolean doAction)
    {
        return !MFFSSettings.PROJECTOR_USE_POWER || requestFortron(amount, doAction) >= amount;
    }

    /**
     * Places a field block at the location
     * <p>
     * Only ckecks if a block is already placed of same type
     *
     * @param x
     * @param y
     * @param z
     */
    protected void placeFieldBlock(int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        Block block = pos.getBlock(worldObj);

        //Only place if not a field block
        if (!(block instanceof BlockForceField))
        {
            //Place block
            if (worldObj.setBlock(x, y, z, BlockForceField.BLOCK_FORCE_FIELD, 0, 2))
            {
                //Track blocks placed
                if (!placedBlocks.contains(pos))
                {
                    this.placedBlocks.add(pos);
                }

                //Update data
                TileEntity entity = worldObj.getTileEntity(x, y, z);
                if (entity instanceof TileForceField)
                {
                    ((TileForceField) entity).setProjectorPosition(this);
                }
            }
        }
        //is field block, check if we own the block but forgot about it
        else
        {
            TileEntity tile = worldObj.getTileEntity(x, y, z);
            if (tile instanceof TileForceField)
            {
                IPos3D projectorPosition = ((TileForceField) tile).getProjectorPosition();

                //No position == free block
                if (projectorPosition == null)
                {
                    ((TileForceField) tile).setProjectorPosition(this);
                }
                //If position results in use, claim the block
                else if (((TileForceField) tile).getProjector() == this)
                {
                    //Track blocks placed
                    if (!placedBlocks.contains(pos))
                    {
                        this.placedBlocks.add(pos);
                    }
                }
            }
        }
    }

    public void destroyFieldBlock(int x, int y, int z)
    {
        //TODO remove block
        queueFieldForPlacement(x, y, z);
    }

    public void queueFieldForPlacement(int x, int y, int z)
    {
        //TODO add to placement queue
        //TODO trigger field to generate
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        destroyField(); //TODO no, field should not destroy each time the tile changes
    }

    @Override
    public void destroyField()
    {
        synchronized (placedBlocks) //TODO check if needed
        {
            //Trigger event on modules
            for (IFieldModule module : getModules(getModuleSlots()))
            {
                if (module.onDestroy(this, getCalculatedField()))
                {
                    break;
                }
            }
            for (BlockPos pos : placedBlocks)
            {
                Block block = pos.getBlock(worldObj);
                if (block instanceof BlockForceField)
                {
                    worldObj.setBlockToAir(pos.xi(), pos.yi(), pos.zi());
                }
            }
        }
        this.placedBlocks.clear();
        this.calculatedFields.clear();
        this.isFieldCompleted = false;
        this.isFinishedCalculatingField = false;
        this.modulesRequireTick = false;
    }

    /**
     * Gets the Filtered stacks based on Items.
     *
     * @return
     */
    public List<Item> getFilterItems() //TODO is this used? if so what for?
    {
        List<Item> stacks = new ArrayList<>();
        for (int slot = 26; slot < 32; slot++) //TODO remove hard coded numbers
        {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null)
            {
                stacks.add(stack.getItem());
            }
        }
        return stacks;
    }

    /**
     * Gets the stacks in the filter.
     *
     * @return
     */
    public List<ItemStack> getFilterStacks() //TODO what is this used for?
    {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 26; slot < 32; slot++) //TODO remove hard coded numbers
        {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null)
            {
                stacks.add(stack);
            }
        }
        return stacks;
    }

    /**
     * @return
     */
    @Override
    public Set<ItemStack> getCards()
    {
        Set<ItemStack> set = new HashSet<>();
        set.add(super.getCard());
        set.add(getStackInSlot(1));
        return set;
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param slot
     * @param stack
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case 0:
                return stack.getItem() instanceof ItemCardFrequency;

            case 1:
                return stack.getItem() instanceof ItemMode;
        }

        if (slot < 26) //TODO remove hard coded numbers
        {
            return stack.getItem() instanceof IFieldModule;
        }
        return true;
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    @Override
    public IMessage handleMessage(IMessage imessage)
    {
        if (imessage instanceof ForcefieldCalculation) //TODO why does the client need to understand the field?
        {
            ForcefieldCalculation calc = (ForcefieldCalculation) imessage;
            getCalculatedField().clear();
            getCalculatedField().addAll(calc.getBlocks());
            this.isCalculatingField = true;
            return null; //we are done!
        }
        else if (imessage instanceof BeamRequest)
        {
            BeamRequest req = (BeamRequest) imessage;  //TODO move to event system
            MFFS.proxy.registerBeamEffect(worldObj, req.destination.translate(.5), new Vector3D((IPos3D) this).translate(.5), 1.0F, 0.0F, 0.0F, 40);
            MFFS.proxy.animateFortron(worldObj, req.destination, 1.0F, 0.0F, 0.0F, 60);
            return null;
        }
        return super.handleMessage(imessage);
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(MFFS.forcefieldProjector));
        return stack;
    }
}

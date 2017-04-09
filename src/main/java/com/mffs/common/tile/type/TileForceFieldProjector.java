package com.mffs.common.tile.type;

import com.mffs.ModularForcefieldSystem;
import com.mffs.SettingConfiguration;
import com.mffs.api.IProjector;
import com.mffs.api.modules.IModule;
import com.mffs.api.modules.IProjectorMode;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.blocks.BlockForceField;
import com.mffs.common.items.ItemMode;
import com.mffs.common.items.card.ItemCardFrequency;
import com.mffs.common.items.modules.projector.ItemModuleDisintegration;
import com.mffs.common.items.modules.projector.ItemModuleSilence;
import com.mffs.common.items.modules.projector.mode.ItemModeCustom;
import com.mffs.common.items.modules.upgrades.ItemModuleSpeed;
import com.mffs.common.net.packet.BeamRequest;
import com.mffs.common.net.packet.ForcefieldCalculation;
import com.mffs.common.tile.TileFieldMatrix;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Calclavia
 */
public class TileForceFieldProjector extends TileFieldMatrix implements IProjector
{

    /* Set of all forceFields by this entity */
    protected final Set<Vector3D> blocks = new HashSet<>();
    public boolean requireTicks, markFieldUpdate = true;
    /* Flag indicating if this entity has finished */
    private boolean isComplete;

    public TileForceFieldProjector()
    {
        this.capacityBase = 50;
        this.module_index = 1;
    }

    @Override
    public void start()
    {
        super.start();
        calculatedForceField();
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
        if (isActive() && getMode() != null && requestFortron(getFortronCost(), false) >= getFortronCost())
        {
            requestFortron(getFortronCost(), true);
            if (!this.worldObj.isRemote)
            {
                if (this.ticks % 10 == 0 || markFieldUpdate || requireTicks)
                {
                    if (!this.isFinished)
                    {
                        calculatedForceField();
                    }
                    else
                    {
                        projectField();
                    }
                }
            }
            else
            {
                this.animation += getFortronCost() / 10;

                if (this.ticks % 40 == 0 && getModuleCount(ItemModuleSilence.class) <= 0)
                {
                    this.worldObj.playSoundEffect(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, ModularForcefieldSystem.MODID + ":field", 0.6F, 1.0F - this.worldObj.rand.nextFloat() * 0.1F);
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
    public void calculatedForceField()
    {
        if (!isCalc)
        {
            IProjectorMode stack = getMode();
            if (stack != null)
            {
                this.blocks.clear();
            }
        }
        super.calculatedForceField();
        this.isComplete = false;
        this.requireTicks = false;

        Set<ItemStack> modules = getModuleStacks();
        for (ItemStack mod : modules)
        {
            if (((IModule) mod.getItem()).requireTicks(mod))
            {
                requireTicks = true;
                return;
            }
        }
    }

    @Override
    public void onCalculationCompletion()
    {
        //TODO: Send field to client
        //Check if repulsion
        //if(getModuleCount())
        //ModularForcefieldSystem.channel.sendToAll(new ForcefieldCalculation(TileForceFieldProjector.this));
    }

    @Override
    public int getSizeInventory()
    {
        return 32;
    }

    @Override
    public int getProjectionSpeed()
    {
        return 28 + 28 * getModuleCount(ItemModuleSpeed.class, getModuleSlots());
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
            return Math.round(super.calculateFortronCost() + mode.getFortronCost(getAmplifier()));
        }

        return 0;
    }

    @Override
    public float getAmplifier()
    {
        IProjectorMode mode = getMode();
        if (mode instanceof ItemModeCustom)
        {
            //TODO: Custom mode
        }
        return Math.max(Math.min(getCalculatedField().size() / 1000, 10), 1);
    }

    @Override
    public Set<Vector3D> getForceFields()
    {
        return blocks;
    }

    /**
     * @param vec
     * @return
     */
    private boolean canReplace(Vector3D vec)
    {
        Block block = vec.getBlock(this.worldObj);
        if(!(block instanceof BlockForceField))
        {
            final int disintegrationModule = getModuleCount(ItemModuleDisintegration.class); //TODO cache, do not recalc each tick
            if (block != null)
            {
                if (disintegrationModule > 0 && block.getBlockHardness(worldObj, vec.intX(), vec.intY(), vec.intZ()) >= 0)
                {
                    return true;
                }
                return block.isReplaceable(worldObj, vec.intX(), vec.intY(), vec.intZ());
            }
            return disintegrationModule > 0;
        }
        return false;
    }

    public void projectField()
    {
        if (this.isFinished && !this.isCalc && (!this.isComplete || this.markFieldUpdate || this.requireTicks))
        {
            this.markFieldUpdate = false;
            int constructSpeed = Math.min(getProjectionSpeed(), SettingConfiguration.MAX_FORCE_FIELDS_PER_TICK);
            rebuild:
            synchronized (this.calculatedFields)
            {
                Set<Vector3D> fieldToBeProjected = this.calculatedFields;
                for (IModule module : getModules(getModuleSlots()))
                {
                    if (module.onProject(this, fieldToBeProjected))
                    {
                        return;
                    }
                }

                //Get force field blocks
                Vector3D projector = new Vector3D(this);
                fieldToBeProjected = fieldToBeProjected.stream()
                        .filter(x -> !x.equals(projector) && canReplace(x))
                        .filter(w -> getWorldObj().getChunkFromBlockCoords(w.intX(), w.intZ()).isChunkLoaded)
                        .limit(constructSpeed).collect(Collectors.toSet());

                //Place force field blocks
                for (Vector3D vec : fieldToBeProjected)
                {
                    int flag = 0;
                    for (ItemStack stack : getModuleStacks(getModuleSlots()))
                    {
                        if (flag == 0 && stack != null && stack.getItem() instanceof IModule)
                        {
                            flag = ((IModule) stack.getItem()).onProject(this, vec);
                            if (flag != 0)
                            {
                                break;
                            }
                        }
                    }

                    if (flag != 1 && flag != 2)
                    {
                        worldObj.setBlock(vec.intX(), vec.intY(), vec.intZ(), BlockForceField.BLOCK_FORCE_FIELD, 0, 2);
                        this.blocks.add(vec);

                        TileEntity entity = vec.getTileEntity(worldObj);
                        if (entity instanceof TileForceField)
                        {
                            ((TileForceField) entity).setProjector(projector);
                        }
                    }
                }
                this.isComplete = (fieldToBeProjected.size() == 0);
            }
        }
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        destroyField();
    }

    @Override
    public void destroyField()
    {
        if (!this.isCalc && isFinished)
        {
            synchronized (this.calculatedFields)
            {
                for (IModule module : getModules(getModuleSlots()))
                {
                    if (module.onDestroy(this, getCalculatedField()))
                    {
                        break;
                    }
                }
                for (Iterator<Vector3D> it$ = new HashSet<>(this.calculatedFields).iterator(); it$.hasNext(); )
                {
                    Vector3D vec = it$.next();
                    Block block = worldObj.getBlock(vec.intX(), vec.intY(), vec.intZ());
                    if (block instanceof BlockForceField)
                    {
                        worldObj.setBlockToAir(vec.intX(), vec.intY(), vec.intZ());
                    }
                }
            }
        }
        this.blocks.clear();
        this.calculatedFields.clear();
        this.isComplete = false;
        this.isFinished = false;
        this.requireTicks = false;
    }

    /**
     * Gets the Filtered stacks based on Items.
     *
     * @return
     */
    public List<Item> getFilterItems()
    {
        List<Item> stacks = new ArrayList<>();
        for (int slot = 26; slot < 32; slot++)
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
    public List<ItemStack> getFilterStacks()
    {
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 26; slot < 32; slot++)
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

        if (slot < 26)
        {
            return stack.getItem() instanceof IModule;
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
        if (imessage instanceof ForcefieldCalculation)
        {
            ForcefieldCalculation calc = (ForcefieldCalculation) imessage;
            getCalculatedField().clear();
            getCalculatedField().addAll(calc.getBlocks());
            this.isCalc = true;
            return null; //we are done!
        }
        else if (imessage instanceof BeamRequest)
        {
            BeamRequest req = (BeamRequest) imessage;
            ModularForcefieldSystem.proxy.registerBeamEffect(worldObj, req.destination.translate(.5), new Vector3D(this).translate(.5), 1.0F, 0.0F, 0.0F, 40);
            ModularForcefieldSystem.proxy.animateFortron(worldObj, req.destination, 1.0F, 0.0F, 0.0F, 60);
            return null;
        }
        return super.handleMessage(imessage);
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(ModularForcefieldSystem.forcefieldProjector));
        return stack;
    }
}

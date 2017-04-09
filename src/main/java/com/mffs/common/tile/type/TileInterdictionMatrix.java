package com.mffs.common.tile.type;

import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.mffs.ModularForcefieldSystem;
import com.mffs.SettingConfiguration;
import com.mffs.api.card.ICardInfinite;
import com.mffs.api.modules.IInterdictionModule;
import com.mffs.api.modules.IModule;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.common.items.card.ItemCardFrequency;
import com.mffs.common.items.modules.interdiction.ItemModuleWarn;
import com.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.TileModuleAcceptor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public final class TileInterdictionMatrix extends TileModuleAcceptor implements IInterdictionMatrix
{

    /* Deteremines if this machine is in 'ban' mode */
    private boolean banMode;

    /**
     * Constructor. DUH
     */
    public TileInterdictionMatrix()
    {
        this.capacityBase = 30;
        this.module_index = 2;
        this.module_end = 9;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (this.isActive() && this.ticks % 20L == 0)
            { //Increase this to 1 a second.
                if (requestFortron(getFortronCost() * 10, false) > 0)
                {
                    requestFortron(getFortronCost() * 10, true);
                    scan();
                }
                else
                {
                    setActive(false);
                }
            }
        }
    }

    /* Single instance of interdiction warning */
    private static ChatComponentText warning = new ChatComponentText("[InterdictionMatrix] " + LanguageUtility.getLocal("message.interdictionMatrix.warn"));

    /**
     * Scans the action range of this entity and performs appropriate action.
     */
    private void scan()
    {
        IBiometricIdentifier bio = getBiometricIdentifier();
        AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);

        int wRange = getWarningRange();
        int aRange = getActionRange();
        int range = Math.max(aRange, wRange);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis.expand(range, range, range));
        Set<ItemStack> modules = getModuleStacks(); //obtain modules here to save reIteration.

        for (EntityLivingBase entity : entities)
        {
            double dist = entity.getDistance(this.xCoord, this.yCoord, this.zCoord);

            if (dist <= aRange)
            { //Action Range
                if (entity instanceof EntityPlayer)
                {
                    EntityPlayer pl = (EntityPlayer) entity;
                    if (bio != null && bio.isAccessGranted(pl.getGameProfile().getName(), Permission.BYPASS_DEFENSE)
                            || !SettingConfiguration.INTERACT_CREATIVE && pl.capabilities.isCreativeMode)
                    {
                        continue;
                    }
                }
                for (ItemStack stack : modules)
                {
                    if (stack != null && stack.getItem() instanceof IInterdictionModule)
                    {
                        IInterdictionModule mod = (IInterdictionModule) stack.getItem();
                        if (mod.onDefend(this, entity) || entity.isDead)
                        {
                            break;
                        }
                    }
                }
                continue; //we do not need to warn them!
            }

            if (dist <= wRange && entity instanceof EntityPlayer)
            {//Warning Range!
                EntityPlayer pl = (EntityPlayer) entity;
                if (bio != null && bio.isAccessGranted(pl.getGameProfile().getName(), Permission.BYPASS_DEFENSE))
                {
                    continue;
                }
                pl.addChatMessage(warning);
            }
        }
    }

    @Override
    public int getWarningRange()
    {
        return Math.min(getModuleCount(ItemModuleWarn.class) + getActionRange(), SettingConfiguration.INTERDICTION_MAX_RANGE) + 3;
    }

    @Override
    public int getActionRange()
    {
        return Math.min(getModuleCount(ItemModuleScale.class), SettingConfiguration.INTERDICTION_MAX_RANGE);
    }

    /**
     * Handles the message given by the handler.
     *
     * @param imessage The message.
     */
    public IMessage handleMessage(IMessage imessage)
    {
        if (imessage instanceof EntityToggle)
        {
            EntityToggle tog = (EntityToggle) imessage;
            if (tog.toggle_opcode == EntityToggle.FILTER_TOGGLE)
            {
                this.banMode = !banMode;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                return null;
            }
        }
        return super.handleMessage(imessage);
    }

    @Override
    public Set<ItemStack> getFilteredItems()
    {
        Set<ItemStack> stacks = new HashSet();
        for (int i = module_end; i < getSizeInventory() - 1; i++)
        {
            if (getStackInSlot(i) != null)
            {
                stacks.add(getStackInSlot(i));
            }
        }
        return stacks;
    }

    @Override
    public boolean getFilterMode()
    {
        return this.banMode;
    }

    @Override
    public float getAmplifier()
    {
        return Math.max(Math.min(getActionRange() / 20, 10), 1);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return 19;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("ban", banMode);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.banMode = nbt.getBoolean("ban");
    }

    @Override
    public Set<ItemStack> getCards()
    {
        Set<ItemStack> cards = new HashSet();
        cards.add(super.getCard());
        cards.add(getStackInSlot(1));
        return cards;
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     *
     * @param slot
     * @param item
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item)
    {
        if (slot == 0)
        {
            return item.getItem() instanceof ICardInfinite;
        }
        else if (slot == 1)
        {
            return item.getItem() instanceof ItemCardFrequency;
        }

        if (slot > this.module_end)
        {
            return true;
        }
        return item.getItem() instanceof IModule;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(ModularForcefieldSystem.interdictionMatrix));
        return stack;
    }
}

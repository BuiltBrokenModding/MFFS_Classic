package com.builtbroken.mffs.content.interdiction;

import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mffs.ModularForceFieldSystem;
import com.builtbroken.mffs.MFFSSettings;
import com.builtbroken.mffs.api.card.ICardInfinite;
import com.builtbroken.mffs.api.modules.IInterdictionModule;
import com.builtbroken.mffs.api.modules.IFieldModule;
import com.builtbroken.mffs.api.security.IBiometricIdentifier;
import com.builtbroken.mffs.api.security.IInterdictionMatrix;
import com.builtbroken.mffs.api.security.Permission;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.items.card.ItemCardFrequency;
import com.builtbroken.mffs.common.items.modules.interdiction.ItemModuleWarn;
import com.builtbroken.mffs.common.items.modules.upgrades.ItemModuleScale;
import com.builtbroken.mffs.common.net.packet.EntityToggle;
import com.builtbroken.mffs.prefab.ModuleInventory;
import com.builtbroken.mffs.prefab.tile.TileModuleAcceptor;
import com.builtbroken.mffs.content.projector.TileForceFieldProjector;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Calclavia
 */
public final class TileInterdictionMatrix extends TileModuleAcceptor implements IInterdictionMatrix
{
    private static final int TICK_RATE = 20; //TODO make configurable TODO scale module effects by speed to keep constant
    /* Single instance of interdiction warning */
    private static ChatComponentText warning = new ChatComponentText("[InterdictionMatrix] " + LanguageUtility.getLocal("message.interdictionMatrix.warn"));

    /* Deteremines if this machine is in 'ban' mode */
    private boolean banMode;

    private TileForceFieldProjector projector;

    public TileInterdictionMatrix()
    {
        this.fortronCapacity = 30;
        this.moduleInventory = new ModuleInventory(this, 2, 9);
    }

    @Override
    public void updateEntity()
    {
        //TODO repogram to have a visual effect so this can not be used as a trap
        //TODO increase power cost when not attached to forcefield
        //TODO decrease damage effects when not attached to forcefield
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            //Increase this to 1 a second.
            if (this.isActive() && this.ticks % TICK_RATE == 0) //TODO randomize start to reduce several fields running on same tick
            {
                //Find projector
                if (projector == null)
                {
                    int count = 0;
                    Pos pos = new Pos(xCoord, yCoord, zCoord);
                    for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
                    {
                        TileEntity tile = pos.add(direction).getTileEntity(worldObj);
                        if (tile instanceof TileForceFieldProjector)
                        {
                            count++;
                            projector = (TileForceFieldProjector) tile;
                        }
                    }

                    if (count > 1)
                    {
                        projector = null;
                        //TODO error saying that there can only be 1 projector per matrix
                    }
                }

                //TODO move energy cost to variable with config
                if (requestFortron(getFortronCost() * 10, false) > 0)
                {
                    requestFortron(getFortronCost() * 10, true);
                    scan();
                }
            }
        }
    }

    @Override
    public boolean isActive()
    {
        return super.isActive() && ticks > 12  && (projector == null || projector.isActive());
    }

    /**
     * Scans the action range of this entity and performs appropriate action.
     */
    private void scan()
    {
        //Get biometrix identifier
        IBiometricIdentifier bio = getBiometricIdentifier();

        //Get ranges
        Cube warningRange = getWarningRange();
        Cube actionRange = getActionRange();

        //Get modules
        Set<ItemStack> modules = getModuleStacks(); //obtain modules here to save reIteration.
        boolean hasWarnModule = false;
        for (ItemStack stack : modules)
        {
            if (stack != null && stack.getItem() instanceof ItemModuleWarn)
            {
                hasWarnModule = true;
                break;
            }
        }

        if (modules != null && modules.size() > 0)
        {
            //Get entities in box
            List<Entity> entities = warningRange.getEntities(worldObj);

            for (Entity entity : entities)
            {
                boolean inField = (projector == null || projector.getInteriorPoints().contains(new Vector3D(entity).floor()));
                //Action Range
                if (actionRange.isWithin(entity.posX, entity.posY, entity.posZ) && inField) //TODO unit test position
                {
                    //Checking if player has permissions
                    if (bio != null && entity instanceof EntityPlayer)
                    {
                        EntityPlayer player = (EntityPlayer) entity;
                        if (bio.isAccessGranted(player.getGameProfile().getName(), Permission.BYPASS_DEFENSE) || player.capabilities.isCreativeMode)
                        {
                            continue;
                        }
                    }

                    //Run modular actions
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
                }
                //Warning Range!
                //TODO add config to force enable
                //TODO add system to detect enter and leave to reduce chat spam
                //TODO add timer to reduce chat spam
                //TODO add config to switch chat to audio
                //TODO add audio warning
                //TODO only chat spam if harmful to user
                else if (entity instanceof EntityPlayer && hasWarnModule)
                {
                    EntityPlayer pl = (EntityPlayer) entity;
                    if (bio != null && bio.isAccessGranted(pl.getGameProfile().getName(), Permission.BYPASS_DEFENSE))
                    {
                        continue;
                    }
                    pl.addChatMessage(warning);
                }
            }
        }
    }

    @Override
    public Cube getWarningRange()
    {
        int range = Math.min(getModuleCount(ItemModuleWarn.class), MFFSSettings.INTERDICTION_MAX_RANGE) + 3;
        return getActionRange().expand(range);
    }

    @Override
    public Cube getActionRange()
    {
        if (projector != null)
        {
            Vector3D negScale = projector.getNegativeScale();
            Vector3D posScale = projector.getPositiveScale();
            Vector3D translation = projector.getTranslation();
            Vector3D pos = new Vector3D(projector.xCoord + 0.5, projector.yCoord + 0.5, projector.zCoord + 0.5);

            negScale = negScale.scale(-1).add(translation).add(pos);
            posScale = posScale.add(translation).add(pos);

            Pos start = new Pos(negScale.x, negScale.y, negScale.z);
            Pos end = new Pos(posScale.x, posScale.y, posScale.z);

            return new Cube(start, end);
        }

        Pos center = new Pos(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
        int range = Math.min(getModuleCount(ItemModuleScale.class), MFFSSettings.INTERDICTION_MAX_RANGE);
        return new Cube(center.sub(range), center.add(range));
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
        for (int i = moduleInventory.end; i < getSizeInventory() - 1; i++)
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
        return 1;
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

        if (slot > moduleInventory.end)
        {
            return true;
        }
        return item.getItem() instanceof IFieldModule;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entityPlayer)
    {
        List<ItemStack> stack = super.getRemovedItems(entityPlayer);
        stack.add(new ItemStack(ModularForceFieldSystem.interdictionMatrix));
        return stack;
    }
}

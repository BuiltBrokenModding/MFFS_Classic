package com.builtbroken.mffs.api.utils;

import com.builtbroken.mffs.ModularForcefieldSystem;
import com.builtbroken.mffs.api.fortron.IFortronFrequency;
import com.builtbroken.mffs.api.modules.IModuleContainer;
import com.builtbroken.mffs.api.vector.Vector3D;
import com.builtbroken.mffs.common.TransferMode;
import com.builtbroken.mffs.common.items.modules.projector.ItemModuleCamouflage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Calclavia
 */
@Deprecated //Either will be moved or phased out
public class FortronHelper
{

    /**
     * Transfers fortron from 1 FortronFreqency, to multiple others.
     *
     * @param freq  The base frequency that is being looked for.
     * @param tiles The other tiles to send frequencies to.
     * @param mode  The current mode to send.
     * @param limit The tick limit.
     */
    public static void transfer(IFortronFrequency freq, Set<IFortronFrequency> tiles, TransferMode mode, int limit)
    {
        int fortron = freq.getFortronEnergy(), capacity = freq.getFortronCapacity();
        for (Iterator<IFortronFrequency> it$ = tiles.iterator(); it$.hasNext(); )
        {
            IFortronFrequency machine = it$.next();
            if (machine == null)
            {//this should prevent nulls.
                it$.remove();
                continue;
            }

            fortron += machine.getFortronEnergy();
            capacity += machine.getFortronCapacity();
        }

        if (fortron <= 0 || capacity <= 0)
        {
            return;
        }

        switch (mode)
        {

            case DISTRIBUTE:
                for (IFortronFrequency machine : tiles)
                {
                    transfer(freq, machine, (fortron / tiles.size()) - machine.getFortronEnergy(), limit);
                }
                return;

            case EQUALIZE: //TODO: This should be different than drain!?
                //tiles.add(freq);//we wanna equally send to ourselves.
            case DRAIN://Seems like the same as EQUALIZE
                //tiles.remove(freq);
                for (IFortronFrequency machine : tiles)
                {
                    int transfer = (int) (((double) machine.getFortronCapacity() / capacity) * fortron) - machine.getFortronEnergy();
                    if (mode == TransferMode.DRAIN && transfer <= 0)
                    {
                        continue;
                    }
                    transfer(freq, machine, transfer, limit);
                }
                break;

            case FILL:
                if (freq.getFortronEnergy() >= freq.getFortronCapacity())
                {
                    return;
                }
                int transfer = freq.getFortronCapacity() - freq.getFortronEnergy();
                for (IFortronFrequency machine : tiles)
                {
                    int consume = Math.min(transfer, machine.getFortronEnergy());
                    transfer = -machine.getFortronEnergy() - consume;
                    if (consume > 0)
                    {
                        transfer(freq, machine, transfer - machine.getFortronEnergy(), limit);
                    }
                }
                break;
        }
    }

    /**
     * Transfers fortron directly from 1 machine, to the receiving.
     *
     * @param freq  The sending machine.
     * @param rec   The receiving machine.
     * @param joul  The jouls to be sent.
     * @param limit The limit per tick.
     */
    public static void transfer(IFortronFrequency freq, IFortronFrequency rec, int joul, int limit)
    {
        TileEntity entity = (TileEntity) freq;
        World world = entity.getWorldObj();

        if (joul < 0)
        { //we switch the frequencies! Means they have less than the receiver
            IFortronFrequency dummy = freq;
            freq = rec;
            rec = dummy;
        }

        joul = Math.min(joul < 0 ? Math.abs(joul) : joul, limit);
        int toBeInject = rec.provideFortron(freq.requestFortron(joul, false), false);
        toBeInject = freq.requestFortron(rec.provideFortron(toBeInject, true), true);


        boolean camo = (freq instanceof IModuleContainer && ((IModuleContainer) freq).getModuleCount(ItemModuleCamouflage.class) > 0);

        if (world.isRemote && toBeInject > 0 && !camo) //TODO should be handled by the device
        {
            ModularForcefieldSystem.proxy.registerBeamEffect(world, new Vector3D((TileEntity) freq).translate(.5),
                    new Vector3D((TileEntity) rec).translate(.5), 0.6F, 0.6F, 1, 20); //TODO move to effect system
        }
    }
}

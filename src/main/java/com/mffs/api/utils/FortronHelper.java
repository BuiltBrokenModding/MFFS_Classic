package com.mffs.api.utils;

import com.mffs.api.TransferMode;
import com.mffs.api.fortron.IFortronFrequency;
import com.mffs.api.modules.IModuleAcceptor;
import com.mffs.api.vector.Vector3D;
import com.mffs.client.render.particles.FortronBeam;
import com.mffs.common.items.modules.upgrades.ModuleCamouflage;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Calclavia
 */
public class FortronHelper {

    /**
     * Transfers fortron from 1 FortronFreqency, to multiple others.
     *
     * @param freq  The base frequency that is being looked for.
     * @param tiles The other tiles to send frequencies to.
     * @param mode  The current mode to send.
     * @param limit The tick limit.
     */
    public static void transfer(IFortronFrequency freq, Set<IFortronFrequency> tiles, TransferMode mode, int limit) {
        int fortron = 0, capacity = 0;
        for (Iterator<IFortronFrequency> it$ = tiles.iterator(); it$.hasNext(); ) {
            IFortronFrequency machine = it$.next();
            if (machine == null) {//this should prevent nulls.
                it$.remove();
                continue;
            }

            fortron += machine.getFortronEnergy();
            capacity += machine.getFortronCapacity();
        }

        if (fortron <= 0 || capacity <= 0)
            return;

        switch (mode) {

            case DISTRIBUTE:
                for (IFortronFrequency machine : tiles)
                    transfer(freq, machine, (fortron / tiles.size()) - machine.getFortronEnergy(), limit);
                return;

            case DRAIN://Seems like the same as EQUALIZE
                tiles.remove(freq);
            case EQUALIZE:
                //tiles.remove(freq);
                for (IFortronFrequency machine : tiles) {
                    int transfer = (int) (((double) machine.getFortronCapacity() / capacity) * fortron) - machine.getFortronEnergy();
                    if (mode == TransferMode.DRAIN && transfer <= 0)
                        continue;
                    transfer(freq, machine, transfer, limit);
                }
                break;

            case FILL:
                if (freq.getFortronEnergy() >= freq.getFortronCapacity())
                    return;
                tiles.remove(freq);
                int transfer = freq.getFortronCapacity() - freq.getFortronEnergy();
                for (IFortronFrequency machine : tiles) {
                    int consume = Math.min(transfer, machine.getFortronEnergy());
                    transfer = -machine.getFortronEnergy() - consume;
                    if (consume > 0)
                        transfer(freq, machine, transfer - machine.getFortronEnergy(), limit);
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
    public static void transfer(IFortronFrequency freq, IFortronFrequency rec, int joul, int limit) {
        TileEntity entity = (TileEntity) freq;
        World world = entity.getWorldObj();
        boolean camo = (freq instanceof IModuleAcceptor && ((IModuleAcceptor) freq).getModuleCount(ModuleCamouflage.class) > 0);

        if (joul < 0) { //we switch the frequencies! Means they have less than the receiver
            IFortronFrequency dummy = freq;
            freq = rec;
            rec = dummy;
        }

        joul = Math.min(joul < 0 ? Math.abs(joul) : joul, limit);
        int toBeInject = rec.provideFortron(freq.requestFortron(joul, false), false);
        toBeInject = freq.requestFortron(rec.provideFortron(toBeInject, true), true);

        if (world.isRemote && toBeInject > 0 && !camo) {
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FortronBeam(world, new Vector3D((TileEntity) freq).translate(.5), new Vector3D((TileEntity) rec).translate(.5), 0.6F, 0.6F, 1, 20));
        }
    }
}

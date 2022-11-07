package dev.su5ed.mffs.api.fortron;

import java.util.Set;

/**
 * Applied to the Fortron Capacitor TileEntity. Extends IFortronFrequency
 *
 * @author Calclavia
 */
public interface FortronCapacitor {
    public Set<FortronFrequency> getLinkedDevices();

    public int getTransmissionRange();

    public int getTransmissionRate();
}

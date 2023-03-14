package dev.su5ed.mffs.api.fortron;

import java.util.Collection;

/**
 * Applied to the Fortron Capacitor TileEntity.
 *
 * @author Calclavia
 */
public interface FortronCapacitor {
    Collection<FortronStorage> getDevicesByFrequency();

    int getTransmissionRange();

    int getTransmissionRate();
}

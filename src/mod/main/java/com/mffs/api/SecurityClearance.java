package com.mffs.api;

/**
 * Created by pwaln on 5/30/2016.
 */
public enum SecurityClearance {
    FFB("Forcefield Bypass", 0),
    EB("Edit MFFS Block", 1),
    CSR("Config Security Rights", 2),
    SR("Stay in Area", 3),
    OSS("Open Secure Storage", 4),
    RPB("Change Protected Block", 5),
    AAI("Allow have all Items", 6),
    UCS("Use Control System", 7);

    private String name;
    byte index;
    SecurityClearance(String name, int index) {
        this.name = name;
        this.index = (byte) index;
    }

    public String getName() { return name;}
}

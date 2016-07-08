package com.mffs.api.utils;


import cpw.mods.fml.common.Loader;

/**
 * @author Calclavia
 */
public enum CompatibilityType {
    THERMAL_EXPANSION("ThermalExpansion", "ThermalExpansion", "Redstone Flux", "RF", 5628), INDUSTRIALCRAFT("IC2", "IndustrialCraft", "Electrical Unit", "EU", 22512), BUILDCRAFT("BuildCraft|Energy", "BuildCraft", "Minecraft Joule", "MJ", 56280);

    public final String modID;
    public final String moduleName;
    public final String fullUnit;
    public final String unit;
    public double ratio;
    public double reciprocal_ratio;

    CompatibilityType(String modID, String moduleName, String fullUnit, String unit, int ratio) {
        this.modID = modID;
        this.moduleName = moduleName;
        this.fullUnit = fullUnit;
        this.unit = unit;
        this.ratio = (1.0D / ratio);
        this.reciprocal_ratio = ratio;
    }

    public static CompatibilityType get(String moduleName) {
        for (CompatibilityType type : CompatibilityType.values()) {
            if (moduleName.equals(type.moduleName)) {
                return type;
            }
        }
        return null;
    }

    public boolean isLoaded() {
        return Loader.isModLoaded(this.modID);
    }
}

package com.mffs.api.utils;

/**
 * @author Calclavia
 */
public class UnitDisplay {
    public static String getDisplay(double value, Unit unit, int decimalPlaces, boolean isShort) {
        return getDisplay(value, unit, decimalPlaces, isShort, 1.0D);
    }

    public static String getDisplay(double value, Unit unit, int decimalPlaces, boolean isShort, double multiplier) {
        String unitName = unit.name;
        String prefix = "";
        if (value < 0.0D) {
            value = Math.abs(value);
            prefix = "-";
        }
        value *= multiplier;
        if (isShort) {
            unitName = unit.symbol;
        } else if (value > 1.0D) {
            unitName = unit.getPlural();
        }
        if (value == 0.0D) {
            return value + " " + unitName;
        }
        for (int i = 0; i < UnitPrefix.values().length; i++) {
            UnitPrefix lowerMeasure = UnitPrefix.values()[i];
            if ((lowerMeasure.isBellow(value)) && (lowerMeasure.ordinal() == 0)) {
                return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.getName(isShort) + unitName;
            }
            if (lowerMeasure.ordinal() + 1 >= UnitPrefix.values().length) {
                return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.getName(isShort) + unitName;
            }
            UnitPrefix upperMeasure = UnitPrefix.values()[(i + 1)];
            if (((lowerMeasure.isAbove(value)) && (upperMeasure.isBellow(value))) || (lowerMeasure.value == value)) {
                return prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.getName(isShort) + unitName;
            }
        }
        return prefix + roundDecimals(value, decimalPlaces) + " " + unitName;
    }

    public static String getDisplay(double value, Unit unit) {
        return getDisplay(value, unit, 2, false);
    }

    public static String getDisplay(double value, Unit unit, UnitPrefix prefix) {
        return getDisplay(value, unit, 2, false, prefix.value);
    }

    public static String getDisplayShort(double value, Unit unit) {
        return getDisplay(value, unit, 2, true);
    }

    public static String getDisplayShort(double value, Unit unit, UnitPrefix prefix) {
        return getDisplay(value, unit, 2, true, prefix.value);
    }

    public static String getDisplayShort(double value, Unit unit, int decimalPlaces) {
        return getDisplay(value, unit, decimalPlaces, true);
    }

    public static String getDisplaySimple(double value, Unit unit, int decimalPlaces) {
        if (value > 1.0D) {
            if (decimalPlaces < 1) {
                return (int) value + " " + unit.getPlural();
            }
            return roundDecimals(value, decimalPlaces) + " " + unit.getPlural();
        }
        if (decimalPlaces < 1) {
            return (int) value + " " + unit.name;
        }
        return roundDecimals(value, decimalPlaces) + " " + unit.name;
    }

    public static double roundDecimals(double d, int decimalPlaces) {
        int j = (int) (d * Math.pow(10.0D, decimalPlaces));
        return j / Math.pow(10.0D, decimalPlaces);
    }

    public static double roundDecimals(double d) {
        return roundDecimals(d, 2);
    }

    public static enum Unit {
        AMPERE("Amp", "I"), AMP_HOUR("Amp Hour", "Ah"), VOLTAGE("Volt", "V"), WATT("Watt", "W"), WATT_HOUR("Watt Hour", "Wh"), RESISTANCE("Ohm", "R"), CONDUCTANCE("Siemen", "S"), JOULES("Joule", "J"), LITER("Liter", "L"), NEWTON_METER("Newton Meter", "Nm"), REDFLUX("Redstone-Flux", "Rf"), MINECRAFT_JOULES("Minecraft-Joules", "Mj"), ELECTRICAL_UNITS("Electrical-Units", "Eu");

        public String name;
        public String symbol;

        private Unit(String name, String symbol) {
            this.name = name;
            this.symbol = symbol;
        }

        public String getPlural() {
            return this.name + "s";
        }
    }

    public static enum UnitPrefix {
        MICRO("Micro", "u", 1.0E-6D), MILLI("Milli", "m", 0.001D), BASE("", "", 1.0D), KILO("Kilo", "k", 1000.0D), MEGA("Mega", "M", 1000000.0D), GIGA("Giga", "G", 1.0E9D), TERA("Tera", "T", 1.0E12D), PETA("Peta", "P", 1.0E15D), EXA("Exa", "E", 1.0E18D), ZETTA("Zetta", "Z", 1.0E21D), YOTTA("Yotta", "Y", 1.0E24D);

        public String name;
        public String symbol;
        public double value;

        private UnitPrefix(String name, String symbol, double value) {
            this.name = name;
            this.symbol = symbol;
            this.value = value;
        }

        public String getName(boolean getShort) {
            if (getShort) {
                return this.symbol;
            }
            return this.name;
        }

        public double process(double value) {
            return value / this.value;
        }

        public boolean isAbove(double value) {
            return value > this.value;
        }

        public boolean isBellow(double value) {
            return value < this.value;
        }
    }
}
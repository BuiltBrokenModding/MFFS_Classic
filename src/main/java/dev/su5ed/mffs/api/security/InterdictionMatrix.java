package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collection;
import java.util.Locale;

public interface InterdictionMatrix extends Activatable, BiometricIdentifierLink, ModuleAcceptor {
    TileEntity be();

    /** The range in which the Interdiction Matrix starts warning the player. */
    int getWarningRange();

    /** The range in which the Interdiction Matrix has an effect on. */
    int getActionRange();

    /**
     * Merges an item into the Interdiction Matrix's safe keeping inventory.
     *
     * @param stack the item to merge
     * @return True if kept, false if dropped.
     */
    boolean mergeIntoInventory(ItemStack stack);

    Collection<ItemStack> getFilteredItems();

    /** @return The confiscation mode (BLACKLIST or WHITELIST). */
    ConfiscationMode getConfiscationMode();

    ITextComponent getTitle();

    enum ConfiscationMode {
        BLACKLIST(0x308F0000),
        WHITELIST(0x30008F00);

        public final int slotTintColor;
        public final ITextComponent translation;

        ConfiscationMode(int slotTintColor) {
            this.slotTintColor = slotTintColor;
            this.translation = new TextComponentTranslation(MFFSMod.MODID + ".confiscation_mode." + name().toLowerCase(Locale.ROOT));
        }

        public ConfiscationMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}

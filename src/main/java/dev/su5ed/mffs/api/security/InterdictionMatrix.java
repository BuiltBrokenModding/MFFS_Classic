package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.Activatable;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.Locale;

public interface InterdictionMatrix extends Activatable, BiometricIdentifierLink, ModuleAcceptor {
    BlockEntity be();

    /**
     * The range in which the Interdiction Matrix starts warning the player.
     */
    int getWarningRange();

    /**
     * The range in which the Interdiction Matrix has an effect on.
     */
    int getActionRange();

    /**
     * Merges an item into the Interdiction Matrix's safe keeping inventory.
     *
     * @param stack the item to merge
     * @return True if kept, false if dropped.
     */
    boolean mergeIntoInventory(ItemStack stack);

    Collection<ItemStack> getFilteredItems();

    /**
     * @return True if the filtering is on ban mode. False if it is on allow-only mode.
     */
    ConfiscationMode getConfiscationMode();
    
    Component getTitle();

    enum ConfiscationMode {
        BLACKLIST(0x308F0000),
        WHITELIST(0x30008F00);

        public final int slotTintColor;
        public final MutableComponent translation;

        ConfiscationMode(int slotTintColor) {
            this.slotTintColor = slotTintColor;
            this.translation = Component.translatable(MFFSMod.MODID + ".confiscation_mode." + name().toLowerCase(Locale.ROOT));
        }

        public ConfiscationMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}

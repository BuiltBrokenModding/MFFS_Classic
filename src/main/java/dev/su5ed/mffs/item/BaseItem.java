package dev.su5ed.mffs.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseItem extends Item {

    /** If true, hovering with Shift shows the item's description translation. */
    protected final boolean showDescription;

    public BaseItem() {
        this(false);
    }

    public BaseItem(boolean showDescription) {
        this.showDescription = showDescription;
    }

    /**
     * Override to inject tooltip lines before the description line.
     */
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     net.minecraft.client.util.ITooltipFlag flagIn) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addInformationPre(stack, worldIn, tooltip, flagIn);
        if (this.showDescription) {
            if (GuiScreen.isShiftKeyDown()) {
                // Shift held: show full description
                String descKey = "item.mffs." + this.getRegistryName().getPath() + ".description";
                tooltip.add(TextFormatting.GRAY + I18n.format(descKey));
            } else {
                // Hint to hold Shift
                tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.show_details",
                    TextFormatting.GRAY + I18n.format("info.mffs.key.shift")));
            }
        }
    }
}

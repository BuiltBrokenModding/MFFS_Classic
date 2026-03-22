package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InterdictionMatrixModuleItem extends ModuleItem<InterdictionMatrixModule> {

    public InterdictionMatrixModuleItem(ModuleType<InterdictionMatrixModule> module) {
        super(module);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     net.minecraft.client.util.ITooltipFlag flagIn) {
        // Show which machine this module belongs to
        if (ModBlocks.INTERDICTION_MATRIX != null) {
            tooltip.add(TextFormatting.DARK_RED + ModBlocks.INTERDICTION_MATRIX.getLocalizedName());
        }
        super.addInformationPre(stack, worldIn, tooltip, flagIn);
    }
}

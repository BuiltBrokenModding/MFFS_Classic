package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class InterdictionMatrixModuleItem extends ModuleItem<InterdictionMatrixModule> {

    public InterdictionMatrixModuleItem(ExtendedItemProperties properties, ModuleType<InterdictionMatrixModule> module) {
        super(properties, module);
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(ModBlocks.INTERDICTION_MATRIX.get().getName().withStyle(ChatFormatting.DARK_RED));

        super.appendHoverTextPre(stack, context, tooltipComponents, isAdvanced);
    }
}

package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class InterdictionMatrixModuleItem extends ModuleItem<InterdictionMatrixModule> {

    public InterdictionMatrixModuleItem(ExtendedItemProperties properties, ModuleType<InterdictionMatrixModule> module) {
        super(properties, module);
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(ModBlocks.INTERDICTION_MATRIX.get().getName().withStyle(ChatFormatting.DARK_RED));

        super.appendHoverTextPre(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}

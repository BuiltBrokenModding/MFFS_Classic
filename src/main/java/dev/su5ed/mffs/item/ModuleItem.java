package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleType;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.text.DecimalFormat;
import java.util.function.Consumer;

public class ModuleItem<T extends Module> extends BaseItem {
    private static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");

    protected final ModuleType<T> module;

    public ModuleItem(ExtendedItemProperties properties, ModuleType<T> module) {
        super(properties);
        this.module = module;
    }

    public ModuleType<T> getModule() {
        return this.module;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        tooltipAdder.accept(ModUtil.translate("info", "fortron_usage", Component.literal(FORTRON_COST_FORMAT.format(this.module.getFortronCost(1) * 20)).withStyle(ChatFormatting.GRAY))
            .withStyle(ChatFormatting.DARK_GRAY));
    }
}

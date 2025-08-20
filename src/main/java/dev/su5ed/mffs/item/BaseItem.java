package dev.su5ed.mffs.item;

import com.google.common.base.Suppliers;
import dev.su5ed.mffs.setup.ModClientSetup;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseItem extends Item {
    private final Supplier<Component> description;

    public BaseItem(ExtendedItemProperties properties) {
        super(properties.properties);

        this.description = properties.description != null ? Suppliers.memoize(() -> properties.description.apply(this)) : null;
    }

    protected void appendHoverTextPre(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        appendHoverTextPre(stack, context, tooltipDisplay, tooltipAdder, flag);
        if (this.description != null) {
            if (ModClientSetup.hasShiftDown()) {
                tooltipAdder.accept(this.description.get());
            } else {
                tooltipAdder.accept(ModUtil.translate("info", "show_details", ModUtil.translate("info", "key.shift").withStyle(ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    public static class ExtendedItemProperties {
        private final Properties properties;

        private Function<Item, Component> description;

        public ExtendedItemProperties(Properties properties) {
            this.properties = properties;
        }

        public Properties getProperties() {
            return this.properties;
        }

        public ExtendedItemProperties description() {
            this.description = item -> {
                String name = BuiltInRegistries.ITEM.getKey(item).getPath();
                return ModUtil.translate("item", name + ".description").withStyle(ChatFormatting.GRAY);
            };
            return this;
        }
    }
}

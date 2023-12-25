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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseItem extends Item {
    private final Supplier<Component> description;

    public BaseItem(ExtendedItemProperties properties) {
        super(properties.properties);

        this.description = properties.description != null ? Suppliers.memoize(() -> properties.description.apply(this)) : null;
    }
    
    protected void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);
        if (this.description != null) {
            if (ModClientSetup.hasShiftDown()) {
                tooltipComponents.add(this.description.get());
            }
            else {
                tooltipComponents.add(ModUtil.translate("info", "show_details", ModUtil.translate("info", "key.shift").withStyle(ChatFormatting.GRAY))
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

        public ExtendedItemProperties description() {
            this.description = item -> {
                String name = BuiltInRegistries.ITEM.getKey(item).getPath();
                return ModUtil.translate("item", name + ".description").withStyle(ChatFormatting.GRAY);
            };
            return this;
        }
    }
}

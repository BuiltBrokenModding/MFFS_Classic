package dev.su5ed.mffs.item;

import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class BaseItem extends Item {
    private final Lazy<Component> description;

    public BaseItem(ExtendedItemProperties properties) {
        super(properties.properties);

        this.description = properties.description != null ? Lazy.of(() -> properties.description.apply(this)) : null;
    }
    
    protected void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);
        if (this.description != null) {
            tooltipComponents.add(this.description.get());
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
                String name = ForgeRegistries.ITEMS.getKey(item).getPath();
                return ModUtil.translate("item", name + ".description").withStyle(ChatFormatting.GRAY);
            };
            return this;
        }
    }
}

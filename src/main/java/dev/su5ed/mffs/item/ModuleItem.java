package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class ModuleItem extends Item implements Module {
    private static final DecimalFormat FORTRON_COST_FORMAT = new DecimalFormat("#.##");
    
    private final float fortronCost;

    private Lazy<Component> description;

    public ModuleItem(Properties properties) {
        this(properties, 0.5F);
    }

    public ModuleItem(Properties properties, float fortronCost) {
        super(properties);

        this.fortronCost = fortronCost;
    }
    
    public ModuleItem withDescription() {
        this.description = Lazy.of(() -> {
            String name = ForgeRegistries.ITEMS.getKey(this).getPath();
            return ModUtil.translate("item", name + ".description").withStyle(ChatFormatting.GRAY);
        });
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        
        tooltipComponents.add(Component.literal("Fortron: " + FORTRON_COST_FORMAT.format(getFortronCost(1) * 20) + " L/s").withStyle(ChatFormatting.GRAY));
        if (this.description != null) {
            tooltipComponents.add(this.description.get());
        }
    }

    @Override
    public float getFortronCost(float amplifier) {
        return this.fortronCost * amplifier;
    }

    @Override
    public boolean beforeProject(Projector projector, Set<BlockPos> field) {
        return false;
    }

    @Override
    public boolean onDestroy(Projector projector, Set<BlockPos> field) {
        return false;
    }

    @Override
    public ProjectAction onProject(Projector projector, BlockPos position) {
        return ProjectAction.PROJECT;
    }

    @Override
    public boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity, ItemStack moduleStack) {
        return false;
    }

    @Override
    public Set<Vec3> onPreCalculate(Projector projector, Set<Vec3> calculatedField) {
        return calculatedField;
    }

    @Override
    public void onCalculate(Projector projector, Set<BlockPos> fieldDefinition) {

    }

    @Override
    public boolean requireTicks(ItemStack moduleStack) {
        return false;
    }
}

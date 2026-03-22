package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ProjectorMenu extends FortronMenu<ProjectorBlockEntity> {
    private int clientFortronCost;
    private int clientBiometricWarning;

    public ProjectorMenu(World world, BlockPos pos, EntityPlayer player, InventoryPlayer playerInventory) {
        super(world, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addIntDataSlot(this.blockEntity::getFortronCost, i -> this.clientFortronCost = i);
        addIntDataSlot(this.blockEntity::getBiometricWarningFlag, i -> this.clientBiometricWarning = i);

        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 10, 89));
        addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 28, 89));
        addInventorySlot(new SlotInventory(this.blockEntity.projectorModeSlot, 118, 45, tooltip("mode")));

        addFieldSlot(EnumFacing.UP,    0, 91,             18);
        addFieldSlot(EnumFacing.NORTH, 0, 91 + 18,        18);
        addFieldSlot(EnumFacing.NORTH, 1, 91 + 18 * 2,    18);
        addFieldSlot(EnumFacing.UP,    1, 91 + 18 * 3,    18);

        addFieldSlot(EnumFacing.WEST,  0, 91,             18 * 2);
        addFieldSlot(EnumFacing.WEST,  1, 91,             18 * 3);
        addFieldSlot(EnumFacing.EAST,  0, 91 + 18 * 3,   18 * 2);
        addFieldSlot(EnumFacing.EAST,  1, 91 + 18 * 3,   18 * 3);

        addFieldSlot(EnumFacing.DOWN,  0, 91,             18 * 4);
        addFieldSlot(EnumFacing.SOUTH, 0, 91 + 18,        18 * 4);
        addFieldSlot(EnumFacing.SOUTH, 1, 91 + 18 * 2,    18 * 4);
        addFieldSlot(EnumFacing.DOWN,  1, 91 + 18 * 3,    18 * 4);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addInventorySlot(new SlotInventory(
                    this.blockEntity.upgradeSlots.get(x + y * 3),
                    19 + x * 18, 36 + y * 18));
            }
        }
    }

    private void addFieldSlot(EnumFacing side, int index, int x, int y) {
        addInventorySlot(new SlotInventory(
            this.blockEntity.fieldModuleSlots.get(side).get(index), x, y,
            tooltip(side.getName())));
    }

    private ITextComponent tooltip(String name) {
        return new TextComponentTranslation(MFFSMod.MODID + ".projector_menu." + name);
    }

    public int getClientFortronCost() {
        return this.clientFortronCost;
    }

    public boolean getClientBiometricWarning() {
        return this.clientBiometricWarning != 0;
    }
}

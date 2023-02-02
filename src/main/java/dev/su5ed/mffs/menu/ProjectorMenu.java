package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.inventory.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class ProjectorMenu extends FortronMenu<ProjectorBlockEntity> {

    private int clientFortronCost;

    public ProjectorMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.PROJECTOR_MENU.get(), ModObjects.PROJECTOR_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);

        layoutPlayerInventorySlots(8, 135);
        addIntDataSlot(this.blockEntity::getFortronCost, i -> this.clientFortronCost = i);

        addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 10, 89));
        addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 28, 89));

        addInventorySlot(new SlotInventory(this.blockEntity.projectorModeSlot, 118, 45, tooltip("mode")));

        addFieldSlot(Direction.UP, 0, 91, 18);
        addFieldSlot(Direction.NORTH, 0, 91 + 18, 18);
        addFieldSlot(Direction.NORTH, 1, 91 + 18 * 2, 18);
        addFieldSlot(Direction.UP, 1, 91 + 18 * 3, 18);

        addFieldSlot(Direction.WEST, 0, 91, 18 * 2);
        addFieldSlot(Direction.WEST, 1, 91, 18 * 3);
        addFieldSlot(Direction.EAST, 0, 91 + 18 * 3, 18 * 2);
        addFieldSlot(Direction.EAST, 1, 91 + 18 * 3, 18 * 3);

        addFieldSlot(Direction.DOWN, 0, 91, 18 * 4);
        addFieldSlot(Direction.SOUTH, 0, 91 + 18, 18 * 4);
        addFieldSlot(Direction.SOUTH, 1, 91 + 18 * 2, 18 * 4);
        addFieldSlot(Direction.DOWN, 1, 91 + 18 * 3, 18 * 4);

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                addInventorySlot(new SlotInventory(this.blockEntity.upgradeSlots.get(x + y * 3), 19 + x * 18, 36 + y * 18));
            }
        }
    }

    private void addFieldSlot(Direction side, int index, int x, int y) {
        addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(side).get(index), x, y, tooltip(side.getName())));
    }

    private Component tooltip(String name) {
        return Component.translatable(MFFSMod.MODID + ".projector_menu." + name);
    }

    public int getClientFortronCost() {
        return this.clientFortronCost;
    }
}

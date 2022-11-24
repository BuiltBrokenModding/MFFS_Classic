package dev.su5ed.mffs.menu;

import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.setup.ModMenus;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.SlotInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ProjectorMenu extends FortronMenu<ProjectorBlockEntity> {
    
    public ProjectorMenu(int containerId, BlockPos pos, Player player, Inventory playerInventory) {
        super(ModMenus.PROJECTOR_MENU.get(), ModObjects.PROJECTOR_BLOCK_ENTITY.get(), containerId, pos, player, playerInventory);
        
        layoutPlayerInventorySlots(8, 135);
        
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            addInventorySlot(new SlotInventory(this.blockEntity.frequencySlot, 10, 89));
            addInventorySlot(new SlotInventory(this.blockEntity.secondaryCard, 28, 89));
            
            addInventorySlot(new SlotInventory(this.blockEntity.projectorModeSlot, 118, 45));
            
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.UP).get(0), 91, 18));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.NORTH).get(0), 91 + 18, 18));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.NORTH).get(1), 91 + 18 * 2, 18));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.UP).get(1), 91 + 18 * 3, 18));
            
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.WEST).get(0), 91, 18 * 2));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.WEST).get(1), 91, 18 * 3));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.EAST).get(0), 91 + 18 * 3, 18 * 2));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.EAST).get(1), 91 + 18 * 3, 18 * 3));
            
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.DOWN).get(0), 91, 18 * 4));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.SOUTH).get(0), 91 + 18, 18 * 4));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.SOUTH).get(1), 91 + 18 * 2, 18 * 4));
            addInventorySlot(new SlotInventory(this.blockEntity.fieldModuleSlots.get(Direction.DOWN).get(1), 91 + 18 * 3, 18 * 4));

            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 3; x++) {
                    addInventorySlot(new SlotInventory(this.blockEntity.upgradeSlots.get(x + y * 3), 19 + x * 18, 36 + y * 18));
                }
            }
        });
    }
}

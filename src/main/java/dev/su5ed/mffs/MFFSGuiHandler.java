package dev.su5ed.mffs;

import dev.su5ed.mffs.blockentity.BiometricIdentifierBlockEntity;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity;
import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import dev.su5ed.mffs.menu.BiometricIdentifierMenu;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.menu.InterdictionMatrixMenu;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.screen.BiometricIdentifierScreen;
import dev.su5ed.mffs.screen.CoercionDeriverScreen;
import dev.su5ed.mffs.screen.FortronCapacitorScreen;
import dev.su5ed.mffs.screen.InterdictionMatrixScreen;
import dev.su5ed.mffs.screen.ProjectorScreen;
import dev.su5ed.mffs.setup.GuiIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class MFFSGuiHandler implements IGuiHandler {

    @Override
    @Nullable
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        switch (id) {
            case GuiIds.COERCION_DERIVER:
                return new CoercionDeriverMenu(world, pos, player, player.inventory);
            case GuiIds.FORTRON_CAPACITOR:
                return new FortronCapacitorMenu(world, pos, player, player.inventory);
            case GuiIds.INTERDICTION_MATRIX:
                return new InterdictionMatrixMenu(world, pos, player, player.inventory);
            case GuiIds.BIOMETRIC_IDENTIFIER:
                return new BiometricIdentifierMenu(world, pos, player, player.inventory);
            case GuiIds.PROJECTOR:
                return new ProjectorMenu(world, pos, player, player.inventory);
            case GuiIds.REMOTE_CONTROLLER:
                // Determine the appropriate container based on the TileEntity at pos
                return getServerGuiElementForTileEntity(world, pos, player);
            default:
                return null;
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        switch (id) {
            case GuiIds.COERCION_DERIVER: {
                CoercionDeriverMenu menu = new CoercionDeriverMenu(world, pos, player, player.inventory);
                return new CoercionDeriverScreen(menu, player.inventory);
            }
            case GuiIds.FORTRON_CAPACITOR: {
                FortronCapacitorMenu menu = new FortronCapacitorMenu(world, pos, player, player.inventory);
                return new FortronCapacitorScreen(menu, player.inventory);
            }
            case GuiIds.INTERDICTION_MATRIX: {
                InterdictionMatrixMenu menu = new InterdictionMatrixMenu(world, pos, player, player.inventory);
                return new InterdictionMatrixScreen(menu, player.inventory);
            }
            case GuiIds.BIOMETRIC_IDENTIFIER: {
                BiometricIdentifierMenu menu = new BiometricIdentifierMenu(world, pos, player, player.inventory);
                return new BiometricIdentifierScreen(menu, player.inventory);
            }
            case GuiIds.PROJECTOR: {
                ProjectorMenu menu = new ProjectorMenu(world, pos, player, player.inventory);
                return new ProjectorScreen(menu, player.inventory);
            }
            case GuiIds.REMOTE_CONTROLLER: {
                // Determine GUI based on TileEntity type at pos
                Object serverGui = getServerGuiElementForTileEntity(world, pos, player);
                if (serverGui == null) return null;
                if (serverGui instanceof CoercionDeriverMenu m) return new CoercionDeriverScreen(m, player.inventory);
                if (serverGui instanceof FortronCapacitorMenu m) return new FortronCapacitorScreen(m, player.inventory);
                if (serverGui instanceof InterdictionMatrixMenu m) return new InterdictionMatrixScreen(m, player.inventory);
                if (serverGui instanceof BiometricIdentifierMenu m) return new BiometricIdentifierScreen(m, player.inventory);
                if (serverGui instanceof ProjectorMenu m) return new ProjectorScreen(m, player.inventory);
                return null;
            }
            default:
                return null;
        }
    }

    /** Determines the appropriate Container for a remote-controlled TileEntity based on its type. */
    @Nullable
    private Object getServerGuiElementForTileEntity(World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos);
        FortronMenu<?> menu = null;
        if (te instanceof CoercionDeriverBlockEntity)     menu = new CoercionDeriverMenu(world, pos, player, player.inventory);
        else if (te instanceof FortronCapacitorBlockEntity)    menu = new FortronCapacitorMenu(world, pos, player, player.inventory);
        else if (te instanceof InterdictionMatrixBlockEntity)  menu = new InterdictionMatrixMenu(world, pos, player, player.inventory);
        else if (te instanceof BiometricIdentifierBlockEntity) menu = new BiometricIdentifierMenu(world, pos, player, player.inventory);
        else if (te instanceof ProjectorBlockEntity)           menu = new ProjectorMenu(world, pos, player, player.inventory);
        if (menu != null) menu.setRemoteAccess(true);
        return menu;
    }
}

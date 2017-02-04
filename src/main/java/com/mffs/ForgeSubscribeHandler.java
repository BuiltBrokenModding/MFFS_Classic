package com.mffs;

import com.builtbroken.mc.core.asm.ChunkSetBlockEvent;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.fortron.FrequencyGrid;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.api.utils.MatrixHelper;
import com.mffs.api.vector.Vector3D;
import com.mffs.common.blocks.BlockForceField;
import com.mffs.common.blocks.BlockInterdictionMatrix;
import com.mffs.common.fluids.Fortron;
import com.mffs.common.items.modules.interdiction.ItemModuleAntiSpawn;
import com.mffs.common.tile.type.TileForceFieldProjector;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidRegistry;

public class ForgeSubscribeHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void preTextureHook(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0)
            FluidRegistry.getFluid(Fortron.FLUID_ID).setIcons(event.map.registerIcon(ModularForcefieldSystem.MODID + ":fortron"));
    }

    @SubscribeEvent
    public void chunkModify(ChunkSetBlockEvent event) {
        if (event.world.isRemote || !(event.block instanceof BlockAir))
            return;
        Vector3D vec = new Vector3D(event.x + (event.getChunk().xPosition << 4), event.y, event.z + (event.getChunk().zPosition << 4));
        for (IBlockFrequency freq : FrequencyGrid.instance().get()) {
            if (freq instanceof TileForceFieldProjector && ((TileEntity) freq).getWorldObj() == event.world) {
                TileForceFieldProjector proj = (TileForceFieldProjector) freq;
                if (proj.getCalculatedField() != null && !proj.markFieldUpdate
                        && proj.getCalculatedField().contains(vec)) {
                    proj.markFieldUpdate = true;
                    break;
                }
            }
        }
    }

    /* Message that is sent to the user */
    private static final ChatComponentText ACTION_DENIED = new ChatComponentText("[InterdictionMatrix] You have no permission to do that!");

    @SubscribeEvent
    public void playerInteraction(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)
            return;

        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && event.world.getBlock(event.x, event.y, event.z) instanceof BlockForceField) {
            event.setCanceled(true);
            return;
        }

        if (event.entityPlayer.capabilities.isCreativeMode && SettingConfiguration.INTERACT_CREATIVE)
            return;

        Vector3D vec = new Vector3D(event.x, event.y, event.z);
        IInterdictionMatrix matrix = MatrixHelper.findMatrix(event.world, vec);
        if(matrix != null) {
            Block block = vec.getBlock(event.world);
            if(block instanceof BlockInterdictionMatrix && MatrixHelper.hasPermission(matrix, event.entityPlayer.getGameProfile().getName(), Permission.CONFIGURE))
                return;
            if(!MatrixHelper.hasPermission(matrix, event.action, event.entityPlayer)) {
                event.entityPlayer.addChatMessage(ACTION_DENIED);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void livingSpawnEvent(LivingSpawnEvent event) {
        if(event.world.isRemote || event.entity instanceof EntityPlayer)
            return;
        IInterdictionMatrix matrix = MatrixHelper.findMatrix(event.world, new Vector3D(event.entityLiving));
        if(matrix != null && matrix.getModuleCount(ItemModuleAntiSpawn.class) > 0)
            if(matrix.getModuleCount(ItemModuleAntiSpawn.class) > 0)
                event.setResult(Event.Result.DENY);
    }
}

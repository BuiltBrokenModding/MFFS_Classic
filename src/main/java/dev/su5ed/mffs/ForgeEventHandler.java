package dev.su5ed.mffs;

import dev.su5ed.mffs.api.EventForceManipulate;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.InterdictionDamageSource;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ForgeEventHandler {

    @SubscribeEvent
    public void eventPreForceManipulate(EventForceManipulate.EventPreForceManipulate event) {
        TileEntity te = event.getWorld().getTileEntity(event.getBeforePos());
        if (te instanceof FortronBlockEntity fortronBlockEntity) {
            fortronBlockEntity.setMarkSendFortron(false);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        onPlayerInteractInternal(event, event.getEntityPlayer(), event.getWorld(), event.getPos(), Fortron.Action.RIGHT_CLICK_BLOCK);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        // If the block is a force field, cancel the interaction entirely
        if (world.getBlockState(pos).getBlock() == ModBlocks.FORCE_FIELD) {
            event.setCanceled(true);
        } else {
            onPlayerInteractInternal(event, event.getEntityPlayer(), world, pos, Fortron.Action.LEFT_CLICK_BLOCK);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource() instanceof InterdictionDamageSource)) return;
        // Only handle mob drops — never interfere with player death drops
        if (event.getEntityLiving() instanceof EntityPlayer) return;

        InterdictionDamageSource source = (InterdictionDamageSource) event.getSource();
        InterdictionMatrix im = source.getInterdictionMatrix();
        MFFSConfig.InterdictionDropMode mode = MFFSConfig.interdictionMobDropMode;

        // Drain kill energy once on actual mob death
        if (MFFSConfig.interdictionMatrixMobKillEnergy > 0) {
            net.minecraft.tileentity.TileEntity be = im.be();
            if (be.hasCapability(dev.su5ed.mffs.setup.ModCapabilities.FORTRON, null)) {
                dev.su5ed.mffs.api.fortron.FortronStorage fortron = be.getCapability(dev.su5ed.mffs.setup.ModCapabilities.FORTRON, null);
                if (fortron != null) {
                    fortron.extractFortron(MFFSConfig.interdictionMatrixMobKillEnergy, false);
                }
            }
        }

        switch (mode) {
            case NORMAL:
                return;
            case DISABLED:
                event.setCanceled(true);
                return;
            case COLLECTION_OPTIONAL:
                if (im.hasModule(ModModules.COLLECTION)) {
                    collectDrops(im, event);
                }
                return;
            case COLLECTION_REQUIRED:
                if (im.hasModule(ModModules.COLLECTION)) {
                    collectDrops(im, event);
                } else {
                    event.setCanceled(true);
                }
                return;
        }
    }

    private void collectDrops(InterdictionMatrix im, LivingDropsEvent event) {
        for (EntityItem entityItem : event.getDrops()) {
            im.mergeIntoInventory(entityItem.getItem());
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void livingSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        BlockPos pos = new BlockPos(MathHelper.floor(event.getX()), MathHelper.floor(event.getY()), MathHelper.floor(event.getZ()));
        InterdictionMatrix interdictionMatrix = Fortron.getNearestInterdictionMatrix(event.getWorld(), pos);
        if (interdictionMatrix != null && interdictionMatrix.hasModule(ModModules.ANTI_SPAWN)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!MFFSConfig.giveGuidebookOnFirstJoin) return;
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntityPlayerMP)) return;
        if (!Loader.isModLoaded("patchouli")) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        NBTTagCompound data = player.getEntityData();
        if (data.getBoolean("mffs_receivedHandbook")) return;

        Item bookItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli", "guide_book"));
        if (bookItem == null) return;

        data.setBoolean("mffs_receivedHandbook", true);

        ItemStack stack = new ItemStack(bookItem);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("patchouli:book", MFFSMod.MODID + ":handbook");
        stack.setTagCompound(tag);
        player.inventory.addItemStackToInventory(stack);
    }

    /**
     * When a player logs into the server, push all currently-active Interdiction Matrix zones
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        World world = player.world;
        for (TileEntity te : world.loadedTileEntityList) {
            if (te instanceof InterdictionMatrixBlockEntity im) {
                im.sendZoneSyncTo(player);
            }
        }
    }

    private void onPlayerInteractInternal(PlayerInteractEvent event, EntityPlayer player, World world, BlockPos pos, Fortron.Action action) {
        if (!player.isCreative()) {
            InterdictionMatrix interdictionMatrix = Fortron.getNearestInterdictionMatrix(world, pos);
            if (interdictionMatrix != null) {
                if (world.getBlockState(pos).getBlock() == ModBlocks.BIOMETRIC_IDENTIFIER
                    && Fortron.isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.CONFIGURE_SECURITY_CENTER)) {
                    return;
                }
                if (!Fortron.hasPermission(world, pos, interdictionMatrix, action, player)) {
                    player.sendStatusMessage(ModUtil.translate("info", "interdiction_matrix.no_permission", interdictionMatrix.getTitle()), false);
                    event.setCanceled(true);
                }
            }
        }
    }
}

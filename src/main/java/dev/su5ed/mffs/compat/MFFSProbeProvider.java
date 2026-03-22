package dev.su5ed.mffs.compat;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.ForceFieldBlockEntity;
import dev.su5ed.mffs.blockentity.FortronBlockEntity;
import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.function.Function;

/**
 * TheOneProbe integration for MFFS (1.12.2).
 *
 * Registered via FML IMC in {@link MFFSMod#postInit} using
 * {@code FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", ...)}.
 * TOP instantiates this class and calls {@link #apply(ITheOneProbe)} to register
 * both the info provider (FE / Fortron readouts) and the display override
 * (shows camouflaged block name/icon instead of "Force Field").
 */
public class MFFSProbeProvider implements IBlockDisplayOverride, IProbeInfoProvider, Function<ITheOneProbe, Void> {
    private static final String ID = MFFSMod.MODID + ":probe";

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerProvider(this);
        probe.registerBlockDisplayOverride(this);
        return null;
    }

    // -------------------------------------------------------------------------
    // IBlockDisplayOverride: show camouflage block info instead of force field
    // -------------------------------------------------------------------------

    @Override
    public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player,
            World world, IBlockState blockState, IProbeHitData data) {
        BlockPos pos = data.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ForceFieldBlockEntity) {
            IBlockState camo = ((ForceFieldBlockEntity) te).getCamouflage();
            if (camo != null) {
                RayTraceResult fakeHit = new RayTraceResult(data.getHitVec(), data.getSideHit(), pos);
                ItemStack clone = camo.getBlock().getPickBlock(camo, fakeHit, world, pos, player);
                DefaultProbeInfoProvider.showStandardBlockInfo(Config.getRealConfig(), mode, probeInfo,
                    camo, camo.getBlock(), world, pos, player,
                    new ProbeHitData(pos, data.getHitVec(), data.getSideHit(), clone));
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // IProbeInfoProvider: add FE and Fortron readouts
    // -------------------------------------------------------------------------

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player,
            World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        // Note: FE/RF bar is already rendered by TOP's built-in DefaultProbeInfoProvider
        // via the IEnergyStorage capability. We only add the Fortron bar here.
        if (te instanceof FortronBlockEntity) {
            FortronBlockEntity fortron = (FortronBlockEntity) te;
            int stored = fortron.fortronStorage.getStoredFortron();
            int capacity = fortron.fortronStorage.getFortronCapacity();
            if (capacity > 0) {
                addFortronInfo(probeInfo, stored, capacity);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void addFortronInfo(IProbeInfo probeInfo, int stored, int capacity) {
        probeInfo.progress(
            stored,
            capacity,
            probeInfo.defaultProgressStyle()
                .showText(true)
                .suffix(" F")
                .filledColor(0xFF00AAFF)
                .alternateFilledColor(0xFF00CCFF)
                .borderColor(0xFF004488));
    }
}

package dev.su5ed.mffs.util;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.network.DrawBeamPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.render.particle.BeamParticleOptions;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModFluids;
import dev.su5ed.mffs.setup.ModModules;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Optional;

/**
 * A class with useful functions related to Fortron.
 *
 * @author Calclavia
 */
public final class Fortron {
    public static FluidStack getFortron(int amount) {
        return new FluidStack(ModFluids.FORTRON_FLUID.get(), amount);
    }

    public static void transferFortron(FortronStorage transmitter, Collection<? extends FortronStorage> receivers, TransferMode transferMode, int limit) {
        if (transmitter != null && receivers.size() > 1) {
            // Check spread mode. Equal, Give All, Take All
            int totalFortron = 0;
            int totalCapacity = 0;

            for (FortronStorage storage : receivers) {
                totalFortron += storage.getFortronStored();
                totalCapacity += storage.getMaxFortron();
            }

            receivers.remove(transmitter);

            if (totalFortron > 0 && totalCapacity > 0) {
                // Test each mode and based on the mode, spread Fortron energy.
                switch (transferMode) {
                    case EQUALIZE -> {
                        for (FortronStorage machine : receivers) {
                            double capacityPercentage = (double) machine.getMaxFortron() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);
                            doTransferFortron(transmitter, machine, amountToSet - machine.getFortronStored(), limit);
                        }
                    }
                    case DISTRIBUTE -> {
                        final int amountToSet = totalFortron / receivers.size();

                        for (FortronStorage machine : receivers) {
                            doTransferFortron(transmitter, machine, amountToSet - machine.getFortronStored(), limit);
                        }
                    }
                    case DRAIN -> {
                        for (FortronStorage machine : receivers) {
                            double capacityPercentage = (double) machine.getMaxFortron() / (double) totalCapacity;
                            int amountToSet = (int) (totalFortron * capacityPercentage);

                            if (amountToSet - machine.getFortronStored() > 0) {
                                doTransferFortron(transmitter, machine, amountToSet - machine.getFortronStored(), limit);
                            }
                        }
                    }
                    case FILL -> {
                        if (transmitter.getFortronStored() < transmitter.getMaxFortron()) {
                            // The amount of energy required to be full.
                            int requiredFortron = transmitter.getMaxFortron() - transmitter.getFortronStored();

                            for (FortronStorage machine : receivers) {
                                int amountToConsume = Math.min(requiredFortron, machine.getFortronStored());
                                int amountToSet = -machine.getFortronStored() - amountToConsume;

                                if (amountToConsume > 0) {
                                    doTransferFortron(transmitter, machine, amountToSet - machine.getFortronStored(), limit);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to transfer Fortron to a specific machine from this capacitor. Renders an animation on
     * the client side.
     *
     * @param receiver : The machine to be transfered to.
     * @param joules   : The amount of energy to be transfered.
     */
    public static void doTransferFortron(FortronStorage transmitter, FortronStorage receiver, int joules, int limit) {
        boolean isCamo = transmitter instanceof ModuleAcceptor acceptor && acceptor.hasModule(ModModules.CAMOUFLAGE);
        if (joules > 0) {
            doTransferFortron(transmitter, receiver, joules, limit, isCamo);
        } else {
            doTransferFortron(receiver, transmitter, joules, limit, isCamo);
        }
    }

    public static void renderBeam(ClientLevel level, Vec3 target, Vec3 position, ParticleColor color, int lifetime) {
        level.addParticle(new BeamParticleOptions(target, color, lifetime), position.x(), position.y(), position.z(), 0, 0, 0);
    }

    public static void renderClientBeam(Level level, Vec3 target, Vec3 position, BlockPos chunkPos, ParticleColor color, int lifetime) {
        DrawBeamPacket packet = new DrawBeamPacket(target, position, color, lifetime);
        Network.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(chunkPos)), packet);
    }

    public static boolean hasPermission(Level level, BlockPos pos, FieldPermission permission, Player player) {
        InterdictionMatrix interdictionMatrix = getNearestInterdictionMatrix(level, pos);
        return interdictionMatrix == null || isPermittedByInterdictionMatrix(interdictionMatrix, player, permission);
    }

    public static boolean hasPermission(Level level, BlockPos pos, InterdictionMatrix interdictionMatrix, Action action, Player player) {
        boolean hasPermission = true;
        if (action == Action.RIGHT_CLICK_BLOCK && level.getBlockEntity(pos) != null && interdictionMatrix.hasModule(ModModules.BLOCK_ACCESS)) {
            hasPermission = isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.USE_BLOCKS);
        }
        if (hasPermission && interdictionMatrix.hasModule(ModModules.BLOCK_ALTER) && (player.getItemInHand(InteractionHand.MAIN_HAND) != null || action == Action.LEFT_CLICK_BLOCK)) {
            hasPermission = isPermittedByInterdictionMatrix(interdictionMatrix, player, FieldPermission.PLACE_BLOCKS);
        }
        return hasPermission;
    }

    public static InterdictionMatrix getNearestInterdictionMatrix(Level level, BlockPos pos) {
        return StreamEx.of(FrequencyGrid.instance(level.isClientSide).get())
            .mapPartial(storage -> {
                BlockEntity be = storage.getOwner();
                return be.getLevel() == level ? be.getCapability(ModCapabilities.INTERDICTION_MATRIX)
                    .filter(interdictionMatrix -> interdictionMatrix.isActive() && pos.closerThan(be.getBlockPos(), interdictionMatrix.getActionRange()))
                    : Optional.empty();
            })
            .findFirst()
            .orElse(null);
    }

    public static boolean isPermittedByInterdictionMatrix(InterdictionMatrix interdictionMatrix, Player player, FieldPermission... permissions) {
        if (interdictionMatrix != null && interdictionMatrix.isActive() && interdictionMatrix.getBiometricIdentifier() != null) {
            for (FieldPermission permission : permissions) {
                if (!interdictionMatrix.getBiometricIdentifier().isAccessGranted(player, permission)) {
                    return interdictionMatrix.hasModule(ModModules.INVERTER);
                }
            }
        }
        return !interdictionMatrix.hasModule(ModModules.INVERTER);
    }

    private static void doTransferFortron(FortronStorage source, FortronStorage destination, int joules, int limit, boolean isCamo) {
        // Take energy from receiver.
        int transfer = Math.min(Math.abs(joules), limit);
        int available = destination.receiveFortron(source.extractFortron(transfer, true), true);
        int transferred = source.extractFortron(destination.receiveFortron(available, false), false);

        // Draw Beam Effect
        if (transferred > 0 && !isCamo) {
            BlockEntity sourceBe = source.getOwner();
            BlockPos sourcePos = sourceBe.getBlockPos();
            Level level = sourceBe.getLevel();
            Vec3 target = Vec3.atCenterOf(destination.getOwner().getBlockPos());
            Vec3 position = Vec3.atCenterOf(sourcePos);
            ParticleColor color = ParticleColor.BLUE_BEAM;
            int lifetime = 20;
            if (level.isClientSide()) {
                ClientLevel clientLevel = (ClientLevel) level;
                renderBeam(clientLevel, target, position, color, lifetime);
            } else {
                renderClientBeam(level, target, position, sourcePos, color, lifetime);
            }
        }
    }

    private Fortron() {}

    public enum Action {
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }

    /* static helper functions */

    public static int convertFortronToEnergy(int fortron) {
        return (int) (fortron / MFFSConfig.COMMON.energyConversionRatio.get());
    }

    public static int convertEnergyToFortron(int energy) {
        return (int) (energy * MFFSConfig.COMMON.energyConversionRatio.get());
    }
}

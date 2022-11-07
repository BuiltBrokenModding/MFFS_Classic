package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.api.FrequencyBlock;
import dev.su5ed.mffs.api.card.CardInfinite;
import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronCapacitor;
import dev.su5ed.mffs.api.fortron.FortronFrequency;
import dev.su5ed.mffs.api.fortron.FrequencyGrid;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyCard;
import dev.su5ed.mffs.util.InventorySlot;
import dev.su5ed.mffs.util.TransferMode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FortronCapacitorBlockEntity extends ModularBlockEntity implements FortronCapacitor, MenuProvider {
    public final InventorySlot secondaryCard;

    private TransferMode transferMode = TransferMode.EQUALIZE;

    public FortronCapacitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.FORTRON_CAPACITOR_BLOCK_ENTITY.get(), pos, state);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, stack -> stack.getItem() instanceof FrequencyCard);
        this.capacityBoost = 10;
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return 700;
    }

    @Override
    protected float getAmplifier() {
        return 0.001f;
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tickServer() {
        super.tickServer();

        consumeCost();

        // Transmit Fortrons in frequency network, evenly distributing them.
        // Gets the card.
        if (isActive() && getTicks() % 10 == 0) {
            Set<FortronFrequency> machines = new HashSet<>();

            for (ItemStack stack : getCards()) {
                if (stack.getItem() instanceof CardInfinite) {
                    setFortronEnergy(getFortronCapacity());
                } else if (stack.getItem() instanceof CoordLink coordLink) {
                    BlockPos linkPosition = coordLink.getLink(stack);

                    if (linkPosition != null && this.level.getBlockEntity(linkPosition) instanceof FortronFrequency f) {
                        machines.add(this);
                        machines.add(f);
                    }
                }
            }

            if (machines.size() < 1) {
                machines = getLinkedDevices();
            }

            Fortron.transferFortron(this, machines, this.transferMode, getTransmissionRate());
        }
    }

    @Override
    public Set<FortronFrequency> getLinkedDevices() {
        Set<FrequencyBlock> frequencyBlocks = FrequencyGrid.instance().get(this.level, this.worldPosition, getTransmissionRange(), getFrequency());
        return StreamEx.of(frequencyBlocks)
            .select(FortronFrequency.class)
            .toSet();
    }

    @Override
    public List<ItemStack> getCards() {
        return List.of(this.frequencySlot.getItem(), this.secondaryCard.getItem());
    }

    @Override
    public int getTransmissionRange() {
        return 15 + getModuleCount(ModItems.SCALE_MODULE.get());
    }

    @Override
    public int getTransmissionRate() {
        return 250 + 50 * getModuleCount(ModItems.SPEED_MODULE.get());
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);
        tag.putString("transferMode", this.transferMode.name());
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        this.transferMode = TransferMode.valueOf(tag.getString("transferMode"));
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FortronCapacitorMenu(containerId, this.worldPosition, player, inventory);
    }
}

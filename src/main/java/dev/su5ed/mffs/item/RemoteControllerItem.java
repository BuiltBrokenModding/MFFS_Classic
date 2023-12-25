package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FrequencyGrid;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RemoteControllerItem extends BaseItem implements CoordLink {

    public RemoteControllerItem() {
        super(new ExtendedItemProperties(new Item.Properties().stacksTo(1)).description());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide && player.isShiftKeyDown()) {
            BlockPos pos = context.getClickedPos();
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && level.getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null) != null) {
                setLink(stack, pos);
                BlockState state = level.getBlockState(pos);
                player.displayClientMessage(ModUtil.translate("info", "link", state.getBlock().getName(), pos.toShortString()).withStyle(ChatFormatting.AQUA), true);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            BlockPos pos = getLink(stack);

            if (pos != null && level.isLoaded(pos)) {
                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof MenuProvider menuProvider && (Fortron.hasPermission(level, pos, FieldPermission.USE_BLOCKS, player) || Fortron.hasPermission(level, pos, FieldPermission.REMOTE_CONTROL, player))) {
                    double requiredEnergy = ModUtil.distance(player.blockPosition(), pos) * (FluidType.BUCKET_VOLUME / 100.0);
                    int frequency = Objects.requireNonNull(level.getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null)).getFrequency();
                    if (drawEnergy(level, player.blockPosition(), player.position().add(0, player.getEyeHeight() - 0.2, 0), frequency, (int) requiredEnergy)) {
                        NetworkHooks.openScreen((ServerPlayer) player, new RemoteMenuProvider(menuProvider), pos);
                        return InteractionResultHolder.success(stack);
                    }

                    player.displayClientMessage(ModUtil.translate("info", "cannot_harness", Math.round(requiredEnergy)).withStyle(ChatFormatting.RED), true);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        BlockPos pos = getLink(stack);
        if (level != null && pos != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && level.getCapability(ModCapabilities.FORTRON, be.getBlockPos(), be.getBlockState(), be, null) != null) {
                tooltipComponents.add(ModUtil.translate("info", "link",
                    be.getBlockState().getBlock().getName().withStyle(ChatFormatting.GREEN),
                    Component.literal(pos.toShortString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Nullable
    @Override
    public BlockPos getLink(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return NbtUtils.readBlockPos(tag.getCompound("link"));
    }

    @Override
    public void setLink(ItemStack stack, BlockPos pos) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("link", NbtUtils.writeBlockPos(pos));
    }

    private boolean drawEnergy(Level level, BlockPos pos, Vec3 target, int frequency, int energy) {
        List<FortronStorage> fortronTiles = FrequencyGrid.instance().get(level, pos, 50, frequency);
        fortronTiles.sort(Comparator.comparingDouble(fortron -> fortron.getOwner().getBlockPos().distToCenterSqr(target)));
        int total = 0;
        List<FortronStorage> transmitters = new ArrayList<>();
        // Find providers
        for (FortronStorage fortron : fortronTiles) {
            int required = energy - total;
            int receivedEnergy = fortron.extractFortron(required, true);
            if (receivedEnergy > 0) {
                transmitters.add(fortron);
            }
            total += receivedEnergy;
            if (total >= energy) {
                break;
            }
        }
        if (total >= energy) {
            total = 0;
            // Draw energy
            for (FortronStorage fortron : transmitters) {
                int required = energy - total;
                total += fortron.extractFortron(required, false);
                BlockPos fortronPos = fortron.getOwner().getBlockPos();
                Fortron.renderClientBeam(level, target, Vec3.atCenterOf(fortronPos), fortronPos, ParticleColor.BLUE_BEAM, 20);
            }
            return true;
        }
        return false;
    }

    private record RemoteMenuProvider(MenuProvider wrapped) implements MenuProvider {

        @Override
        public Component getDisplayName() {
            return this.wrapped.getDisplayName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            AbstractContainerMenu menu = this.wrapped.createMenu(containerId, playerInventory, player);
            if (menu instanceof FortronMenu<?> fortronMenu) {
                fortronMenu.setRemoteAccess(true);
            }
            return menu;
        }
    }
}

package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.card.CoordLink;
import dev.su5ed.mffs.api.fortron.FortronStorage;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.render.particle.ParticleColor;
import dev.su5ed.mffs.setup.ModItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class RemoteControllerItem extends FrequencyCardItem implements CoordLink {

    public RemoteControllerItem() {
        super(ModItems.itemProperties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (!level.isClientSide && player.isShiftKeyDown()) {
            BlockPos pos = context.getClickedPos();
            setLink(stack, pos);

            BlockState state = level.getBlockState(pos);
            player.displayClientMessage(ModUtil.translate("info", "link.create", state.getBlock().getName(), pos.toShortString()).withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.SUCCESS;
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

                if (be instanceof MenuProvider menuProvider /* TODO PERMISSIONS */) {
                    double requiredEnergy = ModUtil.distance(player.blockPosition(), pos) * (FluidType.BUCKET_VOLUME / 100.0);
                    int receivedEnergy = 0;

                    Set<? extends FortronStorage> fortronTiles = FrequencyGrid.instance().getFortronBlocks(level, player.blockPosition(), 50, getFrequency(stack));

                    for (FortronStorage fortron : fortronTiles) {
                        BlockPos fortronPos = fortron.getOwner().getBlockPos();
                        int consumedEnergy = fortron.extractFortron((int) Math.ceil(requiredEnergy / fortronTiles.size()), false);

                        if (consumedEnergy > 0) {
                            Fortron.renderClientBeam(level, player.position().add(0, player.getEyeHeight() - 0.2, 0), Vec3.atCenterOf(fortronPos), fortronPos, ParticleColor.BLUE_BEAM, 20);
                            receivedEnergy += consumedEnergy;
                        }

                        if (receivedEnergy >= requiredEnergy) {
                            NetworkHooks.openScreen((ServerPlayer) player, new RemoteMenuProvider(menuProvider), pos);
                            return InteractionResultHolder.success(stack);
                        }
                    }

                    player.displayClientMessage(ModUtil.translate("info", "cannot_harness", Math.round(requiredEnergy)).withStyle(ChatFormatting.RED), true);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        BlockPos pos = getLink(stack);
        if (level != null && pos != null) {
            BlockState state = level.getBlockState(pos);

            if (!state.isAir()) {
                tooltipComponents.add(ModUtil.translate("info", "link.name", state.getBlock().getName()).withStyle(ChatFormatting.GRAY));
                tooltipComponents.add(Component.literal(pos.toShortString()).withStyle(ChatFormatting.GRAY));
                return;
            }
        }

        tooltipComponents.add(ModUtil.translate("info", "link.none").withStyle(ChatFormatting.GRAY));
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

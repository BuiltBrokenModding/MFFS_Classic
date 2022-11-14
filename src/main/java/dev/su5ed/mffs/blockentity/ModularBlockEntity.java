package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ObjectCache;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ModularBlockEntity extends FortronBlockEntity implements ModuleAcceptor, ObjectCache {
    public final List<InventorySlot> upgradeSlots;
    private final int capacityBoost;
    /**
     * Caching for the module stack data. This is used to reduce calculation time. Cache gets reset
     * when inventory changes.
     */
    private final Map<String, Object> cache;

    /**
     * Used for client-side only.
     */
    public int clientFortronCost = 0;
    
    protected ModularBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 5);
    }

    protected ModularBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state, int capacityBoost) {
        super(type, pos, state);
        
        this.capacityBoost = capacityBoost;
        this.cache = new HashMap<>();
        this.upgradeSlots = IntStreamEx.range(3)
            .mapToObj(i -> addSlot("upgrade_" + i, InventorySlot.Mode.NONE, stack -> stack.getItem() instanceof Module))
            .toList();
    }

    public void consumeCost() {
        if (getFortronCost() > 0) {
            requestFortron(getFortronCost(), FluidAction.EXECUTE);
        }
    }
    
    protected float getAmplifier() {
        return 1;
    }
    
    @Override
    public int getBaseFortronTankCapacity() {
        return 500;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateFortronTankCapacity();
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
        updateFortronTankCapacity();
        // Clears the cache.
        clearCache();
    }

    @Override
    public <T extends Item & Module> ItemStack getModule(T module) {
        String cacheID = "getModule_" + module.hashCode();

        if (MFFSConfig.COMMON.useCache.get() && this.cache.get(cacheID) instanceof ItemStack stack) {
            return stack;
        }

        ItemStack returnStack = new ItemStack(module, 0);
        getModuleStacks().stream()
            .filter(stack -> stack.is(module))
            .map(ItemStack::getCount)
            .forEach(returnStack::grow);

        if (MFFSConfig.COMMON.useCache.get()) {
            this.cache.put(cacheID, returnStack.copy());
        }

        return returnStack;
    }

    @Override
    public <T extends Item & Module> int getModuleCount(T module, Collection<InventorySlot> slots) {
        String cacheID = "getModuleCount_" + module.hashCode() + "_" + slots.hashCode();

        if (MFFSConfig.COMMON.useCache.get() && this.cache.get(cacheID) instanceof Integer i) {
            return i;
        }

        int count = getModuleItems(slots)
            .filter(stack -> stack.is(module))
            .mapToInt(ItemStack::getCount)
            .sum();

        if (MFFSConfig.COMMON.useCache.get()) {
            this.cache.put(cacheID, count);
        }

        return count;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ItemStack> getModuleStacks(Collection<InventorySlot> slots) {
        String cacheID = "getModuleStacks_" + slots.hashCode();

        if (MFFSConfig.COMMON.useCache.get() && this.cache.get(cacheID) instanceof Set<?> stacks) {
            return (Set<ItemStack>) stacks;
        }

        Set<ItemStack> modules = getModuleItems(slots)
            .filter(stack -> stack.getItem() instanceof Module)
            .toSet();

        if (MFFSConfig.COMMON.useCache.get()) {
            this.cache.put(cacheID, modules);
        }

        return modules;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Item & Module> Set<T> getModules() {
        String cacheID = "getModules";

        if (MFFSConfig.COMMON.useCache.get() && this.cache.get(cacheID) instanceof Set<?> stacks) {
            return (Set<T>) stacks;
        }

        Set<T> modules = StreamEx.of(this.upgradeSlots)
            .map(slot -> slot.getItem().getItem())
            .select(Module.class)
            .map(item -> (T) item)
            .toSet();

        if (MFFSConfig.COMMON.useCache.get()) {
            this.cache.put(cacheID, modules);
        }

        return modules;
    }

    /**
     * Returns Fortron cost in ticks.
     */
    @Override
    public final int getFortronCost() {
        if (this.level.isClientSide) {
            return this.clientFortronCost;
        }

        String cacheID = "getFortronCost";

        if (MFFSConfig.COMMON.useCache.get() && this.cache.get(cacheID) instanceof Integer i) {
            return i;
        }

        int result = this.doGetFortronCost();

        if (MFFSConfig.COMMON.useCache.get()) {
            this.cache.put(cacheID, result);
        }

        return doGetFortronCost();
    }
    
    @Override
    public Object getCache(String cacheID) {
        return this.cache.get(cacheID);
    }

    @Override
    public void putCache(String cacheID, Object obj) {
        this.cache.put(cacheID, obj);
    }

    @Override
    public void clearCache(String cacheID) {
        this.cache.remove(cacheID);
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }
    
    protected int doGetFortronCost() {
        double cost = StreamEx.of(getModuleStacks())
            .mapToDouble(stack -> stack.getCount() * ((Module) stack.getItem()).getFortronCost(getAmplifier()))
            .sum();
        return (int) Math.round(cost);
    }

    private void updateFortronTankCapacity() {
        int capacity = (getModuleCount(ModItems.CAPACITY_MODULE.get()) * this.capacityBoost + getBaseFortronTankCapacity()) * FluidType.BUCKET_VOLUME;
        this.fortronTank.setCapacity(capacity);
    }
    
    private StreamEx<ItemStack> getModuleItems(Collection<InventorySlot> slots) {
        return slots.isEmpty() ? StreamEx.of(this.upgradeSlots).map(InventorySlot::getItem)
            : StreamEx.of(slots).map(InventorySlot::getItem);
    }
}

package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.ObjectCache;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class ModularBlockEntity extends FortronBlockEntity implements ModuleAcceptor, ObjectCache {
    private static final String FOTRON_COST_CACHE_KEY = "getFortronCost";
    private static final String ALL_MODULES_CACHE_KEY = "getModules";
    private static final String MODULE_COUNT_CACHE_KEY = "getModuleCount_";

    private final int capacityBoost;
    /**
     * Caching for the module stack data. This is used to reduce calculation time. Cache gets reset
     * when inventory changes.
     */
    private final Map<String, Object> cache = Collections.synchronizedMap(new HashMap<>());

    protected ModularBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state) {
        this(type, pos, state, 5);
    }

    protected ModularBlockEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state, int capacityBoost) {
        super(type, pos, state);

        this.capacityBoost = capacityBoost;
    }

    public void consumeCost() {
        if (getFortronCost() > 0) {
            this.fortronStorage.extractFortron(getFortronCost(), false);
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
    public boolean hasModule(Module module) {
        return getAllModuleItemsStream().anyMatch(stack -> ModUtil.isModule(stack, module));
    }

    @Override
    public int getModuleCount(Module module, Collection<InventorySlot> slots) {
        String key = MODULE_COUNT_CACHE_KEY + module.hashCode() + "_" + slots.hashCode();
        return cached(key, () -> getModuleItemsStream(slots)
            .filter(stack -> ModUtil.isModule(stack, module)))
            .mapToInt(ItemStack::getCount)
            .sum();
    }

    @Override
    public Set<ItemStack> getModuleStacks() {
        return getAllModuleItemsStream()
            .filter(ModUtil::isModule)
            .toSet();
    }

    @Override
    public Set<Module> getModules() {
        return cached(ALL_MODULES_CACHE_KEY, () -> getModuleItemsStream(List.of())
            .mapPartial(stack -> stack.getCapability(ModCapabilities.MODULE).resolve())
            .toSet());
    }

    @Override
    public StreamEx<ItemStack> getAllModuleItemsStream() {
        return getModuleItemsStream(List.of());
    }

    /**
     * Returns Fortron cost per tick.
     */
    @Override
    public int getFortronCost() {
        return cached(FOTRON_COST_CACHE_KEY, this::doGetFortronCost);
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }

    @SuppressWarnings("unchecked")
    protected <T> T cached(String key, Supplier<T> calculation) {
        if (MFFSConfig.COMMON.useCache.get()) {
            synchronized (this.cache) {
                Object value = this.cache.get(key);
                if (value == null) {
                    T computed = calculation.get();
                    this.cache.put(key, computed);
                    return computed;
                }
                return (T) value;
            }
        }
        return calculation.get();
    }

    protected int doGetFortronCost() {
        double cost = StreamEx.of(getModuleStacks())
            .mapToDouble(stack -> stack.getCount() * ((Module) stack.getItem()).getFortronCost(getAmplifier()))
            .sum();
        return (int) Math.round(cost);
    }

    protected void addModuleSlots(List<? super InventorySlot> list) {}

    protected List<InventorySlot> createUpgradeSlots(int count) {
        return createUpgradeSlots(count, false);
    }

    protected List<InventorySlot> createUpgradeSlots(int count, boolean allowAny) {
        return IntStreamEx.range(count)
            .mapToObj(i -> addSlot("upgrade_" + i, InventorySlot.Mode.BOTH, stack -> allowAny || ModUtil.isModule(stack)))
            .toList();
    }

    private void updateFortronTankCapacity() {
        int capacity = (getModuleCount(ModModules.CAPACITY) * this.capacityBoost + getBaseFortronTankCapacity()) * FluidType.BUCKET_VOLUME;
        this.fortronStorage.setCapacity(capacity);
    }

    private StreamEx<ItemStack> getModuleItemsStream(Collection<InventorySlot> slots) {
        if (slots.isEmpty()) {
            List<InventorySlot> moduleSlots = new ArrayList<>();
            addModuleSlots(moduleSlots);
            return StreamEx.of(moduleSlots).map(InventorySlot::getItem);
        }
        return StreamEx.of(slots).map(InventorySlot::getItem);
    }
}

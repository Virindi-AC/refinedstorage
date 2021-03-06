package refinedstorage.tile.externalstorage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.config.ModeFilter;

import javax.annotation.Nonnull;
import java.util.List;

public class DeepStorageUnitStorage extends ExternalStorage {
    private TileExternalStorage externalStorage;
    private IDeepStorageUnit unit;

    public DeepStorageUnitStorage(TileExternalStorage externalStorage, IDeepStorageUnit unit) {
        this.externalStorage = externalStorage;
        this.unit = unit;
    }

    @Override
    public int getCapacity() {
        return unit.getMaxStoredCount();
    }

    @Override
    public void addItems(List<ItemStack> items) {
        if (unit.getStoredItemType() != null && unit.getStoredItemType().stackSize > 0) {
            items.add(unit.getStoredItemType().copy());
        }
    }

    @Override
    public ItemStack push(@Nonnull ItemStack stack, int size, boolean simulate) {
        if (ModeFilter.respectsMode(externalStorage.getFilters(), externalStorage, externalStorage.getCompare(), stack)) {
            if (unit.getStoredItemType() != null) {
                if (RefinedStorageUtils.compareStackNoQuantity(unit.getStoredItemType(), stack)) {
                    if (getStored() + size > unit.getMaxStoredCount()) {
                        int remainingSpace = getCapacity() - getStored();

                        if (remainingSpace <= 0) {
                            return ItemHandlerHelper.copyStackWithSize(stack, size);
                        }

                        if (!simulate) {
                            unit.setStoredItemCount(unit.getStoredItemType().stackSize + remainingSpace);
                        }

                        return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                    } else {
                        if (!simulate) {
                            unit.setStoredItemCount(unit.getStoredItemType().stackSize + size);
                        }

                        return null;
                    }
                }
            } else {
                if (getStored() + size > unit.getMaxStoredCount()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        unit.setStoredItemType(stack.copy(), remainingSpace);
                    }

                    return ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        unit.setStoredItemType(stack.copy(), size);
                    }

                    return null;
                }
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack take(@Nonnull ItemStack stack, int size, int flags) {
        if (RefinedStorageUtils.compareStack(stack, unit.getStoredItemType(), flags)) {
            if (size > unit.getStoredItemType().stackSize) {
                size = unit.getStoredItemType().stackSize;
            }

            ItemStack stored = unit.getStoredItemType();

            unit.setStoredItemCount(stored.stackSize - size);

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

        return null;
    }

    @Override
    public int getStored() {
        return unit.getStoredItemType() != null ? unit.getStoredItemType().stackSize : 0;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }
}

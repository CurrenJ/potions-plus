package grill24.potionsplus.item;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.items.FishItems;
import grill24.potionsplus.utility.ItemStacksTooltip;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FishingRodDataComponent(List<ItemStack> fishingRodItems) {
    public static final Codec<FishingRodDataComponent> CODEC = Codec.list(ItemStack.CODEC).xmap(FishingRodDataComponent::new, FishingRodDataComponent::fishingRodItems);
    public static final StreamCodec<RegistryFriendlyByteBuf, FishingRodDataComponent> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FishingRodDataComponent::fishingRodItems,
            FishingRodDataComponent::new
    );

    public FishingRodDataComponent(List<ItemStack> fishingRodItems) {
        this.fishingRodItems = new ArrayList<>(fishingRodItems);
    }

    public FishingRodDataComponent() {
        this(new ArrayList<>());
    }

    public List<ItemStack> getFishingRodItems() {
        return fishingRodItems;
    }

    public List<ItemStack> clearFishingRodItems() {
        List<ItemStack> clearedItems = new ArrayList<>(fishingRodItems);
        fishingRodItems.clear();
        return clearedItems;
    }

    public void consumeBait() {
        for (ItemStack item : fishingRodItems) {
            if (item.getCount() > 0) {
                item.shrink(1);
                if (item.getCount() <= 0) {
                    fishingRodItems.remove(item);
                }
                break;
            }
        }
    }

    public void addFishingRodItem(ItemStack item) {
        if (canAcceptItem(item)) {
            for (ItemStack existingItem : fishingRodItems) {
                // Stack items are the same
                if (item.getCount() > 0 && ItemStack.isSameItem(existingItem, item)) {
                    // Merge the items
                    int growAmount = Math.min(existingItem.getMaxStackSize() - existingItem.getCount(), item.getCount());
                    existingItem.grow(growAmount);
                    item.shrink(growAmount);
                    return;
                }
            }

            if (item.getCount() > 0) {
                // Add the item to the list
                fishingRodItems.add(item.copy());
                item.shrink(item.getCount());
            }
        }
    }

    public boolean canAcceptItem(ItemStack stack) {
        return stack.is(FishItems.WORMS);
    }

    public List<List<ItemStack>> getDisplayFishingRodItems() {
        return List.of(fishingRodItems);
    }

    public Optional<TooltipComponent> getTooltipImage() {
        List<List<ItemStack>> displayItems = getDisplayFishingRodItems();
        if (displayItems.isEmpty() || displayItems.getFirst().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ItemStacksTooltip.of(displayItems, false, true));
    }

    public boolean contains(ItemStack item) {
        for (ItemStack existingItem : fishingRodItems) {
            if (ItemStack.isSameItem(existingItem, item)) {
                return true;
            }
        }
        return false;
    }
}

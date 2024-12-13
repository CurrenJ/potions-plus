package grill24.potionsplus.blockentity;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public class StoredItemsComponent implements INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = ppId("stored_items");
    private final List<ItemStack> stacks = new ArrayList<>();

    public void readFromTag(CompoundTag tag, HolderLookup.Provider registryAccess) {
        stacks.clear();
        ListTag list = tag.getList("stacks", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            ItemStack.parse(registryAccess, t).ifPresent(stacks::add);
        }
    }

    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryAccess) {
        ListTag list = new ListTag();
        for (ItemStack stack : stacks) {
            list.add(stack.save(registryAccess, new CompoundTag()));
        }
        tag.put("stacks", list);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
        var ret = new CompoundTag();
        writeToNbt(ret, registryAccess);
        return ret;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag tag) {
    readFromTag(tag, registryAccess);
    }
}

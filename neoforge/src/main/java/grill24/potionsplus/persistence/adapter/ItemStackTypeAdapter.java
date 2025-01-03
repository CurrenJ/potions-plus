package grill24.potionsplus.persistence.adapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.util.Optional;

public class ItemStackTypeAdapter extends com.google.gson.TypeAdapter<net.minecraft.world.item.ItemStack> {
    private final HolderLookup.Provider registries;
    public ItemStackTypeAdapter(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        if (value.isEmpty())
            return;

        CompoundTag blankTag = new CompoundTag();
        Tag result = value.save(registries, blankTag);
        out.value(result.toString());
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        try {
            String s = in.nextString();
            CompoundTag compoundTag = TagParser.parseTag(s);
            Optional<ItemStack> itemStack = ItemStack.parse(registries, compoundTag);
            if (itemStack.isEmpty()) {
                PotionsPlus.LOGGER.warn("Failed to parse ItemStack from JSON: " + compoundTag);
            }
            return itemStack.orElse(ItemStack.EMPTY);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

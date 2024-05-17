package grill24.potionsplus.persistence.adapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

public class ItemStackTypeAdapter extends com.google.gson.TypeAdapter<net.minecraft.world.item.ItemStack> {
    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        CompoundTag tag = new CompoundTag();
        value.save(tag);
        out.value(tag.toString());
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        try {
            CompoundTag compoundTag = TagParser.parseTag(in.nextString());
            return ItemStack.of(compoundTag);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

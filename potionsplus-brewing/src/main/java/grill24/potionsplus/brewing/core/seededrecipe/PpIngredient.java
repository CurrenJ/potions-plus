package grill24.potionsplus.core.seededrecipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public class PpIngredient {
    public List<ItemStack> matchStacks;

    // Use toString() as a simple unique identifier for the ingredient.

    public static final Codec<PpIngredient> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ItemStack.STRICT_CODEC.listOf().fieldOf("matchStacks").forGetter(data -> data.matchStacks)
    ).apply(codecBuilder, PpIngredient::new));


    public static final Codec<List<PpIngredient>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<RegistryFriendlyByteBuf, PpIngredient> STREAM_CODEC = StreamCodec.of(
            (byteBuf, ppIngredient) -> {
                ItemStack.STREAM_CODEC.encode(byteBuf, ppIngredient.getItemStack());
            },
            (byteBuf) -> {
                ItemStack itemStack = ItemStack.STREAM_CODEC.decode(byteBuf);
                return PpIngredient.of(itemStack);
            }
    );

    protected PpIngredient(ItemStack stack) {
        this.matchStacks = List.of(stack.copy());
    }

    protected PpIngredient(List<ItemStack> matchStacks) {
        this.matchStacks = matchStacks.stream()
                .map(ItemStack::copy)
                .toList();
    }


    // Hashcode and equals methods
    @Override
    public int hashCode() {
        StringBuilder result = new StringBuilder();
        for (ItemStack stack : matchStacks) {
            result.append(PUtil.getNameOrVerbosePotionName(stack));
        }
        return result.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PpIngredient)) {
            return false;
        }
        PpIngredient other = (PpIngredient) obj;
        if (matchStacks.size() != other.matchStacks.size()) {
            return false;
        }
        for (int i = 0; i < matchStacks.size(); i++) {
            if (!toJson(matchStacks.get(i)).equals(toJson(other.matchStacks.get(i)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (ItemStack stack : matchStacks) {
            result.append(" ");
            result.append(PUtil.getNameOrVerbosePotionName(stack));
        }
        result.append(" ]");
        return result.toString();
    }

    public boolean contains(ItemStack stack) {
        for (ItemStack i : matchStacks) {
            if (toJson(i).equals(toJson(stack))) {
                return true;
            }
        }
        return false;
    }

    private static JsonElement toJson(ItemStack ingredient) {
        DataResult<JsonElement> result = ItemStack.OPTIONAL_CODEC.encodeStart(JsonOps.INSTANCE, ingredient);
        return result.result().orElse(JsonOps.INSTANCE.empty());
    }

    public ItemStack getItemStack() {
        assert matchStacks.size() == 1 : "Tried to get item stacks, but there are multiple.";
        return matchStacks.getFirst().copy();
    }

    public Ingredient asIngredient() {
        assert matchStacks.size() == 1 : "Tried to get ingredient, but there are multiple.";
        return Ingredient.of(matchStacks.getFirst().getItem());
    }

    public SlotDisplay display() {
        Ingredient ingredient = asIngredient();
        return ingredient.display();
    }

    public static PpIngredient of(ItemStack stack) {
        return new PpIngredient(stack);
    }
}

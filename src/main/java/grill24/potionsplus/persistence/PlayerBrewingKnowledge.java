package grill24.potionsplus.persistence;

import grill24.potionsplus.core.seededrecipe.PpIngredients;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerBrewingKnowledge {
    private final List<CompoundTag> serializableData = new ArrayList<>();

    private final transient Lazy<Set<PpIngredients>> uniqueIngredients = Lazy.of(this::getUniqueIngredients);

    public void addIngredient(ItemStack ingredient) {
        serializableData.add(ingredient.getTag());
        uniqueIngredients.get().add(new PpIngredients(ingredient));
    }

    public Set<PpIngredients> getUniqueIngredients() {
        Set<PpIngredients> uniqueIngredients = new HashSet<>();
        for (CompoundTag tag : serializableData) {
            ItemStack stack = ItemStack.of(tag);
            uniqueIngredients.add(new PpIngredients(stack));
        }
        return uniqueIngredients;
    }

    public boolean contains(ItemStack ingredient) {
        return uniqueIngredients.get().contains(new PpIngredients(ingredient));
    }

    public static void onAcquiredNewIngredientKnowledge(Level level, Player player, ItemStack ingredient) {
        if(level != null) {
            TranslatableComponent text = new TranslatableComponent("chat.potionsplus.acquired_ingredient_knowledge_" + level.getRandom().nextInt(1, 4), ingredient.getHoverName());
            player.displayClientMessage(text, true);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 1.0F, 1.0F);
        }
    }
}

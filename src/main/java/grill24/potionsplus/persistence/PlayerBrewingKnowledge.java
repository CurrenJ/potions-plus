package grill24.potionsplus.persistence;

import grill24.potionsplus.core.seededrecipe.PpIngredients;
import net.minecraft.core.BlockPos;
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
    private final List<ItemStack> serializableData = new ArrayList<>();
    private BlockPos lastAbyssalTroveUsedPos = BlockPos.ZERO;

    private final transient Lazy<Set<PpIngredients>> uniqueIngredients = Lazy.of(this::getUniqueIngredients);

    public PlayerBrewingKnowledge() {
    }

    public void addIngredient(ItemStack ingredient) {
        serializableData.add(ingredient);
        uniqueIngredients.get().add(new PpIngredients(ingredient));
    }

    public Set<PpIngredients> getUniqueIngredients() {
        Set<PpIngredients> uniqueIngredients = new HashSet<>();
        for (ItemStack stack : serializableData) {
            uniqueIngredients.add(new PpIngredients(stack));
        }
        return uniqueIngredients;
    }

    public boolean contains(ItemStack ingredient) {
        return uniqueIngredients.get().contains(new PpIngredients(ingredient));
    }

    public static void onAcquiredNewIngredientKnowledge(Level level, Player player, ItemStack ingredient) {
        if (level != null) {
            TranslatableComponent text = new TranslatableComponent("chat.potionsplus.acquired_ingredient_knowledge_" + level.getRandom().nextInt(1, 4), ingredient.getHoverName());
            player.displayClientMessage(text, true);
            level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void onAbyssalTroveInsert(BlockPos pos) {
        lastAbyssalTroveUsedPos = pos;
    }

    public BlockPos getLastAbyssalTroveUsedPos() {
        return lastAbyssalTroveUsedPos;
    }
}

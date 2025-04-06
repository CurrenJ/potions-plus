package grill24.potionsplus.event;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.item.FishSizeDataComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class PpFishCaughtEvent extends Event {
    private final ItemStack fish;
    private final Player player;

    public PpFishCaughtEvent(ItemStack item, Player player) {
        this.fish = item;
        this.player = player;
    }

    public float getSize() {
        return this.fish.getOrDefault(DataComponents.FISH_SIZE, new FishSizeDataComponent(0)).size();
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getFish() {
        return fish;
    }
}

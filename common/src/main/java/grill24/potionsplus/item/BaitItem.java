package grill24.potionsplus.item;

import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import static grill24.potionsplus.utility.Utility.ppId;

public class BaitItem extends Item {
    public final String descriptionKey;

    public BaitItem(Properties properties, String descriptionKey) {
        super(properties);

        this.descriptionKey = descriptionKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public static void onAnimateTooltip(final AnimatedItemTooltipEvent.Add event) {
        if (event.getItemStack().getItem() instanceof BaitItem baitItem) {
            event.getTooltipMessages().add(
                    AnimatedItemTooltipEvent.TooltipLines.of(ppId("bait_description"), 50, Component.translatable(baitItem.getDescriptionKey())));
        }
    }
}

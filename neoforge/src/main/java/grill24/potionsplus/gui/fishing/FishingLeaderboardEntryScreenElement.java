package grill24.potionsplus.gui.fishing;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.TextComponentScreenElement;
import grill24.potionsplus.gui.skill.ItemStackScreenElement;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.Utility;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FishingLeaderboardEntryScreenElement extends HorizontalListScreenElement<RenderableScreenElement> {
    public FishingLeaderboardEntryScreenElement(Screen screen, ItemStack stack, ResolvableProfile profile, Component text, Component textHeader) {
        super(screen, Settings.DEFAULT, YAlignment.CENTER, 2);

        this.setChildren(initializeElements(stack, profile, text, textHeader));
    }

    public Collection<RenderableScreenElement> initializeElements(ItemStack stack, ResolvableProfile profile, Component text, Component textHeader) {
        ItemStackScreenElement stackRenderer = new ItemStackScreenElement(this.screen, null, RenderableScreenElement.Settings.DEFAULT, stack);
        if (stack.has(DataComponents.FISH_SIZE)) {
            float size = stack.get(DataComponents.FISH_SIZE).getItemFrameSizeMultiplier();
            stackRenderer.setCurrentScale(size * 1.5F);
        }
        ItemStackScreenElement playerHead = new ItemStackScreenElement(this.screen, null, RenderableScreenElement.Settings.DEFAULT, Utility.getPlayerHead(profile));
        TextComponentScreenElement textElement = new TextComponentScreenElement(this.screen, Settings.DEFAULT, Color.WHITE, text);
        TextComponentScreenElement textHeaderElement = new TextComponentScreenElement(this.screen, Settings.DEFAULT, Color.WHITE, textHeader);

        Player localPlayer = this.screen.getMinecraft().player;
        if (!SavedData.instance.fishingLeaderboards.getFishingData().get(localPlayer.getUUID()).hasCaughtFish(stack.getItemHolder())) {
            stackRenderer.setOnlyShowSilhouette(true);
        }

        return List.of(
                textHeaderElement,
                stackRenderer,
                textElement,
                playerHead);
    }
}

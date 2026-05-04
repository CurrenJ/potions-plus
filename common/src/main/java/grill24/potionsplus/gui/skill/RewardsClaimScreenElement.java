package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.gui.VerticalListScreenElement;
import grill24.potionsplus.skill.PendingRewardsData;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class RewardsClaimScreenElement extends VerticalListScreenElement<RenderableScreenElement> {
    public RewardsClaimScreenElement(Screen screen, RenderableScreenElement... elements) {
        super(screen, Settings.DEFAULT, XAlignment.CENTER, elements);
    }

    public void updateRewards() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        SkillsData skillsData = SkillsData.getPlayerData(player);
        PendingRewardsData pendingRewardsData = skillsData.pendingRewards();

        List<RenderableScreenElement> children = pendingRewardsData.getValidRewards().entrySet().stream()
                .map(this::createRewardItemElement).toList();

        List<RenderableScreenElement> wrappedElements = wrapElements(children, (int) Math.ceil(Math.sqrt(children.size())));

        this.setChildren(wrappedElements);
    }

    private RenderableScreenElement createRewardItemElement(Map.Entry<ResourceKey<ConfiguredGrantableReward<?, ?>>, List<List<ItemStack>>> rewards) {
        ItemStack stack = rewards.getValue().stream()
                .flatMap(List::stream)
                .findFirst()
                .orElse(ItemStack.EMPTY);

        PendingItemRewardElement pendingRewardDisplay = new PendingItemRewardElement(
                screen,
                null,
                Settings.DEFAULT,
                Anchor.CENTER,
                new HoverItemStackScreenElement(this.screen, null, Settings.DEFAULT, stack, 1.0f, 1.2f),
                rewards.getValue().size(),
                rewards.getKey()
        );

        // Add click listener to claim the reward
        pendingRewardDisplay.addClickListener((x, y, mouseButton, el) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            if(el instanceof PendingItemRewardElement pendingItemRewardElement) {
                pendingItemRewardElement.tryClaim();
            }
        });

        return pendingRewardDisplay;
    }

    private List<RenderableScreenElement> wrapElements(List<RenderableScreenElement> elements, int maxPerRow) {
        if (elements.isEmpty()) {
            return List.of();
        }

        int totalRows = (elements.size() + maxPerRow - 1) / maxPerRow;
        List<HorizontalListScreenElement<RenderableScreenElement>> rows = new java.util.ArrayList<>(totalRows);

        for (int i = 0; i < totalRows; i++) {
            int start = i * maxPerRow;
            int end = Math.min(start + maxPerRow, elements.size());
            rows.add(new HorizontalListScreenElement<>(this.screen, Settings.DEFAULT, YAlignment.CENTER,
                    elements.subList(start, end).toArray(new RenderableScreenElement[0])));
        }

        return rows.stream().map(h -> (RenderableScreenElement) h).toList();
    }
}

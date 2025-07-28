package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.network.ServerboundTryClaimSkillReward;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class PendingItemRewardElement extends SplashTextDivScreenElement {
    private final ResourceKey<ConfiguredGrantableReward<?, ?>> rewardKey;
    private int countSplash;

    public PendingItemRewardElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Anchor childAlignment, RenderableScreenElement child, int count, @Nonnull ResourceKey<ConfiguredGrantableReward<?, ?>> rewardKey) {
        super(screen, parent, settings, childAlignment, child, Component.literal(String.valueOf(count)));
        this.rewardKey = rewardKey;
        this.countSplash = count;
    }

    public void tryClaim() {
        if (rewardKey == null) {
            return;
        }

        PacketDistributor.sendToServer(new ServerboundTryClaimSkillReward(rewardKey));

        // Decrease the count and update the display. This is "faux", because the pending reward will only be fully removed once the player consumes the item.
        // Why? Because the player might lose the item or misplace it before they can consume it.
        // This is just for visual feedback.
        this.countSplash--;
        this.component = Component.literal(String.valueOf(this.countSplash));

        if (this.countSplash <= 0) {
            this.hide(false);
        }
    }
}

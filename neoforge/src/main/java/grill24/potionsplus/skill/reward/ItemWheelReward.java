package grill24.potionsplus.skill.reward;

import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundDisplayWheelAnimationPacket;
import grill24.potionsplus.utility.DelayedServerEvents;
import grill24.potionsplus.utility.InvUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class ItemWheelReward extends GrantableReward<ItemWheelRewardConfiguration> {
    public ItemWheelReward() {
        super(ItemWheelRewardConfiguration.CODEC);
    }

    @Override
    public Optional<Component> getDescription(ItemWheelRewardConfiguration config) {
        // Condense the description to a single line
        List<List<Component>> multiLineRichDescription = getMultiLineRichDescription(config, false);
        MutableComponent description = Component.empty();
        for (List<Component> line : multiLineRichDescription) {
            for (Component component : line) {
                description.append(component.plainCopy());
            }
        }

        return Optional.of(description);
    }

    @Override
    public List<List<Component>> getMultiLineRichDescription(ItemWheelRewardConfiguration config) {
        return getMultiLineRichDescription(config, true);
    }

    private List<List<Component>> getMultiLineRichDescription(ItemWheelRewardConfiguration config, boolean verbose) {
        if (!config.translationKey.isEmpty()) {
            return List.of(List.of(Component.translatable(Translations.DESCRIPTION_POTIONSPLUS_SPIN_TO_WIN, ""), Component.translatable(config.translationKey).withStyle(ChatFormatting.GREEN)));
        } else if (config.possibleRewards != null && !config.possibleRewards.isEmpty() && verbose) {
            MutableComponent component = Component.empty();
            for (int i = 0; i < config.possibleRewards.size(); i++) {
                component.append(config.possibleRewards.get(i).getHoverName());
                if (i < config.possibleRewards.size() - 1) {
                    component.append(", ");
                }
                if (i == config.possibleRewards.size() - 2) {
                    component.append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_OR).withStyle(ChatFormatting.GREEN).append(" "));
                }
            }
            component.withStyle(ChatFormatting.GREEN);
            return List.of(List.of(Component.translatable(Translations.DESCRIPTION_POTIONSPLUS_SPIN_TO_WIN, ""), component));
        }
        return List.of(List.of(Component.translatable(Translations.DESCRIPTION_POTIONSPLUS_REWARD_WHEEL)));
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, ItemWheelRewardConfiguration config, ServerPlayer player) {
        List<ItemStack> possibleRewards = config.possibleRewards != null ? new ArrayList<>(config.possibleRewards) : new ArrayList<>();
        int winnerIndex;
        if (config.lootTableResourceKey != null && config.numToSample > 0) {
            LootTable lootTable = player.getServer().reloadableRegistries().getLootTable(config.lootTableResourceKey);
            List<ItemStack> samples = new ArrayList<>();
            for (int i = 0; i < config.numToSample; i++) {
                List<ItemStack> sample = lootTable.getRandomItems(new LootParams.Builder((ServerLevel)player.level())
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(lootTable.getParamSet()));
                if (!sample.isEmpty()) {
                    samples.add(sample.getFirst());
                }
            }

            possibleRewards.addAll(samples);
        }

        winnerIndex = player.getRandom().nextInt(possibleRewards.size());
        // Play animation on client
        PacketDistributor.sendToPlayer(player, new ClientboundDisplayWheelAnimationPacket(possibleRewards, winnerIndex));
        // Give item at right time during animation (server)
        DelayedServerEvents.queueDelayedEvent(() -> InvUtil.giveOrDropItem(player, possibleRewards.get(winnerIndex).copy()), 190);
    }

    public static class ItemWheelRewardBuilder implements ConfiguredGrantableRewards.IRewardBuilder {
        private String translationKey;

        private final List<ItemStack> itemStacks;
        private final ResourceKey<LootTable> lootTable;
        private final int numToSample;

        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        public ItemWheelRewardBuilder(String name, ItemStack... itemStacks) {
            translationKey = "";

            this.itemStacks = Arrays.stream(itemStacks).toList();
            this.lootTable = null;
            this.numToSample = 0;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ItemWheelRewardBuilder(String name, ResourceKey<LootTable> lootTable, int numToSample) {
            translationKey = "";

            this.itemStacks = new ArrayList<>();
            this.lootTable = lootTable;
            this.numToSample = numToSample;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {return key; }

        public ItemWheelRewardBuilder translation(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.WHEEL.value(),
                    new ItemWheelRewardConfiguration(translationKey, itemStacks, Optional.ofNullable(lootTable), numToSample)
            ));

        }
    }
}

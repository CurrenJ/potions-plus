package grill24.potionsplus.skill.reward;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.network.ClientboundDisplayAlert;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.skill.UnknownPotionIngredientRewardConfiguration;
import grill24.potionsplus.utility.InvUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UnknownPotionIngredientReward extends GrantableReward<UnknownPotionIngredientRewardConfiguration> {
    public UnknownPotionIngredientReward() {
        super(UnknownPotionIngredientRewardConfiguration.CODEC);
    }

    @Override
    public Optional<Component> getDescription(UnknownPotionIngredientRewardConfiguration config) {
        return Optional.of(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_REWARD_UNKNOWN_POTION_INGREDIENT_DESCRIPTION));
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, UnknownPotionIngredientRewardConfiguration config, ServerPlayer player) {
        BlockPos pos = SavedData.instance.getData(player).getPairedAbyssalTrovePos();
        player.level().getBlockEntity(pos, Blocks.ABYSSAL_TROVE_BLOCK_ENTITY.value()).ifPresentOrElse(
                blockEntity -> {
                    if (blockEntity instanceof AbyssalTroveBlockEntity abyssalTroveBlockEntity) {
                        getUnknownPotionsIngredients(config, player, abyssalTroveBlockEntity.getStoredIngredients());
                    }
                },
                () -> {
                    getUnknownPotionsIngredients(config, player, Set.of());
                }
        );

        PacketDistributor.sendToPlayer(player, new ClientboundDisplayAlert(Component.translatable(Translations.ALERT_POTIONSPLUS_REWARD_UNKNOWN_POTION_INGREDIENT)));
    }

    private static void getUnknownPotionsIngredients(UnknownPotionIngredientRewardConfiguration config, ServerPlayer player, Set<PpIngredient> knownIngredients) {
        Set<PpIngredient> allIngredients = new HashSet<>(Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.getUniqueIngredients());
        allIngredients.removeAll(knownIngredients);

        // Pick random
        for (int i = 0; i < config.count() && !allIngredients.isEmpty(); i++) {
            PpIngredient ppIngredient = allIngredients.stream().skip(player.getRandom().nextInt(allIngredients.size())).findFirst().orElseThrow();
            InvUtil.giveOrDropItem(player, ppIngredient.getItemStack().copy());
            allIngredients.remove(ppIngredient);
        }
    }
}
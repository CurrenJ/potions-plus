package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record EdibleChoiceRewardOption(
        ResourceKey<ConfiguredGrantableReward<?, ?>> linkedChoiceParent,
        ResourceKey<ConfiguredGrantableReward<?, ?>> linkedOption,
        ItemStack activationFood) {
    public static final Codec<EdibleChoiceRewardOption> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).fieldOf("linkedChoiceParent").forGetter(instance -> instance.linkedChoiceParent),
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).fieldOf("linkedOption").forGetter(instance -> instance.linkedOption),
            ItemStack.CODEC.fieldOf("activationFood").forGetter(instance -> instance.activationFood)
    ).apply(codecBuilder, EdibleChoiceRewardOption::new));

    public ItemStack createItem(ServerPlayer player, ResourceLocation flag) {
        HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = player.level().registryAccess().lookupOrThrow(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);
        Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optionalLinkedChoiceParent = lookup.get(linkedChoiceParent);
        Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optionalLinkedOption = lookup.get(linkedOption);

        // Check that the ConfiguredGrantableReward for the linked choice parent and option exist
        if (optionalLinkedChoiceParent.isPresent() && optionalLinkedOption.isPresent() &&
                optionalLinkedChoiceParent.get().value().reward() instanceof EdibleChoiceReward) {
            ItemStack item = this.activationFood.copy();
            item.set(DataComponents.CHOICE_ITEM, new EdibleRewardGranterDataComponent(linkedChoiceParent, linkedOption, flag));
            item.set(DataComponents.OWNER, OwnerDataComponent.fromPlayer(player));
            item.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            return item;
        } else {
            PotionsPlus.LOGGER.error("No parent choice reward found for key: " + linkedChoiceParent);
        }

        return ItemStack.EMPTY;
    }
}

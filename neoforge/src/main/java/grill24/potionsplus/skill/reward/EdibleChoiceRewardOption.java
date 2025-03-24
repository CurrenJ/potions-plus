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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class EdibleChoiceRewardOption {
    public static final Codec<EdibleChoiceRewardOption> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).fieldOf("linkedChoiceParent").forGetter(instance -> instance.linkedChoiceParent),
            ConfiguredGrantableReward.HOLDER_CODECS.holderCodec().fieldOf("linkedOption").forGetter(instance -> instance.linkedOption),
            ItemStack.CODEC.fieldOf("activationFood").forGetter(instance -> instance.activationFood)
    ).apply(codecBuilder, EdibleChoiceRewardOption::new));

    public ResourceKey<ConfiguredGrantableReward<?, ?>> linkedChoiceParent;
    public final Holder<ConfiguredGrantableReward<?, ?>> linkedOption;
    public final ItemStack activationFood;

    private EdibleChoiceRewardOption(ResourceKey<ConfiguredGrantableReward<?, ?>> linkedChoiceParent, Holder<ConfiguredGrantableReward<?, ?>> linkedOption, ItemStack activationFood) {
        this.linkedChoiceParent = linkedChoiceParent;
        this.linkedOption = linkedOption;
        this.activationFood = activationFood.copy();
    }

    public EdibleChoiceRewardOption(HolderGetter<ConfiguredGrantableReward<?, ?>> lookup, ResourceKey<ConfiguredGrantableReward<?, ?>> parentChoiceKey, ResourceKey<ConfiguredGrantableReward<?, ?>> linkedOption, ItemStack activationFood) {
        this.linkedChoiceParent = parentChoiceKey;
        this.linkedOption = lookup.getOrThrow(linkedOption);
        this.activationFood = activationFood.copy();
    }

    public void giveItem(ServerPlayer player, ResourceLocation flag) {
        HolderGetter<ConfiguredGrantableReward<?, ?>> lookup = player.level().registryAccess().lookupOrThrow(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD);
        Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optional = lookup.get(linkedChoiceParent);

        if (optional.isPresent() && optional.get().value().reward() instanceof EdibleChoiceReward choiceReward) {
            ItemStack item = this.activationFood.copy();
            item.set(DataComponents.CHOICE_ITEM, new EdibleRewardGranterDataComponent(optional.get(), linkedOption, flag));
            item.set(DataComponents.OWNER, OwnerDataComponent.fromPlayer(player));
            item.set(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            if (player.addItem(item)) {
                player.level()
                        .playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                SoundEvents.ITEM_PICKUP,
                                SoundSource.PLAYERS,
                                0.2F,
                                ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                        );
            } else {
                player.drop(item, false);
            }
        } else {
            PotionsPlus.LOGGER.error("No parent choice reward found for key: " + linkedChoiceParent);
        }
    }
}

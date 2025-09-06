package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Translations;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemWheelRewardConfiguration extends GrantableRewardConfiguration {
    public static final Codec<ItemWheelRewardConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("translationKey", Translations.DESCRIPTION_POTIONSPLUS_REWARD_WHEEL).forGetter(config -> config.translationKey),
                    Codec.list(ItemStack.CODEC).optionalFieldOf("possibleRewards", new ArrayList<>()).forGetter(config -> config.possibleRewards),
                    ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("lootTable").forGetter(config -> java.util.Optional.ofNullable(config.lootTableResourceKey)),
                    Codec.INT.fieldOf("numToSample").forGetter(config -> config.numToSample)
            ).apply(instance, ItemWheelRewardConfiguration::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemWheelRewardConfiguration> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            instance -> instance.translationKey,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            instance -> instance.possibleRewards,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.LOOT_TABLE)),
            instance -> Optional.ofNullable(instance.lootTableResourceKey),
            ByteBufCodecs.INT,
            instance -> instance.numToSample,
            ItemWheelRewardConfiguration::new
    );

    public final String translationKey;

    public final List<ItemStack> possibleRewards;
    public ResourceKey<LootTable> lootTableResourceKey;
    public int numToSample;

    public ItemWheelRewardConfiguration(String translationKey, List<ItemStack> possibleRewards, Optional<ResourceKey<LootTable>> lootTable, int numToSample) {
        this.translationKey = translationKey;

        this.lootTableResourceKey = lootTable.orElse(null);
        this.numToSample = numToSample;
        this.possibleRewards = possibleRewards;
    }
}

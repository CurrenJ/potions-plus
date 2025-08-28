package grill24.potionsplus.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.LootItemConditions;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Optional;
import java.util.Set;

/**
 * A LootItemCondition that checks the {@linkplain LootContextParams#TOOL tool} against an {@link ItemPredicate}.
 */
public record HasPlayerAbilityCondition(ResourceKey<ConfiguredPlayerAbility<?, ?>> key) implements LootItemCondition {
    public static final MapCodec<HasPlayerAbilityCondition> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).fieldOf("configuredPlayerAbility").forGetter(HasPlayerAbilityCondition::key)).apply(codecBuilder, HasPlayerAbilityCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.HAS_PLAYER_ABILITY.value();
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.THIS_ENTITY);
    }

    public boolean test(LootContext context) {
        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Player)) {
            entity = context.getOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER);
        }
        if (entity instanceof Player player) {
            SkillsData skillsData = player.getData(DataAttachments.SKILL_PLAYER_DATA);
            Optional<AbilityInstanceSerializable<?, ?>> abilityInstance = skillsData.getAbilityInstance(entity.registryAccess(), key.location());
            return abilityInstance.isPresent() && abilityInstance.get().data().isEnabled();
        }

        return false;
    }

    public static Builder hasPlayerAbility(ResourceKey<ConfiguredPlayerAbility<?, ?>> key) {
        return () -> new HasPlayerAbilityCondition(key);
    }
}

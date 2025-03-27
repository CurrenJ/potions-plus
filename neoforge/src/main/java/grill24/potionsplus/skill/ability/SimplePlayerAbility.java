package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.*;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.SimpleAbilityInstanceData;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class SimplePlayerAbility extends PlayerAbility<PlayerAbilityConfiguration> implements BiPredicate<PlayerAbilityConfiguration, ItemStack> {
    public SimplePlayerAbility() {
        super(PlayerAbilityConfiguration.CODEC, Set.of(AbilityInstanceTypes.SIMPLE_TOGGLEABLE.value()));
    }

    public SimplePlayerAbility(Set<AbilityInstanceType<?>> instanceTypes) {
        super(PlayerAbilityConfiguration.CODEC, instanceTypes);
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.SIMPLE_TOGGLEABLE.value(),
                new SimpleAbilityInstanceData(ability, true));
    }

    @Override
    public void onEnable(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> instance) {}

    @Override
    public void onDisable(ServerPlayer player, PlayerAbilityConfiguration config) {}

    @Override
    public void onAbilityGranted(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> instance) {}

    @Override
    public void onAbilityRevoked(ServerPlayer player, PlayerAbilityConfiguration config) {}

    @Override
    public boolean test(PlayerAbilityConfiguration playerAbilityConfiguration, ItemStack stack) {
        return playerAbilityConfiguration.getData().itemPredicate().test(stack);
    }

    public Component getRichEnablementTooltipComponent(boolean isEnabled) {
        return isEnabled ? Component.translatable(Translations.GENERIC_POTIONSPLUS_ENABLED_RICH) : Component.translatable(Translations.GENERIC_POTIONSPLUS_DISABLED_RICH);
    }

    abstract static class AbstractBuilder<AC extends PlayerAbilityConfiguration, A extends PlayerAbility<AC>, B extends AbstractBuilder<AC, A, B>> implements ConfiguredPlayerAbilities.IAbilityBuilder<B> {
        protected final ResourceKey<ConfiguredPlayerAbility<?, ?>> key;
        protected String translationKey;
        protected String longTranslationKey;
        protected ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey;
        protected ItemPredicate itemPredicate;
        protected boolean enabledByDefault;
        protected A ability;

        public AbstractBuilder(String key) {
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ppId(key));
            this.translationKey = "";
            this.longTranslationKey = "";
            this.itemPredicate = ItemPredicate.Builder.item().build();
            this.enabledByDefault = true;
        }

        public B translationKey(String translationKey) {
            this.translationKey = translationKey;
            return self();
        }

        public B longTranslationKey(String longTranslationKey) {
            this.longTranslationKey = longTranslationKey;
            return self();
        }

        public B parentSkill(ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey) {
            this.parentSkillKey = parentSkillKey;
            return self();
        }

        public B itemPredicate(ItemPredicate itemPredicate) {
            this.itemPredicate = itemPredicate;
            return self();
        }

        public B enabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
            return self();
        }

        public B ability(A ability) {
            this.ability = ability;
            return self();
        }

        public B ability(Supplier<A> ability) {
            this.ability = ability.get();
            return self();
        }

        public boolean validate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (parentSkillKey == null) {
                throw new IllegalStateException("Parent skill key must be set. | " + key);
            }

            if (ability == null) {
                throw new IllegalStateException("Ability must be set. | " + key);
            }

            if (this.translationKey == null) {
                this.translationKey = "";
            }

            if (this.itemPredicate == null) {
                this.itemPredicate = ItemPredicate.Builder.item().build();
            }

            return true;
        }

        protected abstract AC buildConfig(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context);

        protected void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            context.register(key, new ConfiguredPlayerAbility<>(ability, buildConfig(context)));
        }

        @Override
        public final void tryGenerate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (validate(context)) {
                generate(context);
            }
        }

        protected PlayerAbilityConfiguration.PlayerAbilityConfigurationData buildBaseConfigurationData(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);
            return new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, longTranslationKey, enabledByDefault, skillLookup.getOrThrow(parentSkillKey), itemPredicate);
        }

        @Override
        public ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey() {
            return key;
        }
    }

    public static class Builder extends AbstractBuilder<PlayerAbilityConfiguration, SimplePlayerAbility, Builder> {
        public Builder(String key) {
            super(key);
            this.ability = PlayerAbilities.SIMPLE.value();
        }

        @Override
        protected PlayerAbilityConfiguration buildConfig(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            return new PlayerAbilityConfiguration(buildBaseConfigurationData(context));
        }

        @Override
        public Builder self() {
            return this;
        }
    }
}

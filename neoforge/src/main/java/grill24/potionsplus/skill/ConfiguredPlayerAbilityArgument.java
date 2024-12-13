package grill24.potionsplus.skill;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class ConfiguredPlayerAbilityArgument implements ArgumentType<ConfiguredPlayerAbilityArgument.AbilityInfo> {
    public record AbilityInfo(Holder<ConfiguredPlayerAbility<?, ?>> holder) {}

    private final CommandBuildContext context;

    public ConfiguredPlayerAbilityArgument(CommandBuildContext context) {
        this.context = context;
    }

    public static Holder<ConfiguredPlayerAbility<?, ?>> getHolder(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, AbilityInfo.class).holder;
    }

    @Override
    public AbilityInfo parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);
        return new AbilityInfo(this.context.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).get(ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, location)).orElseThrow());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(
                this.context.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).listElementIds()
                .map(ResourceKey::location)
                .toList(), builder);
    }}

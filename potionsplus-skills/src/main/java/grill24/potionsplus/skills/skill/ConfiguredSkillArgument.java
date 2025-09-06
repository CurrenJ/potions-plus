package grill24.potionsplus.skill;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import grill24.potionsplus.core.PotionsPlusRegistries;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static grill24.potionsplus.utility.Utility.ppId;

public class ConfiguredSkillArgument implements ArgumentType<ConfiguredSkill<?, ?>> {
    private static final Collection<String> EXAMPLES = List.of(ppId("mining").toString(), ppId("walking").toString());
    private CommandBuildContext context;

    public ConfiguredSkillArgument(CommandBuildContext context) {
        this.context = context;
    }

    public static ConfiguredSkill<?, ?> getSkill(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, ConfiguredSkill.class);
    }

    public ConfiguredSkill<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        return this.context.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).get(ResourceKey.create(PotionsPlusRegistries.CONFIGURED_SKILL, ResourceLocation.read(reader))).orElseThrow().value();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(
                this.context.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).listElementIds()
                        .map(ResourceKey::location)
                        .toList(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

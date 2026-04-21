package grill24.potionsplus.render.animation.keyframe;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SpatialAnimationDataArgument implements ArgumentType<SpatialAnimationDataArgument.Info> {
    public record Info(ResourceLocation id, SpatialAnimationData spatialAnimationData) {
    }

    private CommandBuildContext context;

    public SpatialAnimationDataArgument(CommandBuildContext context) {
        this.context = context;
    }

    public static SpatialAnimationData get(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Info.class).spatialAnimationData;
    }

    public static ResourceLocation getId(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Info.class).id;
    }

    // Server execution
    public Info parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        SpatialAnimationData spatialAnimationData = SpatialAnimations.get(id);
        return new Info(id, spatialAnimationData);
    }

    @Override
    public Collection<String> getExamples() {
        return SpatialAnimations.getIds().stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }

    // Client execution
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(SpatialAnimations.getIds(), builder);
    }
}

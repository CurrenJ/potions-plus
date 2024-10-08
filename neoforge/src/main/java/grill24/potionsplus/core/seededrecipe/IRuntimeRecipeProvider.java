package grill24.potionsplus.core.seededrecipe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@FunctionalInterface
public interface IRuntimeRecipeProvider {
    List<RecipeHolder<?>> getRuntimeRecipesToInject(MinecraftServer server);
}

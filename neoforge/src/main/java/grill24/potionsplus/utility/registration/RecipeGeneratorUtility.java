package grill24.potionsplus.utility.registration;

import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class RecipeGeneratorUtility {
    public abstract static class AbstractRecipeGenerator<T> implements IRecipeGenerator<T> {
        protected final Supplier<Holder<T>> holder;
        @Nullable protected final ResourceLocation id;

        public AbstractRecipeGenerator(Supplier<Holder<T>> holder, @Nullable ResourceLocation id) {
            this.holder = holder;
            this.id = id;
        }

        public AbstractRecipeGenerator(Supplier<Holder<T>> holder) {
            this(holder, null);
        }

        @Override
        public Holder<? extends T> getHolder() {
            return this.holder.get();
        }
    }

    public static class RecipeGenerator<T> extends AbstractRecipeGenerator<T> {
        private final Function<Holder<? extends T>, RecipeBuilder> recipeBuilder;

        public RecipeGenerator(Supplier<Holder<T>> holder, Function<Holder<? extends T>, RecipeBuilder> recipeBuilder) {
            super(holder);
            this.recipeBuilder = recipeBuilder;
        }

        public RecipeGenerator(Supplier<Holder<T>> holder, ResourceLocation id, Function<Holder<? extends T>, RecipeBuilder> recipeBuilder) {
            super(holder, id);
            this.recipeBuilder = recipeBuilder;
        }

            @Override
        public void generate(RecipeProvider provider, RecipeOutput output) {
            ResourceLocation id = this.id == null ? getHolder().getKey().location() : this.id;
            recipeBuilder.apply(getHolder()).save(output, id);
        }
    }


}

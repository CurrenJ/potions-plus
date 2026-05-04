package grill24.potionsplus.core;

import grill24.potionsplus.item.consumeeffect.EdibleChoiceItemConsumeEffect;
import grill24.potionsplus.item.consumeeffect.GeneticCropItemConsumeEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ConsumeEffects {
    public static Holder<ConsumeEffect.Type<?>> EDIBLE_CHOICE_ITEM;
    public static Holder<ConsumeEffect.Type<?>> GENETIC_CROP_ITEM;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<ConsumeEffect.Type<?>>, Holder<ConsumeEffect.Type<?>>> register) {
        EDIBLE_CHOICE_ITEM = register.apply("edible_choice_item",
                () -> new ConsumeEffect.Type<>(EdibleChoiceItemConsumeEffect.MAP_CODEC, EdibleChoiceItemConsumeEffect.STREAM_CODEC));
        GENETIC_CROP_ITEM = register.apply("genetic_crop_item",
                () -> new ConsumeEffect.Type<>(GeneticCropItemConsumeEffect.MAP_CODEC, GeneticCropItemConsumeEffect.STREAM_CODEC));
    }
}

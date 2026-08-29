package grill24.potionsplus.core;

import net.minecraft.core.Holder;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ConsumeEffects {
    public static void init(BiFunction<String, Supplier<ConsumeEffect.Type<?>>, Holder<ConsumeEffect.Type<?>>> register) {
    }
}

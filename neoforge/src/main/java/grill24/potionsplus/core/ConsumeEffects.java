package grill24.potionsplus.core;

import grill24.potionsplus.item.consumeeffect.EdibleChoiceItemConsumeEffect;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConsumeEffects {
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<EdibleChoiceItemConsumeEffect>> EDIBLE_CHOICE_ITEM = CONSUME_EFFECTS.register("edible_choice_item",
            () -> new ConsumeEffect.Type<>(EdibleChoiceItemConsumeEffect.MAP_CODEC, EdibleChoiceItemConsumeEffect.STREAM_CODEC));
}

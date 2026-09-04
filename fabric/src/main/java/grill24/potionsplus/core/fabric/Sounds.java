package grill24.potionsplus.core.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import static grill24.potionsplus.utility.Utility.ppId;

public class Sounds {
    public static final Holder<SoundEvent> ABYSSAL_TROVE_DEPOSIT = register("abyssal_trove_deposit");
    public static final Holder<SoundEvent> HERBALISTS_LECTERN_APPEAR = register("herbalists_lectern_appear");
    public static final Holder<SoundEvent> HERBALISTS_LECTERN_DISAPPEAR = register("herbalists_lectern_disappear");
    public static final Holder<SoundEvent> PING_0 = register("ping_0");
    public static final Holder<SoundEvent> PING_1 = register("ping_1");
    public static final Holder<SoundEvent> PING_2 = register("ping_2");
    public static final Holder<SoundEvent> PING_3 = register("ping_3");
    public static final Holder<SoundEvent> GIANT_STEPS = register("giant_steps");
    public static final Holder<SoundEvent> RECIPE_UNLOCKED = register("recipe_unlocked");
    public static final Holder<SoundEvent> MUTED_PLUCKS_0 = register("muted_plucks_0");
    public static final Holder<SoundEvent> MUTED_PLUCKS_1 = register("muted_plucks_1");
    public static final Holder<SoundEvent> SANGUINE_ALTAR_CONVERSION = register("sanguine_altar_conversion");
    public static final Holder<SoundEvent> LIGHTNING_BOLT_ABILITY = register("lightning_bolt_ability");
    public static final Holder<SoundEvent> HEAVY_IMPACT = register("heavy_impact");

    public static void init() {
        // No-op: forces class loading so the static initializers run.
    }

    private static Holder<SoundEvent> register(String name) {
        return FabricRegistration.register(BuiltInRegistries.SOUND_EVENT, name, () -> SoundEvent.createVariableRangeEvent(ppId(name)));
    }

    static {
        grill24.potionsplus.core.Sounds.ABYSSAL_TROVE_DEPOSIT = ABYSSAL_TROVE_DEPOSIT;
        grill24.potionsplus.core.Sounds.HERBALISTS_LECTERN_APPEAR = HERBALISTS_LECTERN_APPEAR;
        grill24.potionsplus.core.Sounds.HERBALISTS_LECTERN_DISAPPEAR = HERBALISTS_LECTERN_DISAPPEAR;
        grill24.potionsplus.core.Sounds.PING_0 = PING_0;
        grill24.potionsplus.core.Sounds.PING_1 = PING_1;
        grill24.potionsplus.core.Sounds.PING_2 = PING_2;
        grill24.potionsplus.core.Sounds.PING_3 = PING_3;
        grill24.potionsplus.core.Sounds.GIANT_STEPS = GIANT_STEPS;
        grill24.potionsplus.core.Sounds.RECIPE_UNLOCKED = RECIPE_UNLOCKED;
        grill24.potionsplus.core.Sounds.MUTED_PLUCKS_0 = MUTED_PLUCKS_0;
        grill24.potionsplus.core.Sounds.MUTED_PLUCKS_1 = MUTED_PLUCKS_1;
        grill24.potionsplus.core.Sounds.SANGUINE_ALTAR_CONVERSION = SANGUINE_ALTAR_CONVERSION;
        grill24.potionsplus.core.Sounds.LIGHTNING_BOLT_ABILITY = LIGHTNING_BOLT_ABILITY;
        grill24.potionsplus.core.Sounds.HEAVY_IMPACT = HEAVY_IMPACT;
    }
}

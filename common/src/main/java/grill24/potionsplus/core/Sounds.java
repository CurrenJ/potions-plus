package grill24.potionsplus.core;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

/**
 * Loader-agnostic stub for the sound event statics. The neoforge {@code DeferredRegister} lives in
 * {@code core.neoforge.NeoSounds} (alongside the datagen {@code SoundDefinitionsProvider}), which
 * populates these fields; Fabric/Forge register their own {@code SoundEvent}s into the same statics.
 * See docs/multi-loader-expansion.md Phase 4.
 */
public class Sounds {
    public static Holder<SoundEvent> ABYSSAL_TROVE_DEPOSIT;
    public static Holder<SoundEvent> HERBALISTS_LECTERN_APPEAR;
    public static Holder<SoundEvent> HERBALISTS_LECTERN_DISAPPEAR;
    public static Holder<SoundEvent> PING_0;
    public static Holder<SoundEvent> PING_1;
    public static Holder<SoundEvent> PING_2;
    public static Holder<SoundEvent> PING_3;
    public static Holder<SoundEvent> GIANT_STEPS;
    public static Holder<SoundEvent> RECIPE_UNLOCKED;
    public static Holder<SoundEvent> MUTED_PLUCKS_0;
    public static Holder<SoundEvent> MUTED_PLUCKS_1;
    public static Holder<SoundEvent> SANGUINE_ALTAR_CONVERSION;
    public static Holder<SoundEvent> LIGHTNING_BOLT_ABILITY;
    public static Holder<SoundEvent> HEAVY_IMPACT;
}

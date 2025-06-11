package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

public class Sounds extends SoundDefinitionsProvider {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ModInfo.MOD_ID);
    public static Holder<SoundEvent> ABYSSAL_TROVE_DEPOSIT = SOUNDS.register("abyssal_trove_deposit", () -> SoundEvent.createVariableRangeEvent(ppId("abyssal_trove_deposit")));
    public static Holder<SoundEvent> HERBALISTS_LECTERN_APPEAR = SOUNDS.register("herbalists_lectern_appear", () -> SoundEvent.createVariableRangeEvent(ppId("herbalists_lectern_appear")));
    public static Holder<SoundEvent> HERBALISTS_LECTERN_DISAPPEAR = SOUNDS.register("herbalists_lectern_disappear", () -> SoundEvent.createVariableRangeEvent(ppId("herbalists_lectern_disappear")));
    public static Holder<SoundEvent> PING_0 = SOUNDS.register("ping_0", () -> SoundEvent.createVariableRangeEvent(ppId("ping_0")));
    public static Holder<SoundEvent> PING_1 = SOUNDS.register("ping_1", () -> SoundEvent.createVariableRangeEvent(ppId("ping_1")));
    public static Holder<SoundEvent> PING_2 = SOUNDS.register("ping_2", () -> SoundEvent.createVariableRangeEvent(ppId("ping_2")));
    public static Holder<SoundEvent> PING_3 = SOUNDS.register("ping_3", () -> SoundEvent.createVariableRangeEvent(ppId("ping_3")));
    public static Holder<SoundEvent> GIANT_STEPS = SOUNDS.register("giant_steps", () -> SoundEvent.createVariableRangeEvent(ppId("giant_steps")));
    public static Holder<SoundEvent> RECIPE_UNLOCKED = SOUNDS.register("recipe_unlocked", () -> SoundEvent.createVariableRangeEvent(ppId("recipe_unlocked")));
    public static Holder<SoundEvent> MUTED_PLUCKS_0 = SOUNDS.register("muted_plucks_0", () -> SoundEvent.createVariableRangeEvent(ppId("muted_plucks_0")));
    public static Holder<SoundEvent> MUTED_PLUCKS_1 = SOUNDS.register("muted_plucks_1", () -> SoundEvent.createVariableRangeEvent(ppId("muted_plucks_1")));
    public static Holder<SoundEvent> SANGUINE_ALTAR_CONVERSION = SOUNDS.register("sanguine_altar_conversion", () -> SoundEvent.createVariableRangeEvent(ppId("sanguine_altar_conversion")));
    public static Holder<SoundEvent> LIGHTNING_BOLT_ABILITY = SOUNDS.register("lightning_bolt_ability", () -> SoundEvent.createVariableRangeEvent(ppId("lightning_bolt_ability")));
    public static Holder<SoundEvent> HEAVY_IMPACT = SOUNDS.register("heavy_impact", () -> SoundEvent.createVariableRangeEvent(ppId("heavy_impact")));

    /**
     * Creates a new instance of this data provider.
     *
     * @param output The {@linkplain PackOutput} instance provided by the data generator.
     * @param modId  The mod ID of the current mod.
     */
    public Sounds(PackOutput output, String modId) {
        super(output, modId);
    }


    @Override
    public void registerSounds() {
        this.add(ABYSSAL_TROVE_DEPOSIT.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(ABYSSAL_TROVE_DEPOSIT.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(ABYSSAL_TROVE_DEPOSIT.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/abyssal_trove_long")).weight(1),
                        sound(ppId("block/abyssal_trove_medium")).weight(3),
                        sound(ppId("block/abyssal_trove_short")).weight(5),
                        sound(ppId("block/abyssal_trove_short_alt0")).weight(8),
                        sound(ppId("block/abyssal_trove_short_alt1")).weight(8)
                )
        );

        this.add(HERBALISTS_LECTERN_APPEAR.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(HERBALISTS_LECTERN_APPEAR.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(HERBALISTS_LECTERN_APPEAR.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/herbalists_lectern_appear")).weight(1)
                )
        );

        this.add(HERBALISTS_LECTERN_DISAPPEAR.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(HERBALISTS_LECTERN_DISAPPEAR.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(HERBALISTS_LECTERN_DISAPPEAR.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/herbalists_lectern_disappear")).weight(1)
                )
        );

        this.add(PING_0.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(PING_0.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(PING_0.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("potion/ping_0")).weight(1)
                )
        );

        this.add(PING_1.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(PING_1.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(PING_1.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("potion/ping_1")).weight(1)
                )
        );

        this.add(PING_2.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(PING_2.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(PING_2.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("potion/ping_2")).weight(1)
                )
        );

        this.add(PING_3.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(PING_3.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(PING_3.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("potion/ping_3")).weight(1)
                )
        );

        this.add(GIANT_STEPS.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(GIANT_STEPS.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(GIANT_STEPS.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("potion/giant_steps")).weight(1)
                )
        );

        this.add(RECIPE_UNLOCKED.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(RECIPE_UNLOCKED.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(RECIPE_UNLOCKED.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/recipe_unlocked_0")).weight(1),
                        sound(ppId("block/recipe_unlocked_1")).weight(1),
                        sound(ppId("block/recipe_unlocked_2")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_0.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(MUTED_PLUCKS_0.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(MUTED_PLUCKS_0.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/muted_plucks_0")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_1.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(MUTED_PLUCKS_1.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(MUTED_PLUCKS_1.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/muted_plucks_1")).weight(1)
                )
        );

        this.add(SANGUINE_ALTAR_CONVERSION.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(SANGUINE_ALTAR_CONVERSION.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(SANGUINE_ALTAR_CONVERSION.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("block/conversion")).weight(1)
                )
        );

        this.add(LIGHTNING_BOLT_ABILITY.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(LIGHTNING_BOLT_ABILITY.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(LIGHTNING_BOLT_ABILITY.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("ability/lightning_0")).weight(1),
                        sound(ppId("ability/lightning_1")).weight(1)
                )
        );

        this.add(HEAVY_IMPACT.value(), definition()
                .subtitle("sound." + BuiltInRegistries.SOUND_EVENT.getKey(HEAVY_IMPACT.value()).getNamespace() + "." + BuiltInRegistries.SOUND_EVENT.getKey(HEAVY_IMPACT.value()).getPath()) // Set translation key
                .with(
                        sound(ppId("ability/stun_0")).weight(1),
                        sound(ppId("ability/stun_1")).weight(1)
                )
        );
    }
}

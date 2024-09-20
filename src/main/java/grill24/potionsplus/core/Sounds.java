package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sounds extends SoundDefinitionsProvider {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModInfo.MOD_ID);
    public static RegistryObject<SoundEvent> ABYSSAL_TROVE_DEPOSIT = SOUNDS.register("abyssal_trove_deposit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "abyssal_trove_deposit")));
    public static RegistryObject<SoundEvent> HERBALISTS_LECTERN_APPEAR = SOUNDS.register("herbalists_lectern_appear", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "herbalists_lectern_appear")));
    public static RegistryObject<SoundEvent> HERBALISTS_LECTERN_DISAPPEAR = SOUNDS.register("herbalists_lectern_disappear", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "herbalists_lectern_disappear")));
    public static RegistryObject<SoundEvent> PING_0 = SOUNDS.register("ping_0", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_0")));
    public static RegistryObject<SoundEvent> PING_1 = SOUNDS.register("ping_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_1")));
    public static RegistryObject<SoundEvent> PING_2 = SOUNDS.register("ping_2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_2")));
    public static RegistryObject<SoundEvent> PING_3 = SOUNDS.register("ping_3", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_3")));
    public static RegistryObject<SoundEvent> GIANT_STEPS = SOUNDS.register("giant_steps", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "giant_steps")));
    public static RegistryObject<SoundEvent> RECIPE_UNLOCKED = SOUNDS.register("recipe_unlocked", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "recipe_unlocked")));
    public static RegistryObject<SoundEvent> MUTED_PLUCKS_0 = SOUNDS.register("muted_plucks_0", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "muted_plucks_0")));
    public static RegistryObject<SoundEvent> MUTED_PLUCKS_1 = SOUNDS.register("muted_plucks_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "muted_plucks_1")));
    public static RegistryObject<SoundEvent> SANGUINE_ALTAR_CONVERSION = SOUNDS.register("sanguine_altar_conversion", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ModInfo.MOD_ID, "sanguine_altar_conversion")));

    /**
     * Creates a new instance of this data provider.
     *
     * @param output The {@linkplain PackOutput} instance provided by the data generator.
     * @param modId  The mod ID of the current mod.
     * @param helper The existing file helper provided by the event you are initializing this provider in.
     */
    public Sounds(PackOutput output, String modId, ExistingFileHelper helper) {
        super(output, modId, helper);
    }


    @Override
    public void registerSounds() {
        this.add(ABYSSAL_TROVE_DEPOSIT.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(ABYSSAL_TROVE_DEPOSIT.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(ABYSSAL_TROVE_DEPOSIT.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_long")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_medium")).weight(3),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short")).weight(5),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short_alt0")).weight(8),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short_alt1")).weight(8)
                )
        );

        this.add(HERBALISTS_LECTERN_APPEAR.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(HERBALISTS_LECTERN_APPEAR.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(HERBALISTS_LECTERN_APPEAR.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/herbalists_lectern_appear")).weight(1)
                )
        );

        this.add(HERBALISTS_LECTERN_DISAPPEAR.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(HERBALISTS_LECTERN_DISAPPEAR.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(HERBALISTS_LECTERN_DISAPPEAR.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/herbalists_lectern_disappear")).weight(1)
                )
        );

        this.add(PING_0.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(PING_0.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(PING_0.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_0")).weight(1)
                )
        );

        this.add(PING_1.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(PING_1.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(PING_1.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_1")).weight(1)
                )
        );

        this.add(PING_2.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(PING_2.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(PING_2.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_2")).weight(1)
                )
        );

        this.add(PING_3.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(PING_3.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(PING_3.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_3")).weight(1)
                )
        );

        this.add(GIANT_STEPS.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(GIANT_STEPS.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(GIANT_STEPS.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/giant_steps")).weight(1)
                )
        );

        this.add(RECIPE_UNLOCKED.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(RECIPE_UNLOCKED.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(RECIPE_UNLOCKED.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_0")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_1")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_2")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_0.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(MUTED_PLUCKS_0.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(MUTED_PLUCKS_0.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/muted_plucks_0")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_1.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(MUTED_PLUCKS_1.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(MUTED_PLUCKS_1.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/muted_plucks_1")).weight(1)
                )
        );

        this.add(SANGUINE_ALTAR_CONVERSION.get(), definition()
                .subtitle("sound." + ForgeRegistries.SOUND_EVENTS.getKey(SANGUINE_ALTAR_CONVERSION.get()).getNamespace() + "." + ForgeRegistries.SOUND_EVENTS.getKey(SANGUINE_ALTAR_CONVERSION.get()).getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/conversion")).weight(1)
                )
        );
    }
}

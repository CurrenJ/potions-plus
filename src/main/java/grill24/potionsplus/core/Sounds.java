package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Sounds extends SoundDefinitionsProvider {
    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModInfo.MOD_ID);
    public static RegistryObject<SoundEvent> ABYSSAL_TROVE_DEPOSIT = SOUNDS.register("abyssal_trove_deposit", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "abyssal_trove_deposit")));
    public static RegistryObject<SoundEvent> HERBALISTS_LECTERN_APPEAR = SOUNDS.register("herbalists_lectern_appear", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "herbalists_lectern_appear")));
    public static RegistryObject<SoundEvent> HERBALISTS_LECTERN_DISAPPEAR = SOUNDS.register("herbalists_lectern_disappear", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "herbalists_lectern_disappear")));
    public static RegistryObject<SoundEvent> PING_0 = SOUNDS.register("ping_0", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_0")));
    public static RegistryObject<SoundEvent> PING_1 = SOUNDS.register("ping_1", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_1")));
    public static RegistryObject<SoundEvent> PING_2 = SOUNDS.register("ping_2", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_2")));
    public static RegistryObject<SoundEvent> PING_3 = SOUNDS.register("ping_3", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "ping_3")));
    public static RegistryObject<SoundEvent> GIANT_STEPS = SOUNDS.register("giant_steps", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "giant_steps")));
    public static RegistryObject<SoundEvent> RECIPE_UNLOCKED = SOUNDS.register("recipe_unlocked", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "recipe_unlocked")));
    public static RegistryObject<SoundEvent> MUTED_PLUCKS_0 = SOUNDS.register("muted_plucks_0", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "muted_plucks_0")));
    public static RegistryObject<SoundEvent> MUTED_PLUCKS_1 = SOUNDS.register("muted_plucks_1", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "muted_plucks_1")));
    public static RegistryObject<SoundEvent> SANGUINE_ALTAR_CONVERSION = SOUNDS.register("sanguine_altar_conversion", () -> new SoundEvent(new ResourceLocation(ModInfo.MOD_ID, "sanguine_altar_conversion")));


    public Sounds(DataGenerator generator, String modId, ExistingFileHelper helper) {
        super(generator, modId, helper);
    }

    @Override
    public void registerSounds() {
        this.add(ABYSSAL_TROVE_DEPOSIT.get(), definition()
                .subtitle("sound." + ABYSSAL_TROVE_DEPOSIT.get().getRegistryName().getNamespace() + "." + ABYSSAL_TROVE_DEPOSIT.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_long")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_medium")).weight(3),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short")).weight(5),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short_alt0")).weight(8),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/abyssal_trove_short_alt1")).weight(8)
                )
        );

        this.add(HERBALISTS_LECTERN_APPEAR.get(), definition()
                .subtitle("sound." + HERBALISTS_LECTERN_APPEAR.get().getRegistryName().getNamespace() + "." + HERBALISTS_LECTERN_APPEAR.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/herbalists_lectern_appear")).weight(1)
                )
        );

        this.add(HERBALISTS_LECTERN_DISAPPEAR.get(), definition()
                .subtitle("sound." + HERBALISTS_LECTERN_DISAPPEAR.get().getRegistryName().getNamespace() + "." + HERBALISTS_LECTERN_DISAPPEAR.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/herbalists_lectern_disappear")).weight(1)
                )
        );

        this.add(PING_0.get(), definition()
                .subtitle("sound." + PING_0.get().getRegistryName().getNamespace() + "." + PING_0.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_0")).weight(1)
                )
        );

        this.add(PING_1.get(), definition()
                .subtitle("sound." + PING_1.get().getRegistryName().getNamespace() + "." + PING_1.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_1")).weight(1)
                )
        );

        this.add(PING_2.get(), definition()
                .subtitle("sound." + PING_2.get().getRegistryName().getNamespace() + "." + PING_2.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_2")).weight(1)
                )
        );

        this.add(PING_3.get(), definition()
                .subtitle("sound." + PING_3.get().getRegistryName().getNamespace() + "." + PING_3.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/ping_3")).weight(1)
                )
        );

        this.add(GIANT_STEPS.get(), definition()
                .subtitle("sound." + GIANT_STEPS.get().getRegistryName().getNamespace() + "." + GIANT_STEPS.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "potion/giant_steps")).weight(1)
                )
        );

        this.add(RECIPE_UNLOCKED.get(), definition()
                .subtitle("sound." + RECIPE_UNLOCKED.get().getRegistryName().getNamespace() + "." + RECIPE_UNLOCKED.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_0")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_1")).weight(1),
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/recipe_unlocked_2")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_0.get(), definition()
                .subtitle("sound." + MUTED_PLUCKS_0.get().getRegistryName().getNamespace() + "." + MUTED_PLUCKS_0.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/muted_plucks_0")).weight(1)
                )
        );

        this.add(MUTED_PLUCKS_1.get(), definition()
                .subtitle("sound." + MUTED_PLUCKS_1.get().getRegistryName().getNamespace() + "." + MUTED_PLUCKS_1.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/muted_plucks_1")).weight(1)
                )
        );

        this.add(SANGUINE_ALTAR_CONVERSION.get(), definition()
                .subtitle("sound." + SANGUINE_ALTAR_CONVERSION.get().getRegistryName().getNamespace() + "." + SANGUINE_ALTAR_CONVERSION.get().getRegistryName().getPath()) // Set translation key
                .with(
                        sound(new ResourceLocation(ModInfo.MOD_ID, "block/conversion")).weight(1)
                )
        );
    }
}

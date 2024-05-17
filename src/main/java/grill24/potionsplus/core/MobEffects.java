package grill24.potionsplus.core;


import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class MobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModInfo.MOD_ID);

    public static final RegistryObject<MobEffect> GEODE_GRACE = EFFECTS.register("geode_grace", () ->
            new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));

    public static final RegistryObject<MobEffect> FALL_OF_THE_VOID = EFFECTS.register("fall_of_the_void", () ->
            new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));

    public static final RegistryObject<MobEffect> EXPLODING = EFFECTS.register("exploding", () ->
            new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));

    public static final RegistryObject<MobEffect> MAGNETIC = EFFECTS.register("magnetic", () ->
            new MagneticEffect(MobEffectCategory.BENEFICIAL, 0x556096));

    public static final RegistryObject<MobEffect> TELEPORTATION = EFFECTS.register("teleportation", () ->
            new TeleportationEffect(MobEffectCategory.NEUTRAL, 0x556096));

    public static final Lazy<Map<ResourceLocation, Integer>> POTION_ICON_INDEX_MAP = Lazy.of(PUtil::getAllMobEffectsIconStackSizeMap);
    public static final int POTION_EFFECT_INDEX_PROPERTY_DIVIDEND = 64;
}

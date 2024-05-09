package grill24.potionsplus.core;


import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModInfo.MOD_ID);

    public static final RegistryObject<MobEffect> GEODE_GRACE = EFFECTS.register("geode_grace", () ->
            new MobEffect(MobEffectCategory.NEUTRAL, 0xECD350));

    public static final RegistryObject<MobEffect> FALL_OF_THE_VOID = EFFECTS.register("fall_of_the_void", () ->
            new MobEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));
}

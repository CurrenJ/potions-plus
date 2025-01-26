package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.AdjustableStrength;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.skill.ability.instance.SimpleToggleable;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AbilityInstanceTypes {
    public static final DeferredRegister<AbilityInstanceType<?>> ABILITY_INSTANCE_TYPE = DeferredRegister.create(PotionsPlusRegistries.ABILITY_INSTANCE_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<AbilityInstanceType<?>, SimpleToggleable> SIMPLE_TOGGLEABLE = ABILITY_INSTANCE_TYPE.register("simple_toggleable", SimpleToggleable::new);
    public static final DeferredHolder<AbilityInstanceType<?>, AdjustableStrength> ADJUSTABLE_STRENGTH = ABILITY_INSTANCE_TYPE.register("adjustable_strength", AdjustableStrength::new);
}

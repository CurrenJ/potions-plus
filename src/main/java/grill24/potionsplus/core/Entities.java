package grill24.potionsplus.core;

import grill24.potionsplus.entity.InvisibleFireDamager;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Entities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ModInfo.MOD_ID);

    public static final RegistryObject<EntityType<InvisibleFireDamager>> INVISIBLE_FIRE_DAMAGER = ENTITIES.register("invisible_fire_damager", () -> EntityType.Builder.<InvisibleFireDamager>of(InvisibleFireDamager::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build(ModInfo.MOD_ID + ":invisible_fire_damager"));
}

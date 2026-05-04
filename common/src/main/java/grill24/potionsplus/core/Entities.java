package grill24.potionsplus.core;

import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.entity.InvisibleFireDamager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class Entities {
    @SuppressWarnings("unchecked")
    public static Holder<EntityType<InvisibleFireDamager>> INVISIBLE_FIRE_DAMAGER;
    @SuppressWarnings("unchecked")
    public static Holder<EntityType<Grungler>> GRUNGLER;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<EntityType<?>>, Holder<EntityType<?>>> register) {
        INVISIBLE_FIRE_DAMAGER = (Holder<EntityType<InvisibleFireDamager>>) (Holder<?>) register.apply("invisible_fire_damager",
                () -> EntityType.Builder.<InvisibleFireDamager>of((type, level) ->
                                new InvisibleFireDamager(level), MobCategory.MISC)
                        .sized(0.98F, 0.98F)
                        .clientTrackingRange(10)
                        .updateInterval(20)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, ppId("invisible_fire_damager")))
        );

        GRUNGLER = (Holder<EntityType<Grungler>>) (Holder<?>) register.apply("grungler",
                () -> EntityType.Builder.<Grungler>of(Grungler::new, MobCategory.MONSTER)
                        .sized(0.6F, 1.25F)
                        .clientTrackingRange(10)
                        .updateInterval(3)
                        .eyeHeight(1.25F)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, ppId("grungler")))
        );
    }
}

package grill24.potionsplus.core;

import grill24.potionsplus.entity.Grungler;
import grill24.potionsplus.entity.InvisibleFireDamager;
import grill24.potionsplus.entity.PrimedSpecialCake;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Entities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<InvisibleFireDamager>> INVISIBLE_FIRE_DAMAGER = ENTITIES.register("invisible_fire_damager",
            () -> EntityType.Builder.<InvisibleFireDamager>of((type, level) ->
                            new InvisibleFireDamager(level), MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ppId("invisible_fire_damager")))
    );

    public static final DeferredHolder<EntityType<?>, EntityType<Grungler>> GRUNGLER = ENTITIES.register("grungler",
            () -> EntityType.Builder.<Grungler>of(Grungler::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.25F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .eyeHeight(1.25F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ppId("grungler")))
    );

    public static final DeferredHolder<EntityType<?>, EntityType<PrimedSpecialCake>> PRIMED_SPECIAL_CAKE = ENTITIES.register("primed_special_cake",
            () -> EntityType.Builder.<PrimedSpecialCake>of(PrimedSpecialCake::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ppId("primed_special_cake")))
    );

    @SubscribeEvent // on the mod event bus
    public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(GRUNGLER.get(), Grungler.createAttributes().build());
    }
}

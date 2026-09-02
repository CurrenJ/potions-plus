package grill24.potionsplus.core.forge;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@Mod(ModInfo.MOD_ID)
public class PotionsPlusForge {
    public static final Logger LOGGER = LogUtils.getLogger();

    // DeferredRegisters for the registries consolidated into common (mirroring the 26.1.2 forge
    // entrypoint). ENTITIES, BLOCK_PREDICATE_TYPES and CONSUME_EFFECTS are dropped: 1.21.1 has no
    // common core.Entities hub and no portable block-predicate/consume-effect registries to fill.
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

    static {
        // Set up the PotionBuilder factory before the common Potions class loads (its static fields
        // trigger registration through it). The Potion constructor's first argument is the
        // translation-key suffix, not a display name; it must match the registry name (the
        // "Potion.name() trap").
        PotionBuilder.potionFactory = (name, effectSupplier) -> ForgeHolder.of(POTIONS.register(name, () -> new Potion(name, effectSupplier.get())));
    }

    public PotionsPlusForge() {
        LOGGER.info("Potions Plus (Forge) initializing");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // 1. Advancements/Attributes/LootItemConditions - common class-load order mirrors the fabric
        //    entrypoint; on Forge the actual registration is deferred to the RegisterEvents.
        grill24.potionsplus.core.Advancements.init(register(TRIGGERS));
        grill24.potionsplus.core.Attributes.init(register(ATTRIBUTES));
        grill24.potionsplus.core.LootItemConditions.init(register(LOOT_ITEM_CONDITIONS));

        // 2. MobEffects BEFORE Potions - Potions' static fields capture MobEffects.* holders at
        //    class-load time, and MobEffectInstance eagerly derefs its effect holder during
        //    construction, so the effects must be registered before the potion fields build.
        grill24.potionsplus.core.potion.MobEffects.init(register(MOB_EFFECTS));
        grill24.potionsplus.core.potion.Potions.init(register(POTIONS));

        // 3. Percentage attributes -> vanilla RangedAttribute (Forge has no PercentageAttribute).
        {
            Holder<Attribute> sprintingSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    register(ATTRIBUTES), "player.sprinting_speed_bonus",
                    () -> new RangedAttribute(Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SPRINT_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.SPRINTING_SPEED = sprintingSpeed;

            Holder<Attribute> useSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    register(ATTRIBUTES), "player.use_speed_bonus",
                    () -> new RangedAttribute(Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_USE_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.USE_SPEED_BONUS = useSpeed;
        }

        // 4. ArmorMaterials BEFORE items - WreathItem's ArmorItem ctor eagerly derefs
        //    ArmorMaterials.WREATH.value() when the ITEM registry flushes.
        ArmorMaterials.init();

        // 5. Blocks BEFORE items - 1.21.1 BlockItem/ItemNameBlockItem ctors take a concrete Block,
        //    so item suppliers deref the block holder at ITEM RegisterEvent flush (BLOCK flushes
        //    before ITEM on Forge). Each sub-hub registers its block then its block item.
        Blocks.init();
        Items.init();

        // 6. Remaining registries (all static-initializer-driven; the flat hubs class-load here).
        Sounds.init();
        Particles.init();
        Recipes.init();
        MenuTypes.init();
        LootItemFunctions.init();
        NumberProviders.init();
        CreativeModeTabs.init();

        // 7. Register every DeferredRegister on the mod bus. Forge flushes each at its RegisterEvent
        //    in registry-dependency order (ARMOR_MATERIAL/BLOCK before ITEM, MOB_EFFECT before POTION).
        Blocks.BLOCKS.register(bus);
        Items.ITEMS.register(bus);
        ArmorMaterials.ARMOR_MATERIALS.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        Sounds.SOUND_EVENTS.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        NumberProviders.NUMBER_PROVIDERS.register(bus);
        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        TRIGGERS.register(bus);
        ATTRIBUTES.register(bus);
        MOB_EFFECTS.register(bus);
        POTIONS.register(bus);
        LOOT_ITEM_CONDITIONS.register(bus);

        // 8. Network packets (serverbound handlers + clientbound codecs). Phase 5.
        Packets.register();

        // 9. Event listeners, capabilities, loot modifiers, lifecycle hooks. Phase 7/8.
        grill24.potionsplus.event.forge.EffectListeners.register();
        Capabilities.register();
        LootModifiers.register();
        ServerLifecycleListeners.register();
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> register(DeferredRegister<T> register) {
        return (name, supplier) -> ForgeHolder.of(register.register(name, supplier));
    }
}

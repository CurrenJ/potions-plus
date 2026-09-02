package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.config.PotionsPlusConfig;
import grill24.potionsplus.core.*;
import grill24.potionsplus.core.neoforge.potion.MobEffectsRegistrar;
import grill24.potionsplus.core.neoforge.potion.PotionsRegistrar;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // DeferredRegister for the attribute registry, consolidated into the common core.Attributes
    // hub (whose original neoforge-only hub was deleted in Phase 4; every other hub keeps its DR on
    // its own neoforge class). The common Potions hub registers at class-load through
    // PotionBuilder.potionFactory, which is wired to PotionsRegistrar.POTIONS in the static block
    // below, before Potions is first touched.
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);

    static {
        // Set up PotionBuilder factory before the common Potions class loads (its static fields
        // trigger registration). The Potion constructor's first argument is the translation-key
        // suffix appended to "item.minecraft.potion.effect." - not a display name. It must match
        // the registry name.
        PotionBuilder.potionFactory = (name, effectSupplier) -> PotionsRegistrar.POTIONS.register(name, () -> new net.minecraft.world.item.alchemy.Potion(name, effectSupplier.get()));
    }

    public PotionsPlus(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, PotionsPlusConfig.CONFIG_SPEC);

        // Initialize common registration holders
        grill24.potionsplus.core.Advancements.init(Advancements.TRIGGERS::register);
        grill24.potionsplus.core.Attributes.init(ATTRIBUTES::register);
        // MobEffects must be initialized before Potions - Potions' static fields reference
        // MobEffects.* holders directly at class-load time, so Potions.init() would otherwise
        // capture null for every custom (non-vanilla) effect. MobEffectsRegistrar's static block
        // calls MobEffects.init() then registers the neoforge-only effects.
        MobEffectsRegistrar.EFFECTS.register(bus);
        // PotionsRegistrar's static block builds its 8 neoforge-only potions through potionFactory,
        // appending to the common Potions.ALL_POTION_GENERATION_DATA (which triggers the common
        // Potions class-load and registers the 23 portable potions first).
        PotionsRegistrar.POTIONS.register(bus);
        grill24.potionsplus.core.potion.Potions.init(PotionsRegistrar.POTIONS::register);

        // Register platform-specific percentage attributes (NeoForge PercentageAttribute)
        {
            Holder<Attribute> sprintingSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    ATTRIBUTES::register, "player.sprinting_speed_bonus",
                    () -> new net.neoforged.neoforge.common.PercentageAttribute(
                            Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SPRINT_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.SPRINTING_SPEED = sprintingSpeed;

            Holder<Attribute> useSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    ATTRIBUTES::register, "player.use_speed_bonus",
                    () -> new net.neoforged.neoforge.common.PercentageAttribute(
                            Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_USE_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.USE_SPEED_BONUS = useSpeed;
        }

        // Init LootItemConditions after its DR is available
        grill24.potionsplus.core.LootItemConditions.init(LootItemConditions.LOOT_ITEM_CONDITIONS::register);

        ArmorMaterials.init();

        // Register all deferred registers
        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        ArmorMaterials.ARMOR_MATERIALS.register(bus);
        Items.ITEMS.register(bus);
        Entities.ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        RecipesRegistrar.RECIPE_TYPES.register(bus);
        RecipesRegistrar.RECIPE_SERIALIZERS.register(bus);
        NeoSounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        Advancements.TRIGGERS.register(bus);

        Features.FEATURES.register(bus);
        DataAttachments.ATTACHMENT_TYPES.register(bus);
        ATTRIBUTES.register(bus);
        LootItemConditions.LOOT_ITEM_CONDITIONS.register(bus);
        PlacementModifierTypes.PLACEMENT_MODIFIER_TYPES.register(bus);
        DataComponents.DATA_COMPONENTS.register(bus);
        MenuTypes.MENU_TYPES.register(bus);
        LootItemFunctions.LOOT_ITEM_FUNCTIONS.register(bus);
        NumberProvidersRegistrar.NUMBER_PROVIDERS.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }
}

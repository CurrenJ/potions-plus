package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.config.PotionsPlusConfig;
import grill24.potionsplus.core.*;
import grill24.potionsplus.core.neoforge.potion.MobEffectsRegistrar;
import grill24.potionsplus.core.neoforge.potion.PotionsRegistrar;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    public PotionsPlus(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, PotionsPlusConfig.CONFIG_SPEC);

        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        ArmorMaterials.ARMOR_MATERIALS.register(bus);
        Items.ITEMS.register(bus);
        Entities.ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        RecipesRegistrar.RECIPE_TYPES.register(bus);
        RecipesRegistrar.RECIPE_SERIALIZERS.register(bus);
        MobEffectsRegistrar.EFFECTS.register(bus);
        PotionsRegistrar.POTIONS.register(bus);
        Sounds.SOUNDS.register(bus);
        LootModifiers.LOOT_MODIFIERS.register(bus);
        Advancements.TRIGGERS.register(bus);

        Features.FEATURES.register(bus);
        DataAttachments.ATTACHMENT_TYPES.register(bus);
        Attributes.ATTRIBUTES.register(bus);
        LootItemConditions.LOOT_ITEM_CONDITIONS.register(bus);
        PlacementModifierTypes.PLACEMENT_MODIFIER_TYPES.register(bus);
        DataComponents.DATA_COMPONENTS.register(bus);
        MenuTypes.MENU_TYPES.register(bus);
        LootItemFunctions.LOOT_ITEM_FUNCTIONS.register(bus);
        NumberProvidersRegistrar.NUMBER_PROVIDERS.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
    }
}

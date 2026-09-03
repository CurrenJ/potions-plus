package grill24.potionsplus.core.fabric;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.PotionBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PotionsPlusFabric implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Potions Plus (Fabric) initializing");

        // 1. Potion factory FIRST - before the Potions class loads (its static fields trigger
        //    registration via potionFactory). The Potion ctor's first arg is the translation-key
        //    suffix and MUST equal the registry name (the "Potion.name() trap").
        PotionBuilder.potionFactory = (name, effectSupplier) -> FabricRegistration.register(
                BuiltInRegistries.POTION, name, () -> new Potion(name, effectSupplier.get()));

        // 2. Immediate registrations (Fabric registers eagerly, no deferred flush).
        grill24.potionsplus.core.Advancements.init(registrar(BuiltInRegistries.TRIGGER_TYPES));
        grill24.potionsplus.core.Attributes.init(registrar(BuiltInRegistries.ATTRIBUTE));
        grill24.potionsplus.core.LootItemConditions.init(registrar(BuiltInRegistries.LOOT_CONDITION_TYPE));

        // 3. MobEffects BEFORE Potions - Potions' static fields capture MobEffects.* holders at
        //    class-load time, and MobEffectInstance eagerly derefs its effect holder during
        //    construction, so the effects must be bound before the potion fields build.
        grill24.potionsplus.core.potion.MobEffects.init(registrar(BuiltInRegistries.MOB_EFFECT));
        grill24.potionsplus.core.potion.Potions.init(registrar(BuiltInRegistries.POTION));

        // 4. Percentage attributes -> vanilla RangedAttribute (Fabric has no PercentageAttribute).
        {
            Holder<Attribute> sprintingSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    registrar(BuiltInRegistries.ATTRIBUTE), "player.sprinting_speed_bonus",
                    () -> new RangedAttribute(Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_SPRINT_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.SPRINTING_SPEED = sprintingSpeed;

            Holder<Attribute> useSpeed = grill24.potionsplus.core.Attributes.registerPlatformAttribute(
                    registrar(BuiltInRegistries.ATTRIBUTE), "player.use_speed_bonus",
                    () -> new RangedAttribute(Translations.DESCRIPTION_POTIONSPLUS_ATTRIBUTE_USE_SPEED_LEVEL, 0.0, 0.0, 1.0));
            grill24.potionsplus.core.Attributes.USE_SPEED_BONUS = useSpeed;
        }

        // 5. ArmorMaterials BEFORE items - WreathItem's ArmorItem ctor eagerly derefs
        //    ArmorMaterials.WREATH.value(), so the material must be bound first.
        grill24.potionsplus.core.ArmorMaterials.init(registrar(BuiltInRegistries.ARMOR_MATERIAL));

        // 6. Blocks BEFORE items. Deviation from the 26.1.2 mirror (which registers items first):
        //    1.21.1 BlockItem/ItemNameBlockItem constructors take a concrete Block, so they eagerly
        //    deref the block holder at registration time (26.1.2's lazy Holder-based ctor allowed
        //    items to precede blocks). Each fabric sub-hub registers its block, then immediately its
        //    block item, so ordering is preserved.
        Blocks.init();
        Items.init();

        // 7. DISPENSER association (Fabric: FabricBlockEntityType.addSupportedBlock, replacing
        //    NeoForge's BlockEntityTypeAddBlocksEvent).
        ((FabricBlockEntityType) BlockEntityType.DISPENSER).addSupportedBlock(
                grill24.potionsplus.core.blocks.BlockEntityBlocks.PRECISION_DISPENSER.value());

        // 8. Remaining registries (all static-initializer-driven, immediate on Fabric).
        Sounds.init();
        Particles.init();
        Recipes.init();
        MenuTypes.init();
        LootItemFunctions.init();
        NumberProviders.init();
        CreativeModeTabs.init();

        // 9. Network packets (server-side: serverbound handlers + clientbound codecs). Phase 5.
        Packets.registerServer();

        // 10. Event listeners, capabilities, loot/biome modifiers, lifecycle hooks. Phase 7/8.
        grill24.potionsplus.event.fabric.EffectListeners.register();
        grill24.potionsplus.event.fabric.TickListeners.registerServer();
        grill24.potionsplus.event.fabric.CommandListeners.register();
        Capabilities.register();
        LootModifiers.register();
        BiomeModifiers.register();
        ServerLifecycleListeners.register();
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(Registry<T> registry) {
        return (name, supplier) -> FabricRegistration.register(registry, name, supplier);
    }
}

package grill24.potionsplus.core.fabric;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
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
        grill24.potionsplus.core.Entities.init(registrar(BuiltInRegistries.ENTITY_TYPE));
        grill24.potionsplus.core.BlockPredicateTypes.init(registrar(BuiltInRegistries.BLOCK_PREDICATE_TYPE));
        grill24.potionsplus.core.ConsumeEffects.init(registrar(BuiltInRegistries.CONSUME_EFFECT_TYPE));

        // 3. MobEffects BEFORE Potions - Potions' static fields capture MobEffects.* holders at
        //    class-load time, and OreFlowerBlock factories are evaluated immediately on Fabric.
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

        // 5. LootItemConditions.
        grill24.potionsplus.core.LootItemConditions.init(registrar(BuiltInRegistries.LOOT_CONDITION_TYPE));

        // 6. DataComponents (before items, in case any item uses core.DataComponents.WEIGHT).
        DataComponents.init();

        // 7. Items before Blocks before block entities.
        Items.init();
        Blocks.init();

        // 8. DISPENSER association (Fabric: FabricBlockEntityType.addSupportedBlock, replacing
        //    NeoForge's BlockEntityTypeAddBlocksEvent).
        ((FabricBlockEntityType) BlockEntityType.DISPENSER).addValidBlock(BlockEntityBlocks.PRECISION_DISPENSER.value());

        // 9. Remaining registries (all static-initializer-driven, immediate on Fabric).
        Sounds.init();
        Particles.init();
        Recipes.init();
        MenuTypes.init();
        CommandArgumentTypes.init();
        LootItemFunctions.init();
        NumberProviders.init();
        CreativeModeTabs.init();

        // 10. Network packets (server-side: serverbound handlers + clientbound codecs).
        Packets.registerServer();

        // 11. Server-side event listeners (commands, ticks, death/attributes, interactions, potion stack size).
        grill24.potionsplus.event.fabric.FabricEventListeners.register();

        // 12. Clothesline item-storage capability (fabric-transfer-api-v1), global loot modifiers
        //     (fabric-loot-api-v3 MODIFY_DROPS), and lunar berry bush biome modifiers.
        Capabilities.register();
        LootModifiers.register();
        BiomeModifiers.register();
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(Registry<T> registry) {
        return (name, supplier) -> FabricRegistration.register(registry, name, supplier);
    }
}

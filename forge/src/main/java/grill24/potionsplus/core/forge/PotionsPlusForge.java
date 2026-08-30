package grill24.potionsplus.core.forge;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.core.potion.PotionBuilder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@Mod(ModInfo.MOD_ID)
public class PotionsPlusForge {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static @Nullable MinecraftServer SERVER;

    public static long worldSeed = -1;

    // DeferredRegisters for registries consolidated into common
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ModInfo.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES = DeferredRegister.create(Registries.BLOCK_PREDICATE_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, ModInfo.MOD_ID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

    static {
        // Set up PotionBuilder factory before Potions class loads (its static fields trigger registration).
        // The Potion constructor's first argument is the translation-key suffix, not a display name; it
        // must match the registry name (the "Potion.name() trap").
        PotionBuilder.potionFactory = (name, effectSupplier) -> ForgeHolder.of(POTIONS.register(name, () -> new Potion(name, effectSupplier.get())));
    }

    public PotionsPlusForge(FMLModContainer container) {
        var bus = container.getModBusGroup();

        // Initialize common registration holders
        grill24.potionsplus.core.Advancements.init(register(TRIGGERS));
        grill24.potionsplus.core.Attributes.init(register(ATTRIBUTES));
        grill24.potionsplus.core.Entities.init(register(ENTITIES));
        grill24.potionsplus.core.BlockPredicateTypes.init(register(BLOCK_PREDICATE_TYPES));
        grill24.potionsplus.core.ConsumeEffects.init(register(CONSUME_EFFECTS));
        // MobEffects must be initialized before Potions - Potions' static fields reference MobEffects.*
        // holders directly at class-load time.
        grill24.potionsplus.core.potion.MobEffects.init(register(MOB_EFFECTS));
        grill24.potionsplus.core.potion.Potions.init(register(POTIONS));

        // Register platform-specific percentage attributes (Forge has no PercentageAttribute; use vanilla RangedAttribute).
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

        // Init LootItemConditions after LOOT_ITEM_CONDITIONS DR is available
        grill24.potionsplus.core.LootItemConditions.init(register(LOOT_ITEM_CONDITIONS));

        Blocks.BLOCKS.register(bus);
        Blocks.BLOCK_ENTITIES.register(bus);
        Items.ITEMS.register(bus);
        ENTITIES.register(bus);
        Particles.PARTICLE_TYPES.register(bus);
        Recipes.RECIPE_TYPES.register(bus);
        Recipes.RECIPE_SERIALIZERS.register(bus);
        Recipes.RECIPE_DISPLAYS.register(bus);
        MOB_EFFECTS.register(bus);
        POTIONS.register(bus);
        Sounds.SOUNDS.register(bus);
        TRIGGERS.register(bus);
        BLOCK_PREDICATE_TYPES.register(bus);
        CommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);
        ATTRIBUTES.register(bus);
        LOOT_ITEM_CONDITIONS.register(bus);
        DataComponents.DATA_COMPONENTS.register(bus);
        CONSUME_EFFECTS.register(bus);
        MenuTypes.MENU_TYPES.register(bus);
        LootItemFunctions.LOOT_ITEM_FUNCTIONS.register(bus);
        NumberProviders.NUMBER_PROVIDERS.register(bus);

        CreativeModeTabs.CREATIVE_MODE_TABS.register(bus);

        // Network packets (channel built once; runs on both dists via the @Mod constructor).
        Packets.register();

        // Server-side event listeners (advancements, effects, attributes, commands, ticks, players, potion stack size).
        grill24.potionsplus.event.forge.ForgeEventListeners.register();
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> register(DeferredRegister<T> register) {
        return (name, supplier) -> ForgeHolder.of(register.register(name, supplier));
    }
}

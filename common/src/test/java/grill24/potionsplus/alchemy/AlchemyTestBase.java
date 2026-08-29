package grill24.potionsplus.alchemy;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;

/**
 * Boots Minecraft's registries once for the whole test run.
 *
 * <p>The alchemy package is deliberately free of access-widened members and of any mod registry, so
 * it can be exercised against a plain vanilla bootstrap - no loader, no mixins, no mod init. Tests use
 * vanilla potions and effects only; anything that needs the mod's own registry entries belongs in a
 * game test instead.
 */
public abstract class AlchemyTestBase {

    private static boolean bootstrapped;

    @BeforeAll
    static synchronized void bootstrapMinecraft() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        // Bootstrap.bootStrap() populates the registries but does not bind items' default data
        // components - the server normally does that at the end of a datapack reload. Without this,
        // constructing any ItemStack fails with "Components not bound yet".
        HolderLookup.Provider registries = VanillaRegistries.createLookup();
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
                .forEach(DataComponentInitializers.PendingComponents::apply);
    }

    protected static MobEffectInstance effect(Holder<MobEffect> type, int duration, int amplifier) {
        return new MobEffectInstance(type, duration, amplifier);
    }

    /** A potion container carrying exactly the given custom effects and no base potion. */
    protected static ItemStack customPotion(PotionContainer container, MobEffectInstance... effects) {
        return PotionDataBuilder.fromEmpty()
                .withEffects(java.util.List.of(effects))
                .applyTo(container.createEmpty(1));
    }
}

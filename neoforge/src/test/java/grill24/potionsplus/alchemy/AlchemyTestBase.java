package grill24.potionsplus.alchemy;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
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
        // Unlike later versions, 1.21.1's Bootstrap.bootStrap() binds items' default data components
        // directly at registration time - there is no separate DataComponentInitializers pass to run
        // afterward, so constructing an ItemStack works immediately.
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

package grill24.potionsplus.alchemy;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies the vanilla bootstrap actually produced usable registries before anything else runs. */
class BootstrapSmokeTest extends AlchemyTestBase {

    @Test
    void registriesAreAvailable() {
        assertNotNull(Items.POTION);
        assertNotNull(Potions.HEALING.value());
        assertTrue(Potions.HEALING.unwrapKey().isPresent());
    }
}

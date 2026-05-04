package grill24.potionsplus.client.integration.jei;

/**
 * JEI integration is disabled in MC 26.1.2 due to JEI API still referencing
 * {@code ResourceLocation} which has been renamed to {@code Identifier}.
 * Re-enable when JEI updates.
 */
public class JeiPotionsPlusPlugin {
    public static void scheduleUpdateJeiHiddenBrewingCauldronRecipes() {
        // JEI is not available in MC 26.1.2
    }
}

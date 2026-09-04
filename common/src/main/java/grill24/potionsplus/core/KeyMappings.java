package grill24.potionsplus.core;

import net.minecraft.client.KeyMapping;

/**
 * Shared key-mapping constants + mutable holder, mirroring the {@code core.Particles}/{@code
 * core.Blocks} idiom used elsewhere in this tree (Decision 4a: the actual {@link KeyMapping}
 * instance is constructed per loader since vanilla has no cross-loader constructor for one that
 * carries a {@code KeyConflictContext} - NeoForge/Forge's is a platform type, Fabric registers a
 * plain vanilla {@code KeyMapping} via {@code KeyBindingHelper}). Each loader's registration sets
 * {@link #ACTIVATE_ABILITY} once so common code (e.g. a shared tick listener) can consume it
 * without depending on any platform module.
 */
public final class KeyMappings {
    public static final String ACTIVATE_ABILITY_TRANSLATION_KEY = "key.potionsplus.activate_ability";
    public static final String CATEGORY_TRANSLATION_KEY = "key.categories.potionsplus";

    public static KeyMapping ACTIVATE_ABILITY;

    private KeyMappings() {
    }
}

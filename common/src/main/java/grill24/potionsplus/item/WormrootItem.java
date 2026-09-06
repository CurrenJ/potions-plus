package grill24.potionsplus.item;

import net.minecraft.world.item.Item;

/**
 * Ported from the old NeoForge tree. The pre-port {@code onEntityItemUpdate} wormroot→rotten-wormroot
 * conversion hook is a NeoForge extension method (not on vanilla {@link Item}), so it was dropped in
 * the 26.1.2 mirror's common consolidation. See docs/multi-loader-expansion.md Phase 4.
 */
public class WormrootItem extends Item {

    public WormrootItem(Properties properties) {
        super(properties);
    }

}

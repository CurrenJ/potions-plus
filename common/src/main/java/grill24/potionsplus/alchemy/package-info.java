/**
 * The single owner of potion and mob-effect data manipulation in Potions Plus.
 *
 * <p>Everything that reads or writes {@link net.minecraft.core.component.DataComponents#POTION_CONTENTS}
 * belongs here. Call sites outside this package should go through {@link grill24.potionsplus.alchemy.PotionData}
 * (reads), {@link grill24.potionsplus.alchemy.PotionDataBuilder} (writes) and
 * {@link grill24.potionsplus.alchemy.EffectComparison} (equality), never through the component directly.
 *
 * <h2>Invariants</h2>
 * <ol>
 *     <li><b>Nothing mutates its arguments.</b> Every method that produces a modified stack returns a new
 *     one. This package never writes a component onto a stack it was handed.</li>
 *     <li><b>No accessor throws for missing data.</b> A stack with no potion contents reads as
 *     {@link grill24.potionsplus.alchemy.PotionData#EMPTY}, not an exception. Callers that need to know
 *     the difference ask {@link grill24.potionsplus.alchemy.PotionData#isEmpty()}.</li>
 *     <li><b>No external component access.</b> {@code DataComponents.POTION_CONTENTS} is referenced only
 *     inside this package.</li>
 * </ol>
 *
 * <h2>Why this exists</h2>
 * <p>Potion handling accumulated across three ports (1.18.2 to 1.21.1 to 26.1.2) without a single owner.
 * The predecessor, {@code grill24.potionsplus.utility.PUtil}, covered reads but exposed exactly one write
 * method, so call sites reconstructed {@link net.minecraft.world.item.alchemy.PotionContents} by hand and
 * drifted apart. This package replaces it; {@code PUtil} was retired in phase 3.
 *
 * <h2>Amplifier and duration ceilings</h2>
 * <p>{@link grill24.potionsplus.alchemy.PotionDataBuilder#build()} clamps every custom effect's amplifier
 * and duration to {@link grill24.potionsplus.alchemy.EffectScaling#MAX_AMPLIFIER} and
 * {@link grill24.potionsplus.alchemy.EffectScaling#MAX_DURATION_TICKS}. Effects still linked through a
 * registered {@link net.minecraft.world.item.alchemy.Potion} are not clamped - this package does not own
 * that data, and existing saved potions of that kind are unaffected.
 */
package grill24.potionsplus.alchemy;

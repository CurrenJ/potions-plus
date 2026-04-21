package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record Milestone(Identifier advancementId) {
    public static final Codec<Milestone> CODEC = Identifier.CODEC.xmap(Milestone::new, Milestone::advancementId);
}

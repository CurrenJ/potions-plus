package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record Milestone(ResourceLocation advancementId) {
    public static final Codec<Milestone> CODEC = ResourceLocation.CODEC.xmap(Milestone::new, Milestone::advancementId);
}

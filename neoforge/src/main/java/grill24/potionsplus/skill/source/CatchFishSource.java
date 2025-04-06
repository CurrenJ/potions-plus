package grill24.potionsplus.skill.source;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.core.SkillPointSources;
import grill24.potionsplus.event.PpFishCaughtEvent;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.stream.Stream;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CatchFishSource extends SkillPointSource<ItemStack, CatchFishSourceConfiguration> {
    public static final ResourceLocation ID = ppId("catch_fish");

    public CatchFishSource() {
        super(CatchFishSourceConfiguration.CODEC);
    }

    @SubscribeEvent
    public static void onCatchFish(final PpFishCaughtEvent event) {
        SkillsData.triggerSkillPointSource(event.getPlayer(), SkillPointSources.CATCH_FISH.value(), event.getFish());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public float evaluateSkillPointsToAdd(CatchFishSourceConfiguration config, ItemStack fish) {
        Stream<TagKey<Item>> tags = fish.getTags();
        for (TagKey<Item> tag : tags.toList()) {
            if (config.getPointsPerFish().containsKey(tag)) {
                float sizeBonus = 0F;
                if (fish.has(DataComponents.FISH_SIZE)) {
                    sizeBonus = fish.get(DataComponents.FISH_SIZE).size()/ 25F;
                }
                return config.getPointsPerFish().get(tag) * fish.getCount();
            }
        }
        return 1F;
    }
}

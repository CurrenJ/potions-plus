package grill24.potionsplus.data;

import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static grill24.potionsplus.utility.Utility.ppId;

public class BiomeTagProvider extends TagsProvider<Biome> {
    // TODO: Appropriate tags for other biomes
    TagKey<Biome> VOLCANIC_CAVE = tag(Biomes.VOLCANIC_CAVE_KEY);
    TagKey<Biome> ICE_CAVE = tag(Biomes.ICE_CAVE_KEY);
    TagKey<Biome> ARID_CAVE = tag(Biomes.ARID_CAVE_KEY);

    protected BiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> holderProvider,
                                    ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, holderProvider, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(VOLCANIC_CAVE).addTag(Tags.Biomes.IS_UNDERGROUND).addTag(Tags.Biomes.IS_HOT);
        tag(ICE_CAVE).addTag(Tags.Biomes.IS_UNDERGROUND).addTag(Tags.Biomes.IS_COLD).addTag(Tags.Biomes.IS_COLD_OVERWORLD);
        tag(ARID_CAVE).addTag(Tags.Biomes.IS_UNDERGROUND).addTag(Tags.Biomes.IS_DRY);
    }

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, ppId(name));
    }

    private static TagKey<Biome> tag(ResourceKey<Biome> key) {
        return TagKey.create(Registries.BIOME, key.location());
    }
}


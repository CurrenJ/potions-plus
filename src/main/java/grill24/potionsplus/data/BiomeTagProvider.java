package grill24.potionsplus.data;

import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends TagsProvider<Biome> {
    TagKey<Biome> VOLCANIC_CAVE = tag(Biomes.VOLCANIC_CAVE_KEY);

    protected BiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> holderProvider,
                                    ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, holderProvider, ModInfo.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(VOLCANIC_CAVE).addTag(Tags.Biomes.IS_UNDERGROUND).addTag(Tags.Biomes.IS_HOT);
    }

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(ModInfo.MOD_ID, name));
    }

    private static TagKey<Biome> tag(ResourceKey<Biome> key) {
        return TagKey.create(Registries.BIOME, key.location());
    }
}


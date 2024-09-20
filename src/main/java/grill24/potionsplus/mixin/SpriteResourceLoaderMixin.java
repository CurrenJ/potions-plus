package grill24.potionsplus.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(SpriteResourceLoader.class)
public abstract class SpriteResourceLoaderMixin {
    @Shadow @Final private static FileToIdConverter ATLAS_INFO_CONVERTER;

    @Unique
    private static final ResourceLocation BLOCKS_ATLAS = new ResourceLocation("blocks");
    @Unique
    private static final ResourceLocation MOB_EFFECTS_ATLAS = new ResourceLocation(ModInfo.MOD_ID, "add_to_blocks");

    /**
     * Injects additional texture sources into the blocks atlas.
     * Sources are read from the mob_effects atlas file in the mod's resources.
     * This is necessary because the mob_effects textures are not available to item models by default in 1.20.
     */
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void load2(ResourceManager resourceManager, ResourceLocation resourceLocation, CallbackInfoReturnable<SpriteResourceLoader> cir, ResourceLocation resourcelocation, List list, Iterator var4, Resource resource, BufferedReader bufferedreader, Dynamic dynamic) {
        if(resourceLocation.equals(BLOCKS_ATLAS)) {
            ResourceLocation atlasLocation = ATLAS_INFO_CONVERTER.idToFile(MOB_EFFECTS_ATLAS);
            for(Resource res : resourceManager.getResourceStack(atlasLocation)) {
                try (BufferedReader buf = res.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(buf);
                    com.mojang.serialization.Dynamic<JsonElement> dyn = new Dynamic<>(JsonOps.INSTANCE, jsonElement);
                    list.addAll(SpriteSources.FILE_CODEC.parse(dyn).getOrThrow(false, LOGGER::error));
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse atlas definition {} in pack {}", resourcelocation, resource.sourcePackId(), exception);
                }
            }
        }
    }
}

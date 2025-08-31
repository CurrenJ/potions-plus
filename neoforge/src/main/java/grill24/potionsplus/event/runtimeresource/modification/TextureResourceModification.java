package grill24.potionsplus.event.runtimeresource.modification;

import com.mojang.blaze3d.platform.NativeImage;
import grill24.potionsplus.utility.FakePngResource;
import grill24.potionsplus.utility.RUtil;
import grill24.potionsplus.utility.ResourceUtility;
import grill24.potionsplus.utility.cache.TextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class TextureResourceModification implements IResourceModification {
    protected final ResourceLocation targetResourceLocation;
    protected final ResourceLocation newResourceLocation;
    protected final Supplier<BufferedImage> textureGenerator;

    public TextureResourceModification(ResourceLocation targetResourceLocation, ResourceLocation newResourceLocation, Supplier<BufferedImage> textureGenerator) {
        this.targetResourceLocation = targetResourceLocation;
        this.newResourceLocation = newResourceLocation;
        this.textureGenerator = textureGenerator;
    }

    public static class OverlayImage {
        private final ResourceLocation overlayTextureLongId;
        private final RUtil.BlendMode blendMode;

        public OverlayImage(ResourceLocation overlayTextureLongId, RUtil.BlendMode blendMode) {
            this.overlayTextureLongId = overlayTextureLongId;
            this.blendMode = blendMode;
        }

        public OverlayImage(ResourceLocation overlayTextureLongId) {
            this(overlayTextureLongId, RUtil.BlendMode.DEFAULT);
        }

        public ResourceLocation getOverlayTextureLongId() {
            return overlayTextureLongId;
        }

        public RUtil.BlendMode getBlendMode() {
            return blendMode;
        }
    }

    public static Supplier<BufferedImage> overlay(ResourceLocation baseTextureLongId, OverlayImage... overlayImages) {
        return () -> {
            // Create overlay specifications for caching
            TextureCache.OverlaySpec[] overlaySpecs = new TextureCache.OverlaySpec[overlayImages.length];
            for (int i = 0; i < overlayImages.length; i++) {
                overlaySpecs[i] = new TextureCache.OverlaySpec(
                    overlayImages[i].getOverlayTextureLongId(), 
                    overlayImages[i].getBlendMode().name()
                );
            }
            
            // Try to get from cache first
            return TextureCache.getOrCreateOverlayResult(baseTextureLongId, overlaySpecs, () -> {
                Optional<Resource> baseTextureResource = ResourceUtility.getResource(baseTextureLongId);
                if (baseTextureResource.isEmpty()) {
                    return createBufferedImageCached(ppId("textures/item/unknown.png"), 
                        ResourceUtility.getResource(ppId("textures/item/unknown.png")).get());
                }
                BufferedImage baseImage = createBufferedImageCached(baseTextureLongId, baseTextureResource.get());

                for (OverlayImage overlay : overlayImages) {
                    Optional<Resource> overlayTextureResource = ResourceUtility.getResource(overlay.getOverlayTextureLongId());
                    if (overlayTextureResource.isEmpty()) {
                        continue;
                    }
                    BufferedImage overlayImage = createBufferedImageCached(overlay.getOverlayTextureLongId(), overlayTextureResource.get());

                    // Draw the overlay image onto the base image
                    for (int x = 0; x < baseImage.getWidth() && x < overlayImage.getWidth(); x++) {
                        for (int y = 0; y < baseImage.getHeight() && y < overlayImage.getHeight(); y++) {
                            int color = overlayImage.getRGB(x, y);
                            // Check that the pixel is not fully transparent
                            int alpha = (color >> 24) & 0xFF;
                            if (alpha != 0) {
                                // Blend the color with the base image
                                int baseColor = baseImage.getRGB(x, y);
                                int blendedColor = RUtil.blendColors(color, baseColor, overlay.getBlendMode());
                                baseImage.setRGB(x, y, blendedColor);
                            }
                        }
                    }
                }

                return baseImage;
            });
        };
    }

    @Override
    public ResourceLocation getTargetResourceLocation() {
        return targetResourceLocation;
    }

    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResourceLocation;
    }

    @Override
    public List<Resource> generateResourceStack() {
        return List.of();
    }

    @Override
    public Optional<Resource> generateResource() {
        // Track the new resource for selective reload optimization
        grill24.potionsplus.utility.performance.SelectiveResourceReloadUtility.trackModifiedResource(newResourceLocation);
        
        Optional<Resource> targetResource = ResourceUtility.getResource(targetResourceLocation);
        return targetResource.map(r -> {
            BufferedImage image = textureGenerator.get();
            if (image == null) {
                return null;
            }
            return new FakePngResource(r, image);
        });
    }

    private static BufferedImage createBufferedImageCached(ResourceLocation location, Resource resource) {
        return TextureCache.getOrCreateBufferedImage(location, resource, TextureResourceModification::createBufferedImage);
    }

    private static BufferedImage createBufferedImage(Resource resource) {
        NativeImage nativeimage;
        try (InputStream inputstream = resource.open()) {
            nativeimage = NativeImage.read(inputstream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // NativeImage to BufferedImage
        BufferedImage bufferedImage = new BufferedImage(nativeimage.getWidth(), nativeimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < nativeimage.getWidth(); x++) {
            for (int y = 0; y < nativeimage.getHeight(); y++) {
                int color = nativeimage.getPixel(x, y);
                bufferedImage.setRGB(x, y, color);
            }
        }

        return bufferedImage;
    }

    @Override
    public String toString() {
        return "TextureResourceModification{" +
                "targetResourceLocation=" + targetResourceLocation +
                ", newResourceLocation=" + newResourceLocation +
                '}';
    }
}

package grill24.potionsplus.event.resources;

import com.mojang.blaze3d.platform.NativeImage;
import grill24.potionsplus.utility.FakePngResource;
import grill24.potionsplus.utility.FakeResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.mc;

public class TextureResourceModification implements IResourceModification {
    protected final ResourceLocation targetResourceLocation;
    protected final ResourceLocation newResourceLocation;

    public TextureResourceModification(ResourceLocation targetResourceLocation, ResourceLocation newResourceLocation) {
        this.newResourceLocation = newResourceLocation;
        this.targetResourceLocation = targetResourceLocation;
    }

    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResourceLocation;
    }

    @Override
    public List<Resource> generateResourceStack(ClientModifyFileResourceStackEvent event, ResourceManager resourceManager) {
        return List.of();
    }

    @Override
    public Optional<Resource> generateResource(ClientModifyFileResourcesEvent event, ResourceManager resourceManager) {
        ResourceLocation rl = mc("textures/item/golden_carrot.png");
        Optional<Resource> goldenCarrotResource = resourceManager.getResource(rl);
        if (goldenCarrotResource.isEmpty()) {
            return Optional.empty();
        }

        Optional<Resource> targetResource = Optional.ofNullable(event.getResources().get(targetResourceLocation));
        if (targetResource.isEmpty()) {
            return Optional.empty();
        }


        BufferedImage targetImage = createBufferedImage(targetResource.get());
        BufferedImage goldenCarrotImage = createBufferedImage(goldenCarrotResource.get());

        // Draw the golden carrot image onto the target image
        for (int x = 0; x < targetImage.getWidth(); x++) {
            for (int y = 0; y < targetImage.getHeight(); y++) {
                int color = goldenCarrotImage.getRGB(x, y);
                // alpha threshold
                if ((color >> 24) == 0x00) {
                    continue;
                }
                targetImage.setRGB(x, y, color);
            }
        }

        // Create a new resource with the modified image
        FakeResource fakeResource = new FakePngResource(targetResource.get(), targetImage);
        return Optional.of(fakeResource);
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
                int color = nativeimage.getPixelRGBA(x, y);
                // Convert RGBA to ARGB
                int alpha = (color >> 24) & 0xFF;
                int blue = (color >> 16) & 0xFF;
                int green = (color >> 8) & 0xFF;
                int red = color & 0xFF;

                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;


                bufferedImage.setRGB(x, y, argb);
            }
        }

        return bufferedImage;
    }
}

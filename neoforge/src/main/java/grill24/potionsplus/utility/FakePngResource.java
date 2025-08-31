package grill24.potionsplus.utility;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.utility.cache.TextureCache;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FakePngResource extends FakeResource {
    private final BufferedImage image;

    public FakePngResource(Resource base, BufferedImage image) {
        super(base, s -> s); // Pass null for base resource since we're mocking
        this.image = image;
    }

    public FakePngResource(Resource base) {
        this(base, generateDebugImage());
    }

    @Override
    public InputStream open() throws IOException {
        // Use cache to avoid repeated PNG encoding
        byte[] pngBytes = TextureCache.getOrCreatePngBytes(image, img -> {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(img, "png", outputStream);
                return outputStream.toByteArray();
            } catch (IOException e) {
                PotionsPlus.LOGGER.error("Failed to encode BufferedImage to PNG", e);
                return null;
            }
        });

        if (pngBytes == null) {
            throw new IOException("Failed to encode BufferedImage to PNG bytes");
        }

        if (Debug.DEBUG_RUNTIME_RESOURCE_INJECTION) {
            PotionsPlus.LOGGER.info("FakePngResource: {}", image);
        }

        // Return an InputStream from the cached byte array
        return new ByteArrayInputStream(pngBytes);
    }

    private static BufferedImage generateDebugImage() {
        // Load image from base resource

        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                image.setRGB(x, y, (x + y) % 2 == 0 ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }
}

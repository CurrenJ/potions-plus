package grill24.potionsplus.utility;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FakePngResource extends FakeResource {
    private final BufferedImage image;
    private volatile byte[] cachedPngData;

    public FakePngResource(Resource base, BufferedImage image) {
        super(base, s -> s); // Pass null for base resource since we're mocking
        this.image = image;
    }

    public FakePngResource(Resource base) {
        this(base, generateDebugImage());
    }

    @Override
    public InputStream open() throws IOException {
        // Use cached PNG data if available, otherwise generate and cache it
        if (cachedPngData == null) {
            synchronized (this) {
                if (cachedPngData == null) {
                    // Write the provided image to a ByteArrayOutputStream
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", outputStream);
                    cachedPngData = outputStream.toByteArray();
                    
                    PotionsPlus.LOGGER.info("FakePngResource: Generated and cached PNG data for {}", image);
                }
            }
        }

        // Return an InputStream from the cached byte array
        return new ByteArrayInputStream(cachedPngData);
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

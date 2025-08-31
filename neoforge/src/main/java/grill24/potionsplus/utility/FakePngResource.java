package grill24.potionsplus.utility;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

public class FakePngResource extends FakeResource {
    private final BufferedImage image;
    
    // Static global cache for PNG byte arrays, keyed by resource location + image hash
    private static final ConcurrentHashMap<String, byte[]> PNG_CACHE = new ConcurrentHashMap<>();

    public FakePngResource(Resource base, BufferedImage image) {
        super(base, s -> s); // Pass null for base resource since we're mocking
        this.image = image;
    }

    public FakePngResource(Resource base) {
        this(base, generateDebugImage());
    }

    @Override
    public InputStream open() throws IOException {
        // Create a cache key based on resource location and image characteristics
        String cacheKey = source().toString() + "_" + getImageHash(image);
        
        // Check if we have cached PNG data for this resource
        byte[] pngData = PNG_CACHE.get(cacheKey);
        
        if (pngData == null) {
            // Generate PNG data and cache it
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            pngData = outputStream.toByteArray();
            
            // Cache the result for future use
            PNG_CACHE.put(cacheKey, pngData);
            
            PotionsPlus.LOGGER.debug("FakePngResource: Generated and cached PNG for {}", cacheKey);
        } else {
            PotionsPlus.LOGGER.debug("FakePngResource: Using cached PNG for {}", cacheKey);
        }

        // Return a new InputStream from the cached byte array
        return new ByteArrayInputStream(pngData);
    }

    /**
     * Generate a simple hash for the image to use in cache keys
     */
    private String getImageHash(BufferedImage image) {
        // Use image dimensions and first few pixels for a simple hash
        int hash = image.getWidth() * 31 + image.getHeight();
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            hash = hash * 31 + image.getRGB(0, 0);
            if (image.getWidth() > 1) {
                hash = hash * 31 + image.getRGB(Math.min(1, image.getWidth() - 1), 0);
            }
            if (image.getHeight() > 1) {
                hash = hash * 31 + image.getRGB(0, Math.min(1, image.getHeight() - 1));
            }
        }
        return String.valueOf(Math.abs(hash));
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

package grill24.potionsplus.utility;

import grill24.potionsplus.extension.IResourceExtension;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

public class FakeResource extends Resource {
    private final UnaryOperator<String> transformer;
    
    // Static global cache for transformed content, keyed by resource location + transformer hash
    private static final ConcurrentHashMap<String, String> CONTENT_CACHE = new ConcurrentHashMap<>();

    public FakeResource(Resource base, UnaryOperator<String> rawStringTransformer) {
        super(base.source(), ((IResourceExtension) base).potions_plus$getStreamSuppler());
        this.transformer = rawStringTransformer;
    }


    @Override
    public BufferedReader openAsReader() throws IOException {
        // Create a cache key based on resource location and transformer identity
        String cacheKey = source().toString() + "_" + getTransformerHash(transformer);
        
        // Check if we have cached transformed content for this resource+transformer combination
        String transformedContent = CONTENT_CACHE.get(cacheKey);
        
        if (transformedContent == null) {
            // Read and transform the content, then cache it
            Optional<String> string = super.openAsReader().lines()
                    .reduce(String::concat)
                    .map(transformer);

            if (string.isPresent()) {
                transformedContent = string.get();
                // Cache the result for future use
                CONTENT_CACHE.put(cacheKey, transformedContent);
            } else {
                throw new IOException("Failed to load FakeResource.");
            }
        }

        // Return a new BufferedReader from the cached string
        return new BufferedReader(new StringReader(transformedContent));
    }
    
    /**
     * Generate a simple hash for the transformer to use in cache keys
     */
    private String getTransformerHash(UnaryOperator<String> transformer) {
        // Use the transformer's identity hash code as a simple key
        return String.valueOf(Math.abs(System.identityHashCode(transformer)));
    }
}

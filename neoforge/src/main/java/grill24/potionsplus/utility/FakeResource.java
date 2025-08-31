package grill24.potionsplus.utility;

import grill24.potionsplus.extension.IResourceExtension;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class FakeResource extends Resource {
    private final UnaryOperator<String> transformer;
    private volatile String cachedTransformedContent;

    public FakeResource(Resource base, UnaryOperator<String> rawStringTransformer) {
        super(base.source(), ((IResourceExtension) base).potions_plus$getStreamSuppler());
        this.transformer = rawStringTransformer;
    }


    @Override
    public BufferedReader openAsReader() throws IOException {
        // Use cached transformed content if available, otherwise generate and cache it
        if (cachedTransformedContent == null) {
            synchronized (this) {
                if (cachedTransformedContent == null) {
                    // Read lines from the base resource, pack into a StringReader so we can return a BufferedReader with modified lines
                    Optional<String> string = super.openAsReader().lines()
                            .reduce(String::concat)
                            .map(transformer);

                    if (string.isPresent()) {
                        cachedTransformedContent = string.get();
                    } else {
                        throw new IOException("Failed to load FakeResource.");
                    }
                }
            }
        }

        return new BufferedReader(new StringReader(cachedTransformedContent));
    }
}

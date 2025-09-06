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

    public FakeResource(Resource base, UnaryOperator<String> rawStringTransformer) {
        super(base.source(), ((IResourceExtension) base).potions_plus$getStreamSuppler());
        this.transformer = rawStringTransformer;
    }


    @Override
    public BufferedReader openAsReader() throws IOException {
        // Read lines from the base resource, pack into a StringReader so we can return a BufferedReader with modified lines
        Optional<String> string = super.openAsReader().lines()
                .reduce(String::concat)
                .map(transformer);

        if (string.isPresent()) {
            return new BufferedReader(new StringReader(string.get()));
        } else {
            throw new IOException("Failed to load FakeResource.");
        }
    }
}

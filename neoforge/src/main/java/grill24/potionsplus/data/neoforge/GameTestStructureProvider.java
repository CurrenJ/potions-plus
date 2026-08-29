package grill24.potionsplus.data.neoforge;

import com.google.common.hash.Hashing;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Writes the empty structure templates the game tests run inside.
 *
 * <p>The game test framework only reads the template's size to work out the test bounds, and fills
 * stone in underneath, so an empty template is all a test that places its own blocks needs. Templates
 * have to be binary {@code .nbt} - the resource-pack loader only reads {@code .snbt} from the dev-only
 * test structures directory.
 *
 * <p>Run once with {@code ./gradlew :neoforge:runData}, then commit the output.
 */
public class GameTestStructureProvider implements DataProvider {

    /** MC 26.1.2's world version, from the game's own version.json. */
    private static final int DATA_VERSION = 4790;

    private final PackOutput output;

    public GameTestStructureProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(writeEmptyStructure(cachedOutput, "empty_testarea", 7, 7, 7));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> writeEmptyStructure(
            CachedOutput cachedOutput, String name, int sizeX, int sizeY, int sizeZ) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = toCompressedNbt(buildEmptyStructureTag(sizeX, sizeY, sizeZ));
                Path path = this.output
                        .getOutputFolder(PackOutput.Target.DATA_PACK)
                        .resolve(ModInfo.MOD_ID + "/structure/" + name + ".nbt");
                cachedOutput.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                throw new RuntimeException("Failed to write game test structure " + name, e);
            }
        });
    }

    private static CompoundTag buildEmptyStructureTag(int sizeX, int sizeY, int sizeZ) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", DATA_VERSION);

        ListTag size = new ListTag();
        size.add(IntTag.valueOf(sizeX));
        size.add(IntTag.valueOf(sizeY));
        size.add(IntTag.valueOf(sizeZ));
        tag.put("size", size);

        tag.put("palette", new ListTag());
        tag.put("blocks", new ListTag());
        tag.put("entities", new ListTag());
        return tag;
    }

    private static byte[] toCompressedNbt(CompoundTag tag) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, bytes);
        return bytes.toByteArray();
    }

    @Override
    public String getName() {
        return "Potions Plus Game Test Structures";
    }
}

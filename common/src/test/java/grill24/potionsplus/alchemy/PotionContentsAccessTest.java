package grill24.potionsplus.alchemy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Enforces alchemy invariant 3 from {@code alchemy/package-info.java}: {@code DataComponents
 * .POTION_CONTENTS} is referenced only inside {@code grill24.potionsplus.alchemy}. Everything else reads
 * and writes potion data through {@link PotionData}/{@link PotionDataBuilder}, so a direct component
 * access anywhere else reopens the P-01-era bypass this package exists to close.
 */
class PotionContentsAccessTest {

    private static final String FORBIDDEN = "DataComponents.POTION_CONTENTS";

    @Test
    void potionContentsComponentIsOnlyReferencedInsideAlchemyPackage() throws IOException {
        Path repoRoot = findRepoRoot();
        List<Path> sourceRoots = List.of(
                repoRoot.resolve("common/src/main/java"),
                repoRoot.resolve("neoforge/src/main/java")
        );

        List<String> violations = new ArrayList<>();
        for (Path sourceRoot : sourceRoots) {
            if (!Files.isDirectory(sourceRoot)) {
                continue;
            }
            try (Stream<Path> files = Files.walk(sourceRoot)) {
                files.filter(path -> path.toString().endsWith(".java"))
                        .filter(path -> !isInAlchemyPackage(path))
                        .forEach(path -> {
                            try {
                                List<String> lines = Files.readAllLines(path);
                                for (int i = 0; i < lines.size(); i++) {
                                    if (lines.get(i).contains(FORBIDDEN)) {
                                        violations.add(path + ":" + (i + 1));
                                    }
                                }
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            }
        }

        assertTrue(violations.isEmpty(),
                "DataComponents.POTION_CONTENTS must only be referenced inside grill24.potionsplus.alchemy, "
                        + "but found direct access at:\n" + String.join("\n", violations));
    }

    private static boolean isInAlchemyPackage(Path path) {
        return path.toString().replace('\\', '/').contains("/grill24/potionsplus/alchemy/");
    }

    private static Path findRepoRoot() {
        Path dir = Path.of("").toAbsolutePath();
        while (dir != null) {
            if (Files.exists(dir.resolve("settings.gradle"))) {
                return dir;
            }
            dir = dir.getParent();
        }
        throw new IllegalStateException("Could not locate repo root (no settings.gradle found above " + Path.of("").toAbsolutePath() + ")");
    }
}

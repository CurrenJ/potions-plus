package grill24.potionsplus.config.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fabric has no stock server-config system (unlike NeoForge/Forge's spec builders), so this hand-rolls
 * a JSON file in the config directory - the same approach apt-ores uses on Fabric.
 */
public class PotionsPlusConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("potionsplus-config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final PotionsPlusConfig CONFIG = load();

    public int potionDrinkTimeTicks = 16;
    public int potionDrinkCooldownTimeTicks = 0;

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("potionsplus.json");
    }

    private static PotionsPlusConfig load() {
        Path path = configPath();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                PotionsPlusConfig config = GSON.fromJson(reader, PotionsPlusConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to read {}, regenerating with defaults", path, e);
            }
        }

        PotionsPlusConfig config = new PotionsPlusConfig();
        config.save();
        return config;
    }

    public void save() {
        Path path = configPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to write {}", path, e);
        }
    }
}

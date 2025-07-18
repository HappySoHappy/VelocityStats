package me.howandev.velocitystats.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigurationHandler {
    private final Path configPath;
    private ConfigurationNode rootNode;
    private final YamlConfigurationLoader loader;

    public ConfigurationHandler(Path dataDirectory) {
        this.configPath = dataDirectory.resolve("config.yml");

        this.loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build();
    }

    public void loadOrCreate() throws IOException {
        if (Files.notExists(configPath)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in == null) {
                    throw new IOException("Unable to read config.yml template from resources!");
                }

                Files.createDirectories(configPath.getParent());
                Files.copy(in, configPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        this.rootNode = loader.load();
    }

    public void save() throws IOException {
        loader.save(rootNode);
    }

    public ConfigurationNode get() {
        return rootNode;
    }
}

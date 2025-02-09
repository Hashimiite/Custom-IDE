package src.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigManager {
    private static final String CONFIG_FILE = "editor_config.properties";
    private Properties properties;
    private Path configPath;

    public ConfigManager() {
        properties = new Properties();
        configPath = Path.of(System.getProperty("user.home"), ".codeeditor", CONFIG_FILE);
        loadConfig();
    }

    private void loadConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    properties.load(in);
                }
            } else {
                setDefaults();
                saveConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("theme", "light");
        properties.setProperty("font.size", "14");
        properties.setProperty("font.family", "Monospaced");
        properties.setProperty("tab.size", "4");
        properties.setProperty("auto.save", "true");
        properties.setProperty("line.numbers", "true");
    }

    public void saveConfig() {
        try {
            try (OutputStream out = Files.newOutputStream(configPath)) {
                properties.store(out, "Code Editor Configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }
}
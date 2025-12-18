package peppermint.config;

import peppermint.themes.ThemeManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages application configuration, specifically for storing and retrieving theme and theme mode settings.
 */
public class ConfigManager {
    private static final String CONFIG_FILE_PATH = "config.toml";
    private String currentTheme;
    private String currentThemeMode;

    /**
     * Initializes the ConfigManager and loads settings from the config file.
     */
    public ConfigManager() {
        loadConfig();
    }

    /**
     * Loads configuration from config.toml file.
     */
    public void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE_PATH);
        
        if (!Files.exists(configPath)) {
            // Create default config file
            createDefaultConfig();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String line;
            boolean inMainSection = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Check for section header [main]
                if (line.equals("[main]")) {
                    inMainSection = true;
                    continue;
                }
                
                // Skip if not in main section or if it's a comment
                if (!inMainSection || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value pairs
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        switch (key) {
                            case "current_theme":
                                this.currentTheme = value;
                                break;
                            case "current_theme_mode":
                                this.currentThemeMode = value;
                                break;
                        }
                    }
                }
            }
            
            // If no values were loaded, set defaults
            if (this.currentTheme == null) {
                this.currentTheme = "default";
            }
            
            if (this.currentThemeMode == null) {
                this.currentThemeMode = "light";
            }
            
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
            // Set defaults on error
            this.currentTheme = "default";
            this.currentThemeMode = "light";
        }
    }

    /**
     * Saves configuration to config.toml file.
     */
    public void saveConfig() {
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(CONFIG_FILE_PATH)))) {
            
            writer.println("# Application Configuration File");
            writer.println("[main]");
            writer.println("current_theme = \"" + this.currentTheme + "\"");
            writer.println("current_theme_mode = \"" + this.currentThemeMode + "\"");
            
        } catch (IOException e) {
            System.err.println("Error writing config file: " + e.getMessage());
        }
    }

    /**
     * Creates a default configuration file.
     */
    private void createDefaultConfig() {
        this.currentTheme = "default";
        this.currentThemeMode = "light";
        saveConfig(); // This will create the file with default values
    }

    /**
     * Sets the current theme and saves to configuration.
     * @param theme The theme name to set
     */
    public void setCurrentTheme(String theme) {
        this.currentTheme = theme;
        saveConfig();
    }

    /**
     * Sets the current theme mode and saves to configuration.
     * @param themeMode The theme mode to set
     */
    public void setCurrentThemeMode(String themeMode) {
        this.currentThemeMode = themeMode;
        saveConfig();
    }

    /**
     * Gets the current theme from configuration.
     * @return The current theme name
     */
    public String getCurrentTheme() {
        return this.currentTheme;
    }

    /**
     * Gets the current theme mode from configuration.
     * @return The current theme mode
     */
    public String getCurrentThemeMode() {
        return this.currentThemeMode;
    }
}
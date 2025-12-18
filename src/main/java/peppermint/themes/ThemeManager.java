package peppermint.themes;

import peppermint.config.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

// Import for XZ decompression and embedded resources
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class ThemeManager {
    private static final String DEFAULT_THEME = "default";
    private static final String DEFAULT_THEME_MODE = "light";

    private String currentTheme;
    private String currentThemeMode;
    private ConfigManager configManager;

    public ThemeManager() {
        this.configManager = new ConfigManager();
        this.currentTheme = configManager.getCurrentTheme();
        this.currentThemeMode = configManager.getCurrentThemeMode();

        // Ensure that the example theme exists in the themes directory
        ensureExampleThemeExists();
    }

    /**
     * Ensures the example theme exists in the themes directory
     */
    private void ensureExampleThemeExists() {
        Path themesDir = Paths.get("PepperMintThemes");
        Path exampleThemeDir = themesDir.resolve("example-theme");

        try {
            // Create themes directory if it doesn't exist
            Files.createDirectories(themesDir);

            // If the example theme doesn't exist, extract it from embedded resources
            if (!Files.exists(exampleThemeDir)) {
                System.out.println("Extracting example theme from embedded resources...");
                extractEmbeddedExampleTheme();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts the example theme from embedded resources
     */
    private void extractEmbeddedExampleTheme() {
        try {
            // Create the example theme directory
            Path themesDir = Paths.get("PepperMintThemes");
            Path exampleThemeDir = themesDir.resolve("example-theme");
            Files.createDirectories(exampleThemeDir);

            // Copy embedded theme files
            copyEmbeddedResourceToFile("/themedata/theme.toml", exampleThemeDir.resolve("theme.toml"));
            copyEmbeddedResourceToFile("/themedata/mani.toml", exampleThemeDir.resolve("mani.toml"));

            System.out.println("Example theme extracted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies an embedded resource to a file
     * @param resourcePath Path of the embedded resource
     * @param targetFile Target file to write to
     */
    private void copyEmbeddedResourceToFile(String resourcePath, Path targetFile) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            // Try alternative path format
            inputStream = ThemeManager.class.getClassLoader().getResourceAsStream(resourcePath.replaceFirst("/", ""));
            if (inputStream == null) {
                // Fallback: copy from the example-theme directory if it exists
                System.err.println("Embedded resource not found: " + resourcePath);
                Path exampleThemePath = Paths.get("example-theme").resolve(targetFile.getFileName().toString());
                if (Files.exists(exampleThemePath)) {
                    Files.copy(exampleThemePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied from example-theme directory: " + targetFile.getFileName());
                    return;
                } else {
                    throw new IOException("Embedded resource not found: " + resourcePath);
                }
            }
        }

        try (InputStream finalInputStream = inputStream; OutputStream outputStream = Files.newOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = finalInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Creates a new theme based on the example theme template
     * @param newThemeName Name of the new theme to create
     * @return true if the theme was created successfully, false otherwise
     */
    public boolean createNewTheme(String newThemeName) {
        try {
            // Validate theme name to prevent directory traversal attacks
            if (newThemeName.contains("..") || newThemeName.contains("/")) {
                throw new IllegalArgumentException("Invalid theme name: " + newThemeName);
            }

            Path themesDir = Paths.get("PepperMintThemes");
            Path newThemeDir = themesDir.resolve(newThemeName);

            // Check if theme already exists
            if (Files.exists(newThemeDir)) {
                System.out.println("Theme already exists: " + newThemeName);
                return false;
            }

            // Create the new theme directory
            Files.createDirectories(newThemeDir);

            // Copy example theme files to the new theme directory
            Path exampleThemeDir = themesDir.resolve("example-theme");

            if (Files.exists(exampleThemeDir)) {
                // Copy theme.toml and mani.toml from example theme
                Files.copy(exampleThemeDir.resolve("theme.toml"), newThemeDir.resolve("theme.toml"),
                          StandardCopyOption.REPLACE_EXISTING);
                Files.copy(exampleThemeDir.resolve("mani.toml"), newThemeDir.resolve("mani.toml"),
                          StandardCopyOption.REPLACE_EXISTING);

                // Update the theme.toml file with the new theme name
                updateThemeConfig(newThemeDir, newThemeName);

                System.out.println("New theme created: " + newThemeName);
                return true;
            } else {
                // If example theme doesn't exist, try to extract from embedded resources
                System.out.println("Example theme not found, extracting from embedded resources...");
                extractEmbeddedExampleTheme();

                // Retry copying after extraction
                if (Files.exists(exampleThemeDir)) {
                    Files.copy(exampleThemeDir.resolve("theme.toml"), newThemeDir.resolve("theme.toml"),
                              StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(exampleThemeDir.resolve("mani.toml"), newThemeDir.resolve("mani.toml"),
                              StandardCopyOption.REPLACE_EXISTING);

                    updateThemeConfig(newThemeDir, newThemeName);

                    System.out.println("New theme created: " + newThemeName);
                    return true;
                } else {
                    System.err.println("Failed to create new theme: example theme not available");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the theme.toml and mani.toml files with the new theme name
     * @param themeDir The directory of the theme to update
     * @param themeName The new theme name
     */
    private void updateThemeConfig(Path themeDir, String themeName) throws IOException {
        // Update theme.toml
        Path themeTomlPath = themeDir.resolve("theme.toml");
        String themeTomlContent = Files.readString(themeTomlPath);
        themeTomlContent = themeTomlContent.replace("Gameboy Green (Dark)", themeName);
        Files.writeString(themeTomlPath, themeTomlContent);

        // Update mani.toml
        Path maniTomlPath = themeDir.resolve("mani.toml");
        String maniTomlContent = Files.readString(maniTomlPath);
        maniTomlContent = maniTomlContent.replace("Gameboy Green (Dark)", themeName);
        Files.writeString(maniTomlPath, maniTomlContent);
    }

    /**
     * Applies the currently selected theme
     */
    public void applyCurrentTheme() {
        try {
            // First check if it's a custom theme
            if (!"default".equals(currentTheme) && !"light".equals(currentTheme) && !"dark".equals(currentTheme)) {
                // Validate that the custom theme exists before trying to load it
                if (isCustomThemeValid(currentTheme)) {
                    // Try to load a custom theme from the themes directory
                    if (loadCustomTheme(currentTheme)) {
                        return; // Custom theme loaded successfully
                    }
                }
                // If custom theme doesn't exist or fails to load, fall back to theme mode
                System.out.println("Warning: Custom theme '" + currentTheme + "' not found or invalid, falling back to theme mode: " + currentThemeMode);
            }

            // Apply theme based on theme mode if available
            if ("dark".equals(currentThemeMode)) {
                // Apply FlatLaf Dark theme if available
                try {
                    Class<?> flatDarkLafClass = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
                    Object laf = flatDarkLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
                } catch (Exception ex) {
                    // FlatLaf not available, continue with default L&F
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } else {
                // Apply FlatLaf Light theme if available (default/fallback)
                try {
                    Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                    Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
                } catch (Exception ex) {
                    // FlatLaf not available, continue with default L&F
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // On error, use system default as fallback
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Checks if a custom theme exists and is valid
     * @param themeName The name of the theme to validate
     * @return true if the theme exists and is valid, false otherwise
     */
    private boolean isCustomThemeValid(String themeName) {
        if (themeName == null) {
            return false;
        }

        try {
            Path themesDir = Paths.get("PepperMintThemes");
            Path themePath = themesDir.resolve(themeName + ".pmt");
            Path themeDir = themesDir.resolve(themeName);

            // Check if it's a .pmt file
            if (Files.exists(themePath)) {
                return true;
            }

            // Check if it's a theme directory with required files
            if (Files.exists(themeDir) && Files.isDirectory(themeDir)) {
                Path themeToml = themeDir.resolve("theme.toml");
                return Files.exists(themeToml);
            }

            return false;
        } catch (Exception e) {
            // If there's an error checking for the theme, log it and return false
            System.out.println("Error validating custom theme '" + themeName + "': " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads a custom theme from the PepperMintThemes directory
     * @param themeName Name of the theme directory or .pmt file
     * @return true if theme was loaded successfully, false otherwise
     */
    private boolean loadCustomTheme(String themeName) {
        Path themesDir = Paths.get("PepperMintThemes");
        Path themePath = themesDir.resolve(themeName + ".pmt");
        Path themeDir = themesDir.resolve(themeName);

        // First, check if it's a .pmt file
        if (Files.exists(themePath)) {
            try {
                // Extract the theme package if it's a .pmt file
                extractThemePackage(themePath);

                // For simplicity in this implementation, we'll just check for a theme.toml file
                Path themeToml = themesDir.resolve(themeName).resolve("theme.toml");

                if (Files.exists(themeToml)) {
                    // Parse theme.toml and apply customizations
                    applyThemeFromToml(themeToml);
                    return true;
                }
            } catch (NoClassDefFoundError e) {
                // Handle missing dependencies gracefully
                System.out.println("Missing dependencies for theme package extraction: " + e.getMessage());
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // Then, check if it's a theme directory
        else if (Files.exists(themeDir) && Files.isDirectory(themeDir)) {
            try {
                // Check for theme.toml file directly in the directory
                Path themeToml = themeDir.resolve("theme.toml");

                if (Files.exists(themeToml)) {
                    // Parse theme.toml and apply customizations
                    applyThemeFromToml(themeToml);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }
    
    /**
     * Extracts a theme package (.pmt file)
     * @param packagePath Path to the .pmt file
     */
    private void extractThemePackage(Path packagePath) throws IOException {
        String themeName = packagePath.getFileName().toString();
        themeName = themeName.substring(0, themeName.length() - 4); // Remove .pmt extension

        Path extractDir = Paths.get("PepperMintThemes").resolve(themeName);

        // Clean up any existing extracted files
        if (Files.exists(extractDir)) {
            deleteRecursively(extractDir);
        }

        // Create directory if it doesn't exist
        Files.createDirectories(extractDir);

        // Extract the XZ-compressed tarball
        try (FileInputStream fileIn = new FileInputStream(packagePath.toFile());
             XZCompressorInputStream xzIn = new XZCompressorInputStream(fileIn);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(xzIn)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                Path entryPath = extractDir.resolve(entry.getName()).normalize();

                // Security check to prevent path traversal
                if (!entryPath.startsWith(extractDir)) {
                    throw new IOException("Entry is outside of target directory: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    // Create parent directories if they don't exist
                    Files.createDirectories(entryPath.getParent());
                    // Copy the file content
                    try (FileOutputStream fileOut = new FileOutputStream(entryPath.toFile())) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = tarIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively deletes a directory and its contents
     * @param path Path to the directory or file to delete
     */
    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walk(path)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        // Ignore errors during cleanup
                    }
                });
        } else {
            Files.delete(path);
        }
    }
    
    /**
     * Applies theme settings from theme.toml file
     * @param themeToml Path to the theme.toml file
     */
    private void applyThemeFromToml(Path themeToml) throws IOException {
        // Read the content of theme.toml
        String content = Files.readString(themeToml);

        // In a real implementation, this would parse the TOML file and apply theme settings
        // For this prototype, we'll implement a basic theme application

        // Example: Check for dark mode setting in the TOML content
        if (content.contains("dark_mode = true") || content.toLowerCase().contains("dark")) {
            // Apply FlatLaf Dark theme
            try {
                Class<?> flatDarkLafClass = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
                Object laf = flatDarkLafClass.getDeclaredConstructor().newInstance();
                UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to light theme
                try {
                    Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                    Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            // Apply FlatLaf Light theme
            try {
                Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Switches to dark theme using FlatLaf
     */
    public void setDarkTheme() {
        try {
            Class<?> flatDarkLafClass = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            Object laf = flatDarkLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            setCurrentTheme("dark");
            setCurrentThemeMode("dark");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches to light theme using FlatLaf
     */
    public void setLightTheme() {
        try {
            Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            setCurrentTheme("light");
            setCurrentThemeMode("light");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the current theme
     * @param themeName Name of the theme to set as current
     */
    public void setCurrentTheme(String themeName) {
        this.currentTheme = themeName;
        configManager.setCurrentTheme(themeName);
    }
    
    /**
     * Gets the name of the currently selected theme
     * @return Name of the current theme
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets the current theme mode
     * @param themeMode Mode of the theme to set as current (light/dark)
     */
    public void setCurrentThemeMode(String themeMode) {
        this.currentThemeMode = themeMode;
        configManager.setCurrentThemeMode(themeMode);
    }

    /**
     * Gets the current theme mode
     * @return Current theme mode (light/dark)
     */
    public String getCurrentThemeMode() {
        return currentThemeMode;
    }
    
    /**
     * Lists all available custom themes in the themes directory
     * @return Array of theme names
     */
    public String[] getAvailableThemes() {
        Path themesDir = Paths.get("PepperMintThemes");

        try {
            if (!Files.exists(themesDir)) {
                Files.createDirectories(themesDir);
            }

            // Look for .pmt files and directories in the themes folder
            return Files.list(themesDir)
                    .filter(path -> !path.getFileName().toString().equals("example-theme") && // Exclude example-theme from available themes
                                  (path.toString().endsWith(".pmt") || Files.isDirectory(path)))
                    .map(path -> {
                        String name = path.getFileName().toString();
                        if (name.endsWith(".pmt")) {
                            name = name.substring(0, name.length() - 4);
                        }
                        return name;
                    })
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[]{DEFAULT_THEME};
        }
    }

    /**
     * Gets the configuration manager associated with this theme manager
     * @return The ConfigManager instance
     */
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}
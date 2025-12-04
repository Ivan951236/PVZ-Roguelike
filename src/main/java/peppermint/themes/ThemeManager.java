package peppermint.themes;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.prefs.Preferences;

// Import for XZ decompression
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class ThemeManager {
    private static final String THEME_PREFS_NODE = "peppermint/themes";
    private static final String CURRENT_THEME_KEY = "currentTheme";
    private static final String DEFAULT_THEME = "default";

    private String currentTheme;
    private Preferences prefs;

    public ThemeManager() {
        this.prefs = Preferences.userRoot().node(THEME_PREFS_NODE);
        this.currentTheme = prefs.get(CURRENT_THEME_KEY, DEFAULT_THEME);
    }

    /**
     * Applies the currently selected theme
     */
    public void applyCurrentTheme() {
        try {
            if ("default".equals(currentTheme) || "light".equals(currentTheme)) {
                // Apply FlatLaf Light theme if available
                try {
                    Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                    Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                    UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
                } catch (Exception ex) {
                    // FlatLaf not available, continue with default L&F
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } else if ("dark".equals(currentTheme)) {
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
                // Try to load a custom theme from the themes directory
                if (!loadCustomTheme(currentTheme)) {
                    // If custom theme fails, fall back to FlatLaf Light theme if available
                    try {
                        Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                        Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                        UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
                    } catch (Exception ex) {
                        // FlatLaf not available, continue with default L&F
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
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
     * Loads a custom theme from the PepperMintThemes directory
     * @param themeName Name of the theme directory or .pmt file
     * @return true if theme was loaded successfully, false otherwise
     */
    private boolean loadCustomTheme(String themeName) {
        Path themesDir = Paths.get("PepperMintThemes");
        Path themePath = themesDir.resolve(themeName + ".pmt");
        
        // Check if theme file exists
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
        prefs.put(CURRENT_THEME_KEY, themeName);
    }
    
    /**
     * Gets the name of the currently selected theme
     * @return Name of the current theme
     */
    public String getCurrentTheme() {
        return currentTheme;
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
                    .filter(path -> path.toString().endsWith(".pmt") || 
                                   Files.isDirectory(path))
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
}
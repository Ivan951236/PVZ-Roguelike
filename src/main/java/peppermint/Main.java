package peppermint;

import peppermint.loader.Loader;
import peppermint.ui.MainWindow;
import peppermint.gens.PresetGenerator;
import peppermint.themes.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Initialize FlatLaf system properties (if available)
        try {
            Class<?> flatSystemPropertiesClass = Class.forName("com.formdev.flatlaf.FlatSystemProperties");
            java.lang.reflect.Method installMethod = flatSystemPropertiesClass.getMethod("install");
            installMethod.invoke(null);
        } catch (Exception e) {
            // FlatLaf not available, continue without system properties
        }

        // Initialize the loader to load all required libraries
        Loader loader = new Loader();
        loader.loadLibraries();

        // Initialize theme manager
        ThemeManager themeManager = new ThemeManager();
        themeManager.applyCurrentTheme();

        // Register shutdown hook to save configuration
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // Save the configuration when the application shuts down
                themeManager.getConfigManager().saveConfig();
                System.out.println("Configuration saved on shutdown.");
            } catch (Exception e) {
                System.err.println("Error saving configuration on shutdown: " + e.getMessage());
            }
        }));

        // Create and show the main window
        SwingUtilities.invokeLater(() -> {
            // Apply the theme again to ensure it's properly set
            themeManager.applyCurrentTheme();

            MainWindow mainWindow = new MainWindow(themeManager);
            mainWindow.setVisible(true);
        });
    }
}
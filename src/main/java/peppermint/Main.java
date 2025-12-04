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

        // Create and show the main window
        SwingUtilities.invokeLater(() -> {
            // Set FlatLaf theme if available
            try {
                Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            } catch (Exception e) {
                // FlatLaf not available, continue with default L&F
            }

            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}
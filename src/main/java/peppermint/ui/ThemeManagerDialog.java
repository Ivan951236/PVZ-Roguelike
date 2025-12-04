package peppermint.ui;

import peppermint.themes.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ThemeManagerDialog extends JDialog {
    private JList<String> themeList;
    private DefaultListModel<String> themeListModel;
    private JButton installThemeButton;
    private JButton removeThemeButton;
    private JButton applyThemeButton;
    private JButton refreshButton;
    private ThemeManager themeManager;

    public ThemeManagerDialog(Frame parent) {
        super(parent, "Theme Manager", true);
        this.themeManager = new ThemeManager();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadThemes();
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        themeListModel = new DefaultListModel<>();
        themeList = new JList<>(themeListModel);
        themeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        installThemeButton = new JButton("Install Theme (.pmt)");
        removeThemeButton = new JButton("Remove Selected");
        applyThemeButton = new JButton("Apply Selected");
        refreshButton = new JButton("Refresh");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(installThemeButton);
        buttonPanel.add(removeThemeButton);
        buttonPanel.add(applyThemeButton);
        buttonPanel.add(refreshButton);

        // Center panel for theme list
        JScrollPane scrollPane = new JScrollPane(themeList);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        add(new JLabel("Available Themes:"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        installThemeButton.addActionListener(new InstallThemeActionListener());
        removeThemeButton.addActionListener(new RemoveThemeActionListener());
        applyThemeButton.addActionListener(new ApplyThemeActionListener());
        refreshButton.addActionListener(e -> loadThemes());
        
        // Enable/disable buttons based on selection
        themeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = themeList.getSelectedIndex() != -1;
                removeThemeButton.setEnabled(hasSelection);
                applyThemeButton.setEnabled(hasSelection);
            }
        });
    }

    private void loadThemes() {
        themeListModel.clear();
        
        // Add default themes
        themeListModel.addElement("default");
        themeListModel.addElement("light");
        themeListModel.addElement("dark");
        
        // Load custom themes
        String[] availableThemes = themeManager.getAvailableThemes();
        for (String theme : availableThemes) {
            if (!"default".equals(theme) && !"light".equals(theme) && !"dark".equals(theme)) {
                themeListModel.addElement(theme);
            }
        }
    }

    private class InstallThemeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Theme Package (.pmt)");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PMT Files", "pmt"));

            int result = fileChooser.showOpenDialog(ThemeManagerDialog.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                try {
                    // Copy the .pmt file to the PepperMintThemes directory
                    Path themesDir = Paths.get("PepperMintThemes");
                    Files.createDirectories(themesDir);
                    
                    Path targetPath = themesDir.resolve(selectedFile.getName());
                    Files.copy(selectedFile.toPath(), targetPath);
                    
                    JOptionPane.showMessageDialog(
                        ThemeManagerDialog.this,
                        "Theme installed successfully!\n" +
                        "Restart the application to see the theme in the list.",
                        "Theme Installed",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    loadThemes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        ThemeManagerDialog.this,
                        "Failed to install theme: " + ex.getMessage(),
                        "Installation Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private class RemoveThemeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedTheme = themeList.getSelectedValue();
            
            if (selectedTheme == null) {
                JOptionPane.showMessageDialog(
                    ThemeManagerDialog.this,
                    "Please select a theme to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // Don't allow removal of default themes
            if ("default".equals(selectedTheme) || "light".equals(selectedTheme) || "dark".equals(selectedTheme)) {
                JOptionPane.showMessageDialog(
                    ThemeManagerDialog.this,
                    "Cannot remove built-in themes.",
                    "Cannot Remove",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(
                ThemeManagerDialog.this,
                "Are you sure you want to remove the theme '" + selectedTheme + "'?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Path themesDir = Paths.get("PepperMintThemes");
                    
                    // Try to delete both the directory and .pmt file
                    Path themeDir = themesDir.resolve(selectedTheme);
                    Path themeFile = themesDir.resolve(selectedTheme + ".pmt");
                    
                    if (Files.exists(themeDir)) {
                        deleteRecursively(themeDir);
                    }
                    
                    if (Files.exists(themeFile)) {
                        Files.delete(themeFile);
                    }
                    
                    loadThemes();
                    JOptionPane.showMessageDialog(
                        ThemeManagerDialog.this,
                        "Theme removed successfully!",
                        "Theme Removed",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        ThemeManagerDialog.this,
                        "Failed to remove theme: " + ex.getMessage(),
                        "Removal Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        
        private void deleteRecursively(Path path) throws java.io.IOException {
            if (Files.isDirectory(path)) {
                java.util.List<Path> children = java.nio.file.Files.walk(path)
                    .sorted(java.util.Comparator.reverseOrder())
                    .toList();
                for (Path child : children) {
                    java.nio.file.Files.delete(child);
                }
            } else {
                java.nio.file.Files.delete(path);
            }
        }
    }

    private class ApplyThemeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedTheme = themeList.getSelectedValue();
            
            if (selectedTheme == null) {
                JOptionPane.showMessageDialog(
                    ThemeManagerDialog.this,
                    "Please select a theme to apply.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            themeManager.setCurrentTheme(selectedTheme);
            
            JOptionPane.showMessageDialog(
                ThemeManagerDialog.this,
                "Theme '" + selectedTheme + "' has been applied!\n" +
                "You may need to restart the application for changes to take effect.",
                "Theme Applied",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
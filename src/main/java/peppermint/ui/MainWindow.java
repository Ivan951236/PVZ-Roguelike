package peppermint.ui;

import peppermint.gens.PresetGenerator;
import peppermint.gens.LevelGenerator;
import peppermint.gens.PatternGenerator;
import peppermint.gens.SeedSlotGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class MainWindow extends JFrame {
    private PresetGenerator presetGenerator;
    private JTextArea outputArea;
    private JComboBox<Integer> levelCountComboBox;
    private JCheckBox endlessModeCheckBox;
    private JComboBox<Integer> seedSlotCountComboBox;
    private JCheckBox patternPerLevelCheckBox;
    private JMenuItem themeGlossaryMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenuItem customThemeCreatorMenuItem;
    private JMenuItem toggleThemeMenuItem;
    private JCheckBoxMenuItem themeToggleCheckBox;
    private JMenuItem manageThemesMenuItem;
    private JPanel levelsPanel;
    private JPanel seedSlotsPanel;

    public MainWindow() {
        this.presetGenerator = new PresetGenerator();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setTitle("Plants vs. Zombies Roguelike Preset Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Initialize combo boxes - level count is fixed at 12, no need to create a selector
        Integer[] seedSlotOptions = {6, 7, 8, 9, 10};
        seedSlotCountComboBox = new JComboBox<>(seedSlotOptions);
        seedSlotCountComboBox.setSelectedItem(8);

        // Initialize checkboxes
        endlessModeCheckBox = new JCheckBox("Generate for endless modes (no levels)");
        patternPerLevelCheckBox = new JCheckBox("Generate pattern for every level (instead of just once)");
        patternPerLevelCheckBox.setSelected(true);

        // Initialize menu items
        themeGlossaryMenuItem = new JMenuItem("Theme Glossary");
        aboutMenuItem = new JMenuItem("About");
        customThemeCreatorMenuItem = new JMenuItem("Custom Theme Creator");
        manageThemesMenuItem = new JMenuItem("Manage Themes");
        toggleThemeMenuItem = new JMenuItem("Toggle Light/Dark Mode");
        themeToggleCheckBox = new JCheckBoxMenuItem("Dark Mode");

        // Initialize panels (they will be created in setupLayout)
        levelsPanel = new JPanel();
        seedSlotsPanel = new JPanel();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Create top control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add controls to the panel
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Number of Levels:"), gbc);
        gbc.gridx = 1;
        // Level count is fixed to 12, so we'll show it as a label instead
        JLabel fixedLevelLabel = new JLabel("12 (fixed)");
        controlPanel.add(fixedLevelLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Endless Mode:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(endlessModeCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Number of Seed Slots:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(seedSlotCountComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        controlPanel.add(patternPerLevelCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton generateButton = new JButton("Generate Preset");
        generateButton.addActionListener(e -> generatePreset());
        controlPanel.add(generateButton, gbc);

        // Add control panel to the top
        add(controlPanel, BorderLayout.NORTH);

        // Create main content area with separate panels for levels and seed slots
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create a panel for the levels (6x2 grid = 12 total)
        levelsPanel = new JPanel(new GridLayout(2, 6, 5, 5)); // 2 rows, 6 columns
        levelsPanel.setBorder(BorderFactory.createTitledBorder("Levels (6x2 grid)"));

        // Create a panel for the seed slots (5x2 grid = 10 max, but only show specified amount)
        seedSlotsPanel = new JPanel(new GridLayout(2, 5, 5, 5)); // 2 rows, 5 columns
        seedSlotsPanel.setBorder(BorderFactory.createTitledBorder("Seed Slots (5x2 grid)"));

        // Create a container panel to arrange levels and seed slots
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(levelsPanel, BorderLayout.CENTER);
        contentPanel.add(seedSlotsPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(themeGlossaryMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(themeToggleCheckBox);
        menuBar.add(fileMenu);

        // Themes menu
        JMenu themesMenu = new JMenu("Themes");
        themesMenu.add(customThemeCreatorMenuItem);
        themesMenu.add(manageThemesMenuItem);
        menuBar.add(themesMenu);

        // Dev menu
        JMenu devMenu = new JMenu("Dev");
        devMenu.add(aboutMenuItem);
        menuBar.add(devMenu);

        return menuBar;
    }
    
    private void setupEventHandlers() {
        // Menu item actions
        themeGlossaryMenuItem.addActionListener(new ThemeGlossaryActionListener());
        aboutMenuItem.addActionListener(new AboutActionListener());
        customThemeCreatorMenuItem.addActionListener(new CustomThemeCreatorActionListener());
        manageThemesMenuItem.addActionListener(new ManageThemesActionListener());

        // Theme toggle handler
        themeToggleCheckBox.addActionListener(e -> toggleTheme());
    }
    
    private void generatePreset() {
        try {
            // Fixed to 12 levels
            int numLevels = 12;
            boolean generateLevels = !endlessModeCheckBox.isSelected();
            int numSeedSlots = (Integer) seedSlotCountComboBox.getSelectedItem();
            boolean generatePatternPerLevel = patternPerLevelCheckBox.isSelected();

            // Generate the preset
            PresetGenerator.Preset preset = presetGenerator.generatePreset(
                numLevels, generateLevels, numSeedSlots, generatePatternPerLevel);

            // Format and display the preset - populate the new panels
            List<String> levels = preset.getLevels();
            List<String> patterns = preset.getPatterns();
            List<String> seedSlots = preset.getSeedSlots();

            // Clear previous content
            levelsPanel.removeAll();
            seedSlotsPanel.removeAll();

            // Populate the levels panel (6x2 grid for 12 levels)
            for (int i = 0; i < 12; i++) {
                String levelText;
                String patternText = "";

                if (i < levels.size()) {
                    levelText = levels.get(i);
                    if (i < patterns.size()) {
                        patternText = "\nPattern: " + patterns.get(i);
                    }
                } else {
                    levelText = "Level " + (i+1);
                    patternText = "\n[No level generated]";
                }

                JTextArea levelArea = new JTextArea(levelText + patternText);
                levelArea.setEditable(false);
                levelArea.setWrapStyleWord(true);
                levelArea.setLineWrap(true);
                levelArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
                levelArea.setBorder(BorderFactory.createLoweredBevelBorder());
                levelsPanel.add(levelArea);
            }

            // Populate the seed slots panel (5x2 grid, showing only the requested number of slots)
            int currentNumSeedSlots = (Integer) seedSlotCountComboBox.getSelectedItem();
            for (int i = 0; i < currentNumSeedSlots && i < seedSlots.size(); i++) {
                JTextArea slotArea = new JTextArea("Seed Slot " + (i+1) + "\n" + seedSlots.get(i));
                slotArea.setEditable(false);
                slotArea.setWrapStyleWord(true);
                slotArea.setLineWrap(true);
                slotArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
                slotArea.setBorder(BorderFactory.createLoweredBevelBorder());
                seedSlotsPanel.add(slotArea);
            }

            // Add empty slots if needed to fill the 5x2 grid (up to 10 slots)
            for (int i = currentNumSeedSlots; i < 10; i++) {
                JTextArea emptySlot = new JTextArea("Seed Slot " + (i+1) + " (empty)");
                emptySlot.setEditable(false);
                emptySlot.setWrapStyleWord(true);
                emptySlot.setLineWrap(true);
                emptySlot.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
                emptySlot.setBorder(BorderFactory.createLoweredBevelBorder());
                seedSlotsPanel.add(emptySlot);
            }

            // Repaint the panels
            levelsPanel.revalidate();
            levelsPanel.repaint();
            seedSlotsPanel.revalidate();
            seedSlotsPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error generating preset: " + e.getMessage(),
                "Generation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class ThemeGlossaryActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showThemeGlossary();
        }
    }
    
    private class AboutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showAboutDialog();
        }
    }
    
    private class CustomThemeCreatorActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            CustomThemeCreator creator = new CustomThemeCreator();
            creator.setVisible(true);
        }
    }

    private class ManageThemesActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ThemeManagerDialog themeManagerDialog = new ThemeManagerDialog(MainWindow.this);
            themeManagerDialog.setVisible(true);
        }
    }

    private void showThemeGlossary() {
        PatternGenerator patternGen = new PatternGenerator();
        StringBuilder glossary = new StringBuilder();
        
        glossary.append("PLANT VS. ZOMBIES PATTERN GLOSSARY\n");
        glossary.append("==================================\n\n");
        
        for (String pattern : patternGen.getAllPatterns()) {
            glossary.append(pattern).append(":\n");
            glossary.append("  ").append(patternGen.getPatternDescription(pattern)).append("\n\n");
        }
        
        JTextArea textArea = new JTextArea(glossary.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Pattern Glossary",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showAboutDialog() {
        String aboutText =
            "Plants vs. Zombies Roguelike Preset Generator\n\n" +
            "Version: 1.0\n" +
            "Developed with Java Swing and Apache Ant\n\n" +
            "This application generates random presets for\n" +
            "a Plants vs. Zombies roguelike game, including:\n" +
            "- Randomized level sequences\n" +
            "- Planting patterns\n" +
            "- Seed slot selections\n\n" +
            "Created with PepperMint framework";

        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void toggleTheme() {
        if (themeToggleCheckBox.isSelected()) {
            // Switch to dark theme
            try {
                Class<?> flatDarkLafClass = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
                Object laf = flatDarkLafClass.getDeclaredConstructor().newInstance();
                UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Switch to light theme
            try {
                Class<?> flatLightLafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
                Object laf = flatLightLafClass.getDeclaredConstructor().newInstance();
                UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup initial theme - default to light theme if FlatLaf is available
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
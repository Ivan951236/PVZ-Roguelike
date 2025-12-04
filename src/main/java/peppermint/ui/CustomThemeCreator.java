package peppermint.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CheckedOutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// Import for XZ compression
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

public class CustomThemeCreator extends JFrame {
    private JTextField nameField;
    private JTextField developerField;
    private JCheckBox darkModeCheckBox;
    private JCheckBox encryptedCheckBox;
    private JPasswordField passwordField;

    // Color editing fields
    private JTextField primaryColorField;
    private JTextField secondaryColorField;
    private JTextField backgroundColorField;
    private JTextField foregroundColorField;

    private JButton createThemeButton;
    private JButton selectKeyButton;
    private JButton selectPrimaryColorButton;
    private JButton selectSecondaryColorButton;
    private JButton selectBackgroundColorButton;
    private JButton selectForegroundColorButton;
    private String selectedKeyPath;

    public CustomThemeCreator() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setTitle("Custom Theme Creator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        // Apply FlatLaf styling (only applicable to top-level windows)
        // This will be handled by FlatLaf automatically
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        developerField = new JTextField(20);
        darkModeCheckBox = new JCheckBox("Dark Mode");
        encryptedCheckBox = new JCheckBox("Encrypt theme files");
        passwordField = new JPasswordField(20);
        passwordField.setEnabled(false);
        createThemeButton = new JButton("Create Theme Package");
        selectKeyButton = new JButton("Select OpenPGP Key");

        // Initialize color fields with default values
        primaryColorField = new JTextField("#2196F3", 10);  // Blue
        secondaryColorField = new JTextField("#FF9800", 10); // Orange
        backgroundColorField = new JTextField("#FFFFFF", 10); // White
        foregroundColorField = new JTextField("#000000", 10); // Black

        // Initialize color selection buttons
        selectPrimaryColorButton = new JButton("Choose");
        selectSecondaryColorButton = new JButton("Choose");
        selectBackgroundColorButton = new JButton("Choose");
        selectForegroundColorButton = new JButton("Choose");

        // Set up event handler for encrypted checkbox
        encryptedCheckBox.addActionListener(e -> {
            passwordField.setEnabled(encryptedCheckBox.isSelected());
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create main input panel with scroll
        JPanel mainInputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        mainInputPanel.add(new JLabel("Theme Name:"), gbc);
        gbc.gridx = 1;
        mainInputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainInputPanel.add(new JLabel("Developer:"), gbc);
        gbc.gridx = 1;
        mainInputPanel.add(developerField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainInputPanel.add(darkModeCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        mainInputPanel.add(encryptedCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainInputPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        mainInputPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        mainInputPanel.add(selectKeyButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainInputPanel.add(new JLabel("Color Settings:"), gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        mainInputPanel.add(new JLabel("Primary Color:"), gbc);
        gbc.gridx = 1;
        JPanel primaryColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        primaryColorPanel.add(primaryColorField);
        primaryColorPanel.add(selectPrimaryColorButton);
        mainInputPanel.add(primaryColorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        mainInputPanel.add(new JLabel("Secondary Color:"), gbc);
        gbc.gridx = 1;
        JPanel secondaryColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondaryColorPanel.add(secondaryColorField);
        secondaryColorPanel.add(selectSecondaryColorButton);
        mainInputPanel.add(secondaryColorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        mainInputPanel.add(new JLabel("Background Color:"), gbc);
        gbc.gridx = 1;
        JPanel backgroundColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backgroundColorPanel.add(backgroundColorField);
        backgroundColorPanel.add(selectBackgroundColorButton);
        mainInputPanel.add(backgroundColorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 10;
        mainInputPanel.add(new JLabel("Foreground Color:"), gbc);
        gbc.gridx = 1;
        JPanel foregroundColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        foregroundColorPanel.add(foregroundColorField);
        foregroundColorPanel.add(selectForegroundColorButton);
        mainInputPanel.add(foregroundColorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainInputPanel.add(createThemeButton, gbc);

        // Wrap in scroll pane for better usability
        JScrollPane scrollPane = new JScrollPane(mainInputPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add a description area
        JTextArea descriptionArea = new JTextArea(
            "Custom Theme Creator:\n\n" +
            "1. Enter a name for your theme\n" +
            "2. Enter the developer name\n" +
            "3. Check 'Dark Mode' if this is a dark theme\n" +
            "4. Check 'Encrypt theme files' if you want encryption\n" +
            "5. Customize color values in hex format (#RRGGBB)\n" +
            "6. Select 'Choose' buttons to pick colors visually\n" +
            "7. Click 'Create Theme Package' to generate a .pmt file\n\n" +
            "The theme file will contain:\n" +
            "- mani.toml (manifest file)\n" +
            "- theme.toml (theme settings)\n" +
            "- openpgp-key.asc (if encrypted)"
        );
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(descriptionArea, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        createThemeButton.addActionListener(new CreateThemeActionListener());
        selectKeyButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select OpenPGP Key File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("ASC Files", "asc"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedKeyPath = fileChooser.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this,
                    "Selected key: " + selectedKeyPath,
                    "Key Selected",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Add color selection button handlers
        selectPrimaryColorButton.addActionListener(e -> {
            Color initialColor = getColorFromHex(primaryColorField.getText());
            Color newColor = JColorChooser.showDialog(this, "Choose Primary Color", initialColor);
            if (newColor != null) {
                primaryColorField.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
            }
        });

        selectSecondaryColorButton.addActionListener(e -> {
            Color initialColor = getColorFromHex(secondaryColorField.getText());
            Color newColor = JColorChooser.showDialog(this, "Choose Secondary Color", initialColor);
            if (newColor != null) {
                secondaryColorField.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
            }
        });

        selectBackgroundColorButton.addActionListener(e -> {
            Color initialColor = getColorFromHex(backgroundColorField.getText());
            Color newColor = JColorChooser.showDialog(this, "Choose Background Color", initialColor);
            if (newColor != null) {
                backgroundColorField.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
            }
        });

        selectForegroundColorButton.addActionListener(e -> {
            Color initialColor = getColorFromHex(foregroundColorField.getText());
            Color newColor = JColorChooser.showDialog(this, "Choose Foreground Color", initialColor);
            if (newColor != null) {
                foregroundColorField.setText(String.format("#%06X", (0xFFFFFF & newColor.getRGB())));
            }
        });
    }

    private Color getColorFromHex(String hex) {
        if (hex == null || !hex.startsWith("#")) {
            return Color.WHITE; // Default to white if invalid
        }
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return Color.WHITE; // Default to white if invalid
        }
    }
    
    private class CreateThemeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String developer = developerField.getText().trim();
            boolean darkMode = darkModeCheckBox.isSelected();
            boolean encrypted = encryptedCheckBox.isSelected();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || developer.isEmpty()) {
                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Please fill in all required fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (encrypted && (password.isEmpty() || selectedKeyPath == null)) {
                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Please provide a password and select an OpenPGP key for encrypted themes",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validate color formats
            if (!isValidHexColor(primaryColorField.getText()) ||
                !isValidHexColor(secondaryColorField.getText()) ||
                !isValidHexColor(backgroundColorField.getText()) ||
                !isValidHexColor(foregroundColorField.getText())) {
                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Please enter valid hex color codes (#RRGGBB format)",
                    "Color Format Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Create the PepperMintThemes directory if it doesn't exist
            Path themesDir = Paths.get("PepperMintThemes");
            try {
                Files.createDirectories(themesDir);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Failed to create PepperMintThemes directory: " + ex.getMessage(),
                    "Directory Creation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Create theme package
            try {
                createThemePackage(name, developer, darkMode, encrypted, password, selectedKeyPath, themesDir);

                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Theme '" + name + "' created successfully!\n" +
                    "File: " + name + ".pmt\n" +
                    "Location: " + themesDir.toAbsolutePath(),
                    "Theme Created",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Clear form
                nameField.setText("");
                developerField.setText("");
                darkModeCheckBox.setSelected(false);
                encryptedCheckBox.setSelected(false);
                passwordField.setText("");
                // Reset colors to defaults
                primaryColorField.setText("#2196F3");
                secondaryColorField.setText("#FF9800");
                backgroundColorField.setText("#FFFFFF");
                foregroundColorField.setText("#000000");
                selectedKeyPath = null;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    CustomThemeCreator.this,
                    "Failed to create theme: " + ex.getMessage(),
                    "Creation Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private boolean isValidHexColor(String color) {
            if (color == null || !color.startsWith("#")) {
                return false;
            }
            String hex = color.substring(1);
            return hex.length() == 6 && hex.matches("[0-9A-Fa-f]+");
        }

        private void createThemePackage(String name, String developer, boolean darkMode, boolean encrypted,
                                       String password, String keyPath, Path themesDir) throws Exception {
            // Create a temporary directory to hold theme files
            Path tempDir = Files.createTempDirectory("pmt_theme_" + name);
            try {
                // Create theme.toml file with theme information including colors
                Path themeToml = tempDir.resolve("theme.toml");
                try (FileWriter writer = new FileWriter(themeToml.toFile())) {
                    writer.write("# PepperMint Theme Configuration\n");
                    writer.write("[theme]\n");
                    writer.write("name = \"" + name + "\"\n");
                    writer.write("developer = \"" + developer + "\"\n");
                    writer.write("encrypted = " + encrypted + "\n");
                    writer.write("version = \"1.0\"\n");
                    writer.write("dark_mode = " + darkMode + "\n");

                    // Add theme colors
                    writer.write("\n[colors]\n");
                    writer.write("primary = \"" + primaryColorField.getText() + "\"\n");
                    writer.write("secondary = \"" + secondaryColorField.getText() + "\"\n");
                    writer.write("background = \"" + backgroundColorField.getText() + "\"\n");
                    writer.write("foreground = \"" + foregroundColorField.getText() + "\"\n");
                }

                // Create mani.toml manifest file
                Path manifestFile = tempDir.resolve("mani.toml");
                try (FileWriter writer = new FileWriter(manifestFile.toFile())) {
                    writer.write("# PepperMint Theme Manifest\n");
                    writer.write("[manifest]\n");
                    writer.write("name = \"" + name + "\"\n");
                    writer.write("author = \"" + developer + "\"\n");
                    writer.write("version = \"1.0\"\n");
                    writer.write("type = \"theme\"\n");
                    writer.write("encryption = " + encrypted + "\n");
                }

                // If encrypted, copy the OpenPGP key
                if (encrypted && keyPath != null) {
                    Path sourceKey = Paths.get(keyPath);
                    Path targetKey = tempDir.resolve("openpgp-key.asc");
                    Files.copy(sourceKey, targetKey);
                }

                // Create the .pmt file (XZ-compressed tarball)
                Path pmtFile = themesDir.resolve(name + ".pmt");
                createXZArchive(tempDir, pmtFile);
            } finally {
                // Clean up the temporary directory
                deleteRecursively(tempDir);
            }
        }

        private void createXZArchive(Path sourceDir, Path outputPath) throws IOException {
            try (FileOutputStream fileOut = new FileOutputStream(outputPath.toFile());
                 XZCompressorOutputStream xzOut = new XZCompressorOutputStream(fileOut);
                 TarArchiveOutputStream tarOut = new TarArchiveOutputStream(xzOut)) {

                tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

                Files.walk(sourceDir)
                    .filter(path -> !path.equals(sourceDir))
                    .forEach(path -> {
                        try {
                            String entryName = sourceDir.relativize(path).toString().replace('\\', '/');
                            TarArchiveEntry entry;

                            if (Files.isDirectory(path)) {
                                entry = new TarArchiveEntry(entryName + "/");
                            } else {
                                entry = new TarArchiveEntry(path.toFile(), entryName);
                            }

                            tarOut.putArchiveEntry(entry);

                            if (!Files.isDirectory(path)) {
                                Files.copy(path, tarOut);
                            }

                            tarOut.closeArchiveEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }
        }

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
    }
}
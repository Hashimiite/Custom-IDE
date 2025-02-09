package src.gui;

import javax.swing.*;
import java.awt.*;
import src.util.ConfigManager;

public class PreferencesDialog extends JDialog {
    private ConfigManager config;
    private JComboBox<String> themeCombo;
    private JSpinner fontSizeSpinner;
    private JCheckBox autoSaveBox;
    private JSpinner tabSizeSpinner;

    public PreferencesDialog(JFrame parent, ConfigManager config) {
        super(parent, "Preferences", true);
        this.config = config;
        initializeUI();
        loadCurrentSettings();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        add(createPreferencesPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Theme selection
        themeCombo = new JComboBox<>(new String[]{"Light", "Dark", "Monokai"});
        addLabelAndComponent(panel, "Theme:", themeCombo, gbc, 0);

        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1));
        addLabelAndComponent(panel, "Font Size:", fontSizeSpinner, gbc, 1);

        tabSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 8, 2));
        addLabelAndComponent(panel, "Tab Size:", tabSizeSpinner, gbc, 2);

        autoSaveBox = new JCheckBox("Enable Auto-save");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(autoSaveBox, gbc);

        return panel;
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent component, 
                                    GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> savePreferences());
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);
        return panel;
    }

    private void loadCurrentSettings() {
        themeCombo.setSelectedItem(config.getProperty("theme"));
        fontSizeSpinner.setValue(Integer.parseInt(config.getProperty("font.size")));
        tabSizeSpinner.setValue(Integer.parseInt(config.getProperty("tab.size")));
        autoSaveBox.setSelected(Boolean.parseBoolean(config.getProperty("auto.save")));
    }

    private void savePreferences() {
        config.setProperty("theme", (String) themeCombo.getSelectedItem());
        config.setProperty("font.size", fontSizeSpinner.getValue().toString());
        config.setProperty("tab.size", tabSizeSpinner.getValue().toString());
        config.setProperty("auto.save", String.valueOf(autoSaveBox.isSelected()));
        dispose();
    }
}
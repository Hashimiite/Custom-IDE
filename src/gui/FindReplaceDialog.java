package src.gui;

import javax.swing.*;
import java.awt.*;
import java.util.regex.*;

public class FindReplaceDialog extends JDialog {
    private JTextField findField;
    private JTextField replaceField;
    private JCheckBox regexCheckBox;
    private JCheckBox matchCaseCheckBox;
    private TextArea textArea;
    private int lastIndex = 0;

    public FindReplaceDialog(JFrame parent, TextArea textArea) {
        super(parent, "Find and Replace", false);
        this.textArea = textArea;
        initializeUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Find panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Find:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        findField = new JTextField(30);
        mainPanel.add(findField, gbc);

        // Replace panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Replace:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        replaceField = new JTextField(30);
        mainPanel.add(replaceField, gbc);

        // Options panel
        JPanel optionsPanel = new JPanel();
        regexCheckBox = new JCheckBox("Regular expressions");
        matchCaseCheckBox = new JCheckBox("Match case");
        optionsPanel.add(regexCheckBox);
        optionsPanel.add(matchCaseCheckBox);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(optionsPanel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton findButton = new JButton("Find Next");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");
        JButton closeButton = new JButton("Close");

        findButton.addActionListener(e -> findNext());
        replaceButton.addActionListener(e -> replace());
        replaceAllButton.addActionListener(e -> replaceAll());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(findButton);
        buttonPanel.add(replaceButton);
        buttonPanel.add(replaceAllButton);
        buttonPanel.add(closeButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void findNext() {
        String searchText = findField.getText();
        String content = textArea.getText();
        
        if (searchText.isEmpty()) return;

        if (regexCheckBox.isSelected()) {
            findNextRegex(searchText, content);
        } else {
            findNextPlain(searchText, content);
        }
    }

    private void findNextPlain(String searchText, String content) {
        if (!matchCaseCheckBox.isSelected()) {
            searchText = searchText.toLowerCase();
            content = content.toLowerCase();
        }

        int index = content.indexOf(searchText, lastIndex);
        if (index == -1) {
            index = content.indexOf(searchText, 0);
        }

        if (index != -1) {
            textArea.setCaretPosition(index);
            textArea.moveCaretPosition(index + searchText.length());
            textArea.requestFocus();
            lastIndex = index + 1;
        } else {
            JOptionPane.showMessageDialog(this, "Text not found", "Find", JOptionPane.INFORMATION_MESSAGE);
            lastIndex = 0;
        }
    }

    private void findNextRegex(String regex, String content) {
        try {
            Pattern pattern = matchCaseCheckBox.isSelected() ? 
                Pattern.compile(regex) : 
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            
            Matcher matcher = pattern.matcher(content);
            if (matcher.find(lastIndex)) {
                textArea.setCaretPosition(matcher.start());
                textArea.moveCaretPosition(matcher.end());
                textArea.requestFocus();
                lastIndex = matcher.end();
            } else if (matcher.find(0)) {
                textArea.setCaretPosition(matcher.start());
                textArea.moveCaretPosition(matcher.end());
                textArea.requestFocus();
                lastIndex = matcher.end();
            } else {
                JOptionPane.showMessageDialog(this, "Text not found", "Find", JOptionPane.INFORMATION_MESSAGE);
                lastIndex = 0;
            }
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(this, "Invalid regular expression: " + e.getMessage());
        }
    }

    private void replace() {
        if (textArea.getSelectedText() != null) {
            textArea.replaceSelection(replaceField.getText());
            findNext();
        } else {
            findNext();
        }
    }

    private void replaceAll() {
        String findText = findField.getText();
        String replaceText = replaceField.getText();
        String content = textArea.getText();
        
        if (findText.isEmpty()) return;

        if (regexCheckBox.isSelected()) {
            replaceAllRegex(findText, replaceText, content);
        } else {
            replaceAllPlain(findText, replaceText, content);
        }
    }

    private void replaceAllPlain(String findText, String replaceText, String content) {
        if (!matchCaseCheckBox.isSelected()) {
            Pattern pattern = Pattern.compile(Pattern.quote(findText), Pattern.CASE_INSENSITIVE);
            String newContent = pattern.matcher(content).replaceAll(replaceText);
            textArea.setText(newContent);
        } else {
            String newContent = content.replace(findText, replaceText);
            textArea.setText(newContent);
        }
    }

    private void replaceAllRegex(String regex, String replaceText, String content) {
        try {
            Pattern pattern = matchCaseCheckBox.isSelected() ? 
                Pattern.compile(regex) : 
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            
            String newContent = pattern.matcher(content).replaceAll(replaceText);
            textArea.setText(newContent);
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(this, "Invalid regular expression: " + e.getMessage());
        }
    }
}

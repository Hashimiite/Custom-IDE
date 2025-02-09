package src.gui;

import java.awt.event.*;
import java.io.*;
import src.core.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.awt.Component;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import src.util.ConfigManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class MenuBar extends JMenuBar {
    private EditorWindow editor;
    private TabManager tabManager;
    
    public MenuBar(EditorWindow editor) {
        this.editor = editor;
        this.tabManager = editor.getTabManager();
        createMenus();
    }

    private void createMenus() {
        add(createFileMenu());
        add(createEditMenu());
        add(createViewMenu());
        add(createToolsMenu());
        add(createRunMenu());
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        
        addMenuItem(fileMenu, "New", "ctrl N", e -> newFile());
        addMenuItem(fileMenu, "Open", "ctrl O", e -> openFile());
        addMenuItem(fileMenu, "Save", "ctrl S", e -> saveFile());
        addMenuItem(fileMenu, "Save As", "ctrl shift S", e -> saveFileAs());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Close Tab", "ctrl W", e -> closeCurrentTab());
        addMenuItem(fileMenu, "Exit", "alt F4", e -> editor.dispose());
        
        return fileMenu;
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        EditorTab currentTab = tabManager.getCurrentTab();
        
        addMenuItem(editMenu, "Undo", "ctrl Z", e -> currentTab.getUndoManager().undo());
        addMenuItem(editMenu, "Redo", "ctrl Y", e -> currentTab.getUndoManager().redo());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Cut", "ctrl X", e -> currentTab.getTextArea().cut());
        addMenuItem(editMenu, "Copy", "ctrl C", e -> currentTab.getTextArea().copy());
        addMenuItem(editMenu, "Paste", "ctrl V", e -> currentTab.getTextArea().paste());
        editMenu.addSeparator();
        addMenuItem(editMenu, "Find/Replace", "ctrl F", e -> showFindReplace());
        addMenuItem(editMenu, "Select All", "ctrl A", e -> currentTab.getTextArea().selectAll());
        
        return editMenu;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        
        JCheckBoxMenuItem lineNumbers = new JCheckBoxMenuItem("Line Numbers");
        lineNumbers.setSelected(true);
        lineNumbers.addActionListener(e -> toggleLineNumbers());
        
        JMenu themes = new JMenu("Themes");
        String[] themeNames = {"Light", "Dark", "Monokai"};
        for (String theme : themeNames) {
            JMenuItem themeItem = new JMenuItem(theme);
            themeItem.addActionListener(e -> changeTheme(theme));
            themes.add(themeItem);
        }
        
        viewMenu.add(lineNumbers);
        viewMenu.add(themes);
        
        return viewMenu;
    }

    private JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        
        addMenuItem(toolsMenu, "Format Code", "ctrl shift F", e -> formatCode());
        addMenuItem(toolsMenu, "Toggle Comment", "ctrl /", e -> toggleComment());
        toolsMenu.addSeparator();
        addMenuItem(toolsMenu, "Preferences", "ctrl P", e -> showPreferences());
        
        return toolsMenu;
    }

    private void addMenuItem(JMenu menu, String text, String accelerator, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        item.addActionListener(listener);
        menu.add(item);
    }

    public boolean saveFile() {
        EditorTab currentTab = tabManager.getCurrentTab();
        if (currentTab.getFilePath() == null) {
            return saveFileAs();
        }
        
        try {
            FileWriter writer = new FileWriter(currentTab.getFilePath());
            currentTab.getTextArea().write(writer);
            writer.close();
            currentTab.setModified(false);
            return true;
        } catch (IOException e) {
            showError("Error saving file: " + e.getMessage());
            return false;
        }
    }

    private boolean saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(editor) == JFileChooser.APPROVE_OPTION) {
            EditorTab currentTab = tabManager.getCurrentTab();
            currentTab.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
            if (saveFile()) {
                editor.setTitle("Code Editor - " + fileChooser.getSelectedFile().getName());
                return true;
            }
        }
        return false;
    }

    private void newFile() {
        tabManager.createNewTab();
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(editor) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                FileReader reader = new FileReader(path);
                EditorTab newTab = tabManager.getCurrentTab();
                newTab.getTextArea().read(reader, null);
                newTab.setFilePath(path);
                newTab.setModified(false);
                editor.setTitle("Code Editor - " + fileChooser.getSelectedFile().getName());
            } catch (IOException e) {
                showError("Error opening file: " + e.getMessage());
            }
        }
    }

    private void closeCurrentTab() {
        EditorTab currentTab = tabManager.getCurrentTab();
        if (currentTab.isModified()) {
            int choice = JOptionPane.showConfirmDialog(
                editor,
                "Save changes before closing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            
            if (choice == JOptionPane.YES_OPTION && !saveFile()) {
                return;
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        if (tabManager.getTabbedPane().getTabCount() > 1) {
            tabManager.getTabbedPane().remove(tabManager.getTabbedPane().getSelectedComponent());
        } else {
            newFile();
        }
    }

    private void showFindReplace() {
        editor.getFindReplaceDialog().setVisible(true);
    }

    private void formatCode() {
        EditorTab currentTab = tabManager.getCurrentTab();
        String code = currentTab.getTextArea().getText();
        String formattedCode = formatWithIndentation(code);
        currentTab.getTextArea().setText(formattedCode);
    }
    
    private String formatWithIndentation(String code) {
        EditorTab currentTab = tabManager.getCurrentTab();
        String[] lines = code.split("\n");
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
     
        // Get file extension to determine language
        String extension = "";
        if (currentTab.getFilePath() != null) {
            extension = getFileExtension(currentTab.getFilePath());
        }
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Handle Python-style indentation
            if (extension.equals("py")) {
                if (trimmedLine.endsWith(":")) {
                    formatted.append("    ".repeat(indentLevel)).append(trimmedLine).append("\n");
                    indentLevel++;
                } else if (trimmedLine.startsWith("return ") || trimmedLine.startsWith("break") || 
                          trimmedLine.startsWith("continue") || trimmedLine.equals("pass")) {
                    formatted.append("    ".repeat(indentLevel)).append(trimmedLine).append("\n");
                    if (indentLevel > 0) indentLevel--;
                } else {
                    formatted.append("    ".repeat(indentLevel)).append(trimmedLine).append("\n");
                }
            } else {
                // Original Java-style indentation
                if (trimmedLine.endsWith("}")) indentLevel--;
                formatted.append("    ".repeat(Math.max(0, indentLevel))).append(trimmedLine).append("\n");
                if (trimmedLine.endsWith("{")) indentLevel++;
            }
        }
        return formatted.toString();
     }

    private void toggleComment() {
        EditorTab currentTab = tabManager.getCurrentTab();
        TextArea textArea = currentTab.getTextArea();
        String selectedText = textArea.getSelectedText();
        
        if (selectedText != null) {
            String[] lines = selectedText.split("\n");
            StringBuilder result = new StringBuilder();
            boolean allCommented = true;
            
            for (String line : lines) {
                if (!line.trim().startsWith("//")) {
                    allCommented = false;
                    break;
                }
            }
            
            for (String line : lines) {
                if (allCommented) {
                    if (line.trim().startsWith("//")) {
                        result.append(line.replaceFirst("//", "")).append("\n");
                    } else {
                        result.append(line).append("\n");
                    }
                } else {
                    result.append("//").append(line).append("\n");
                }
            }
            
            textArea.replaceSelection(result.toString().trim());
        }
    }

    private void toggleLineNumbers() {
        EditorTab currentTab = tabManager.getCurrentTab();
        JScrollPane scrollPane = (JScrollPane) currentTab.getComponent(0);
        JViewport rowHeader = scrollPane.getRowHeader();
        
        if (rowHeader == null) {
            scrollPane.setRowHeaderView(new LineNumberArea(currentTab.getTextArea()));
        } else {
            scrollPane.setRowHeaderView(null);
        }
    }
    
    private void changeTheme(String theme) {
        EditorTab currentTab = tabManager.getCurrentTab();
        TextArea textArea = currentTab.getTextArea();
        
        switch (theme.toLowerCase()) {
            case "dark" -> {
                textArea.setBackground(new Color(43, 43, 43));
                textArea.setForeground(new Color(169, 183, 198));
                textArea.setCaretColor(Color.WHITE);
            }
            case "monokai" -> {
                textArea.setBackground(new Color(39, 40, 34));
                textArea.setForeground(new Color(248, 248, 242));
                textArea.setCaretColor(new Color(249, 38, 114));
            }
            default -> {
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(Color.BLACK);
                textArea.setCaretColor(Color.BLACK);
            }
        }
    }
    
    private void showPreferences() {
        PreferencesDialog dialog = new PreferencesDialog(editor, new ConfigManager());
        dialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            editor,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private JMenu createRunMenu() {
        JMenu runMenu = new JMenu("Run");
        addMenuItem(runMenu, "Run", "F5", e -> runCode());
        return runMenu;
    }
    
    private void runCode() {
        EditorTab currentTab = tabManager.getCurrentTab();
        String filePath = currentTab.getFilePath();
        if (filePath == null) {
            showError("Please save the file before running");
            return;
        }
    
        String extension = getFileExtension(filePath);
        try {
            switch (extension.toLowerCase()) {
                case "java" -> runJavaCode(filePath);
                case "py" -> runPythonCode(filePath);
                case "js" -> runJavaScriptCode(filePath);
                default -> showError("Unsupported file type for execution");
            }
        } catch (Exception e) {
            showError("Error running code: " + e.getMessage());
        }
    }
    
    private void runJavaCode(String filePath) throws Exception {
        String className = getClassName(filePath);
        File directory = new File(filePath).getParentFile();
        
        // Compile
        Process compile = Runtime.getRuntime().exec("javac " + filePath);
        compile.waitFor();
    
        if (compile.exitValue() == 0) {
            // Run
            Process run = Runtime.getRuntime().exec("java -cp " + directory.getAbsolutePath() + " " + className);
            showProcessOutput(run);
        } else {
            showProcessOutput(compile);
        }
    }
    
    private void runPythonCode(String filePath) throws Exception {
        Process process = Runtime.getRuntime().exec("/opt/homebrew/bin/python3 " + filePath);
        showProcessOutput(process);
    }
    
    private void runJavaScriptCode(String filePath) throws Exception {
        Process process = Runtime.getRuntime().exec("node " + filePath);
        showProcessOutput(process);
    }
    
    private void showProcessOutput(Process process) {
        JDialog outputDialog = new JDialog(editor, "Output", false);
        JTextArea outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line;
                    SwingUtilities.invokeLater(() -> 
                        outputArea.append(finalLine + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line;
                    SwingUtilities.invokeLater(() -> 
                        outputArea.append("Error: " + finalLine + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    
        outputDialog.add(new JScrollPane(outputArea));
        outputDialog.pack();
        outputDialog.setLocationRelativeTo(editor);
        outputDialog.setVisible(true);
    }
    
    private String getClassName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
    
    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot + 1) : "";
    }
}
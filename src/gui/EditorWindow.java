package src.gui;

import src.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditorWindow extends JFrame {
    private TabManager tabManager;
    private MenuBar menuBar;
    private StatusBar statusBar;
    private FindReplaceDialog findReplaceDialog;

    public EditorWindow() {
        setTitle("Code Editor");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        initializeComponents();
        setupLayout();
        setupWindowListeners();
    }

    private void initializeComponents() {
        tabManager = new TabManager();
        menuBar = new MenuBar(this);
        statusBar = new StatusBar();
        findReplaceDialog = new FindReplaceDialog(this, getCurrentTextArea());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabManager.getTabbedPane(), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        setJMenuBar(menuBar);
    }

    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        tabManager.getTabbedPane().addChangeListener(e -> {
            updateTitle();
            updateStatusBar();
        });
    }

    private void handleExit() {
        boolean canExit = true;
        
        for (int i = 0; i < tabManager.getTabbedPane().getTabCount(); i++) {
            EditorTab tab = (EditorTab) tabManager.getTabbedPane().getComponentAt(i);
            if (tab.isModified()) {
                tabManager.getTabbedPane().setSelectedIndex(i);
                int choice = showUnsavedChangesDialog(tab);
                
                if (choice == JOptionPane.YES_OPTION) {
                    if (!menuBar.saveFile()) {
                        canExit = false;
                        break;
                    }
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    canExit = false;
                    break;
                }
            }
        }
        
        if (canExit) {
            dispose();
            System.exit(0);
        }
    }

    private int showUnsavedChangesDialog(EditorTab tab) {
        String fileName = tab.getFilePath() != null ? 
            new java.io.File(tab.getFilePath()).getName() : 
            "Untitled";
            
        return JOptionPane.showConfirmDialog(
            this,
            "Save changes to " + fileName + "?",
            "Unsaved Changes",
            JOptionPane.YES_NO_CANCEL_OPTION
        );
    }

    private void updateTitle() {
        EditorTab currentTab = (EditorTab) tabManager.getTabbedPane().getSelectedComponent();
        String fileName = currentTab.getFilePath() != null ? 
            new java.io.File(currentTab.getFilePath()).getName() : 
            "Untitled";
        setTitle("Code Editor - " + fileName);
    }

    private void updateStatusBar() {
        EditorTab currentTab = (EditorTab) tabManager.getTabbedPane().getSelectedComponent();
        TextArea textArea = currentTab.getTextArea();
        int caretPos = textArea.getCaretPosition();
        try {
            int line = textArea.getLineOfOffset(caretPos);
            int column = caretPos - textArea.getLineStartOffset(line);
            statusBar.setStatus(String.format("Line: %d, Column: %d", line + 1, column + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TabManager getTabManager() {
        return tabManager;
    }

    public TextArea getCurrentTextArea() {
        return ((EditorTab) tabManager.getTabbedPane().getSelectedComponent()).getTextArea();
    }

    public FindReplaceDialog getFindReplaceDialog() {
        return findReplaceDialog;
    }
}
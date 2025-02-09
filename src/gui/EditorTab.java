package src.gui;

import javax.swing.*;
import java.awt.*;
import src.core.UndoRedoManager;

public class EditorTab extends JPanel {
    private TextArea textArea;
    private LineNumberArea lineNumberArea;
    private UndoRedoManager undoManager;
    private String filePath;
    private boolean isModified;

    public EditorTab() {
        setLayout(new BorderLayout());
        
        textArea = new TextArea();
        lineNumberArea = new LineNumberArea(textArea);
        undoManager = new UndoRedoManager();
        
        textArea.getDocument().addUndoableEditListener(undoManager);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberArea);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String path) {
        this.filePath = path;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        this.isModified = modified;
    }

    public UndoRedoManager getUndoManager() {
        return undoManager;
    }
}
package src.gui;

import src.core.SyntaxHighlighter;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;

public class TextArea extends JTextPane {
    private boolean hasUnsavedChanges;
    private SyntaxHighlighter syntaxHighlighter;
    private Timer highlightTimer;
    private String currentLanguage;
    private Highlighter editorHighlighter;

    public TextArea() {
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setMargin(new Insets(5, 5, 5, 5));
        syntaxHighlighter = new SyntaxHighlighter();
        editorHighlighter = getHighlighter();
        currentLanguage = "java";
        
        setupDocumentListener();
        setupHighlightTimer();
    }

    private void setupDocumentListener() {
        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                hasUnsavedChanges = true;
                restartHighlightTimer();
            }
            public void removeUpdate(DocumentEvent e) {
                hasUnsavedChanges = true;
                restartHighlightTimer();
            }
            public void changedUpdate(DocumentEvent e) {
                hasUnsavedChanges = true;
            }
        });
    }

    private void setupHighlightTimer() {
        highlightTimer = new Timer(500, e -> performHighlighting());
        highlightTimer.setRepeats(false);
    }

    private void restartHighlightTimer() {
        highlightTimer.restart();
    }

    private void performHighlighting() {
        syntaxHighlighter.highlight((StyledDocument) getDocument());
    }

    public void setLanguage(String language) {
        currentLanguage = language;
        syntaxHighlighter.setLanguage(language);
        performHighlighting();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    public void setHasUnsavedChanges(boolean hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    }

    public SyntaxHighlighter getSyntaxHighlighter() {
        return syntaxHighlighter;
    }
    
    public int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        return doc.getDefaultRootElement().getElementIndex(offset);
    }
    
    public int getLineStartOffset(int line) throws BadLocationException {
        Element root = getDocument().getDefaultRootElement();
        return root.getElement(line).getStartOffset();
    }
}
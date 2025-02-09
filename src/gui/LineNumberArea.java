package src.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.awt.event.*;

public class LineNumberArea extends JPanel {
    private TextArea textArea;
    private static final int MARGIN = 5;

    public LineNumberArea(TextArea textArea) {
        this.textArea = textArea;
        setPreferredWidth();
        setBackground(new Color(240, 240, 240));
        setFont(textArea.getFont());
    }

    private void setPreferredWidth() {
        int lineCount = getLineCount();
        int width = getFontMetrics(getFont()).stringWidth(String.valueOf(lineCount)) + 2 * MARGIN;
        setPreferredSize(new Dimension(width, 0));
    }

    private int getLineCount() {
        Element root = textArea.getDocument().getDefaultRootElement();
        return root.getElementCount();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int lineHeight = fontMetrics.getHeight();
        int startOffset = textArea.getInsets().top;
        
        for (int i = 1; i <= getLineCount(); i++) {
            String lineNum = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNum);
            int x = getWidth() - width - MARGIN;
            int y = startOffset + (i - 1) * lineHeight + fontMetrics.getAscent();
            g.drawString(lineNum, x, y);
        }
    }
}

// Actions for Undo/Redo
class UndoAction extends AbstractAction {
    private UndoManager manager;

    public UndoAction(UndoManager manager) {
        super("Undo");
        this.manager = manager;
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            manager.undo();
        } catch (CannotUndoException ex) {
            ex.printStackTrace();
        }
    }
}

class RedoAction extends AbstractAction {
    private UndoManager manager;

    public RedoAction(UndoManager manager) {
        super("Redo");
        this.manager = manager;
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            manager.redo();
        } catch (CannotRedoException ex) {
            ex.printStackTrace();
        }
    }
}
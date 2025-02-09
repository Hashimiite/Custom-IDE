package src.core;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import javax.swing.event.UndoableEditEvent;

public class UndoRedoManager extends UndoManager {
    private UndoAction undoAction;
    private RedoAction redoAction;

    public UndoRedoManager() {
        undoAction = new UndoAction(this);
        redoAction = new RedoAction(this);
        updateActions();
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        super.undoableEditHappened(e);
        updateActions();
    }

    @Override
    public void undo() {
        super.undo();
        updateActions();
    }

    @Override
    public void redo() {
        super.redo();
        updateActions();
    }

    private void updateActions() {
        undoAction.setEnabled(canUndo());
        redoAction.setEnabled(canRedo());
    }

    public UndoAction getUndoAction() {
        return undoAction;
    }

    public RedoAction getRedoAction() {
        return redoAction;
    }
}

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
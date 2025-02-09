package src;

import src.gui.EditorWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            EditorWindow editor = new EditorWindow();
            editor.setVisible(true);
        });
    }
}

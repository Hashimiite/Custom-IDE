package src.util;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    private static final Color LIGHT_BG = new Color(255, 255, 255);
    private static final Color LIGHT_FG = new Color(0, 0, 0);
    private static final Color DARK_BG = new Color(43, 43, 43);
    private static final Color DARK_FG = new Color(169, 183, 198);
    private static final Color MONOKAI_BG = new Color(39, 40, 34);
    private static final Color MONOKAI_FG = new Color(248, 248, 242);

    public static void applyTheme(String themeName, JFrame frame) {
        switch (themeName.toLowerCase()) {
            case "dark" -> applyDarkTheme(frame);
            case "monokai" -> applyMonokaiTheme(frame);
            default -> applyLightTheme(frame);
        }
    }

    private static void applyLightTheme(JFrame frame) {
        updateComponentsRecursively(frame, LIGHT_BG, LIGHT_FG);
    }

    private static void applyDarkTheme(JFrame frame) {
        updateComponentsRecursively(frame, DARK_BG, DARK_FG);
    }

    private static void applyMonokaiTheme(JFrame frame) {
        updateComponentsRecursively(frame, MONOKAI_BG, MONOKAI_FG);
    }

    private static void updateComponentsRecursively(Container container, Color bg, Color fg) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextArea) {
                comp.setBackground(bg);
                comp.setForeground(fg);
                if (comp.getFont() != null) {
                    comp.setFont(new Font("Monospace", Font.PLAIN, comp.getFont().getSize()));
                }
            }
            if (comp instanceof Container) {
                updateComponentsRecursively((Container) comp, bg, fg);
            }
        }
    }
}

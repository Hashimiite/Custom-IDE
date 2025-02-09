package src.core;

import src.gui.EditorTab;
import javax.swing.*;
import java.util.*;

public class TabManager {
    private JTabbedPane tabbedPane;
    private List<EditorTab> tabs;

    public TabManager() {
        tabbedPane = new JTabbedPane();
        tabs = new ArrayList<>();
        createNewTab();
    }

    public void createNewTab() {
        EditorTab tab = new EditorTab();
        tabs.add(tab);
        tabbedPane.addTab("Untitled", tab);
        tabbedPane.setSelectedComponent(tab);
    }

    public EditorTab getCurrentTab() {
        return (EditorTab) tabbedPane.getSelectedComponent();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}

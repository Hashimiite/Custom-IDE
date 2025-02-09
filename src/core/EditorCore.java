package src.core;

import java.util.HashMap;
import java.util.Map;

public class EditorCore {
    private DocumentManager documentManager;
    private SyntaxHighlighter syntaxHighlighter;
    private Map<String, String> preferences;

    public EditorCore() {
        documentManager = new DocumentManager();
        syntaxHighlighter = new SyntaxHighlighter();
        preferences = new HashMap<>();
        loadDefaultPreferences();
    }

    private void loadDefaultPreferences() {
        preferences.put("tabSize", "4");
        preferences.put("fontSize", "14");
        preferences.put("fontFamily", "Monospaced");
        preferences.put("autoSave", "true");
    }

    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    public SyntaxHighlighter getSyntaxHighlighter() {
        return syntaxHighlighter;
    }
}
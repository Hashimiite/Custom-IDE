package src.core;

import javax.swing.text.*;
import java.awt.Color;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import java.awt.event.*;

public class SyntaxHighlighter {
    private Map<String, SyntaxStyle> styles;
    private Map<String, Pattern> patterns;
    private String currentLanguage;

    public SyntaxHighlighter() {
        styles = new HashMap<>();
        patterns = new HashMap<>();
        initializeDefaultStyles();
        setLanguage("java");
    }

    private void initializeDefaultStyles() {
        styles.put("keyword", new SyntaxStyle(new Color(127, 0, 85), true));
        styles.put("string", new SyntaxStyle(new Color(42, 161, 152), false));
        styles.put("comment", new SyntaxStyle(new Color(87, 166, 74), true));
        styles.put("number", new SyntaxStyle(new Color(0, 134, 179), false));
        styles.put("type", new SyntaxStyle(new Color(205, 122, 0), false));
        styles.put("method", new SyntaxStyle(new Color(0, 134, 179), true));
    }

    public void setLanguage(String language) {
        currentLanguage = language;
        patterns.clear();
        switch (language.toLowerCase()) {
            case "java" -> initializeJavaPatterns();
            case "python" -> initializePythonPatterns();
            case "javascript" -> initializeJavaScriptPatterns();
            default -> initializeJavaPatterns();
        }
    }

    private void initializeJavaPatterns() {
        patterns.put("keyword", Pattern.compile("\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)\\b"));
        patterns.put("type", Pattern.compile("\\b(String|Integer|Boolean|Double|Float|List|Map|Set|ArrayList|HashMap|HashSet)\\b"));
        patterns.put("string", Pattern.compile("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\""));
        patterns.put("comment", Pattern.compile("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/"));
        patterns.put("number", Pattern.compile("\\b\\d+\\.?\\d*\\b"));
        patterns.put("method", Pattern.compile("\\b[a-zA-Z_]\\w*\\s*(?=\\()"));
    }

    private void initializePythonPatterns() {
        patterns.put("keyword", Pattern.compile("\\b(and|as|assert|break|class|continue|def|del|elif|else|except|False|finally|for|from|global|if|import|in|is|lambda|None|nonlocal|not|or|pass|raise|return|True|try|while|with|yield)\\b"));
        patterns.put("string", Pattern.compile("(\"\"\"|'''|\"[^\"]*\"|'[^']*')"));
        patterns.put("comment", Pattern.compile("#.*"));
        patterns.put("number", Pattern.compile("\\b\\d+\\.?\\d*\\b"));
        patterns.put("method", Pattern.compile("\\bdef\\s+([a-zA-Z_]\\w*)\\s*\\("));
    }

    private void initializeJavaScriptPatterns() {
        patterns.put("keyword", Pattern.compile("\\b(break|case|catch|class|const|continue|debugger|default|delete|do|else|export|extends|finally|for|function|if|import|in|instanceof|new|return|super|switch|this|throw|try|typeof|var|void|while|with|yield|let|static|enum|await|implements|package|protected|interface|private|public)\\b"));
        patterns.put("string", Pattern.compile("(\"|')(?:\\\\.|[^\\\\])*?\\1"));
        patterns.put("comment", Pattern.compile("//.*|/\\*[^]*?\\*/"));
        patterns.put("number", Pattern.compile("\\b\\d+\\.?\\d*\\b"));
        patterns.put("method", Pattern.compile("\\b[a-zA-Z_]\\w*\\s*(?=\\()"));
    }

    public void highlight(StyledDocument doc) {
        SwingUtilities.invokeLater(() -> {
            try {
                String text = doc.getText(0, doc.getLength());
                Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
                doc.setCharacterAttributes(0, text.length(), defaultStyle, true);

                for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
                    String type = entry.getKey();
                    Pattern pattern = entry.getValue();
                    Matcher matcher = pattern.matcher(text);

                    while (matcher.find()) {
                        SyntaxStyle style = styles.get(type);
                        if (style != null) {
                            Style s = doc.addStyle(type, null);
                            StyleConstants.setForeground(s, style.color());
                            StyleConstants.setBold(s, style.bold());
                            doc.setCharacterAttributes(matcher.start(), 
                                                     matcher.end() - matcher.start(), 
                                                     s, 
                                                     true);
                        }
                    }
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateStyle(String type, Color color, boolean bold) {
        styles.put(type, new SyntaxStyle(color, bold));
    }

    public Map<String, SyntaxStyle> getStyles() {
        return new HashMap<>(styles);
    }
}

record SyntaxStyle(Color color, boolean bold) {}
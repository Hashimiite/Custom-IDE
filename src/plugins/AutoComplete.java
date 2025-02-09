package src.plugins;

import javax.swing.*;
import java.util.*;

public class AutoComplete implements Plugin {
    private Set<String> keywords;
    private Map<String, String> snippets;

    public AutoComplete() {
        keywords = new HashSet<>();
        snippets = new HashMap<>();
        loadDefaultKeywords();
        loadDefaultSnippets();
    }

    private void loadDefaultKeywords() {
        keywords.addAll(Arrays.asList(
            "public", "private", "protected", "class", "interface",
            "void", "int", "String", "boolean", "return"
        ));
    }

    private void loadDefaultSnippets() {
        snippets.put("sout", "System.out.println();");
        snippets.put("psvm", "public static void main(String[] args) {\n\n}");
        snippets.put("fore", "for (Type item : collection) {\n\n}");
    }

    public List<String> getSuggestions(String prefix) {
        return keywords.stream()
                      .filter(k -> k.startsWith(prefix))
                      .collect(java.util.stream.Collectors.toList());
    }

    public String getSnippet(String key) {
        return snippets.getOrDefault(key, "");
    }

    

    @Override
    public String getName() {
        return "AutoComplete";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

}
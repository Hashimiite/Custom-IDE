package src.util;

import java.util.HashMap;
import java.util.Map;

public class LanguageDetector {
    private static final Map<String, String> EXTENSION_MAP = new HashMap<>();
    
    static {
        EXTENSION_MAP.put("java", "java");
        EXTENSION_MAP.put("py", "python");
        EXTENSION_MAP.put("js", "javascript");
        EXTENSION_MAP.put("html", "html");
        EXTENSION_MAP.put("css", "css");
        EXTENSION_MAP.put("php", "php");
        EXTENSION_MAP.put("c", "c");
        EXTENSION_MAP.put("cpp", "cpp");
        EXTENSION_MAP.put("rb", "ruby");
        EXTENSION_MAP.put("go", "go");
    }
    
    public static String detectLanguage(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            String ext = fileName.substring(lastDot + 1).toLowerCase();
            return EXTENSION_MAP.getOrDefault(ext, "text");
        }
        return "text";
    }
}
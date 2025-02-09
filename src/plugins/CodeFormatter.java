package src.plugins;

public class CodeFormatter implements Plugin {
    private int indentSize;
    private boolean useTabs;

    public CodeFormatter() {
        indentSize = 4;
        useTabs = false;
    }

    public String format(String code) {
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        String[] lines = code.split("\n");

        for (String line : lines) {
            line = line.trim();
            
            if (line.endsWith("}")) {
                indentLevel--;
            }
            
            if (!line.isEmpty()) {
                formatted.append(getIndent(indentLevel))
                        .append(line)
                        .append("\n");
            }
            
            if (line.endsWith("{")) {
                indentLevel++;
            }
        }
        return formatted.toString();
    }

    private String getIndent(int level) {
        String singleIndent = useTabs ? "\t" : " ".repeat(indentSize);
        return singleIndent.repeat(level);
    }

    

    @Override
    public String getName() {
        return "CodeFormatter";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    
}
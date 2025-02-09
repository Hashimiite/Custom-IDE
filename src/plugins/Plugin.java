package src.plugins;

public interface Plugin {
    void initialize();
    String getName();
    String getVersion();
    void execute();
}
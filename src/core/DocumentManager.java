package src.core;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentManager {
    private Path currentFile;
    private List<Path> recentFiles;
    private boolean isDirty;

    public DocumentManager() {
        recentFiles = new ArrayList<>();
        isDirty = false;
    }

    public String openFile(Path path) throws IOException {
        currentFile = path;
        String content = Files.readString(path);
        addToRecentFiles(path);
        isDirty = false;
        return content;
    }

    public void saveFile(String content) throws IOException {
        if (currentFile != null) {
            Files.writeString(currentFile, content);
            isDirty = false;
        }
    }

    private void addToRecentFiles(Path path) {
        if (!recentFiles.contains(path)) {
            recentFiles.add(0, path);
            if (recentFiles.size() > 10) {
                recentFiles.remove(recentFiles.size() - 1);
            }
        }
    }

    public List<Path> getRecentFiles() {
        return new ArrayList<>(recentFiles);
    }
}

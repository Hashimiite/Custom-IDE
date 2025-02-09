package src.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtils {
    private static final Set<String> TEXT_EXTENSIONS = new HashSet<>(
        Arrays.asList("txt", "java", "py", "js", "html", "css", "xml", "json")
    );

    public static boolean isTextFile(Path path) {
        String fileName = path.toString().toLowerCase();
        return TEXT_EXTENSIONS.stream().anyMatch(ext -> fileName.endsWith("." + ext));
    }

    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }

    public static void createBackup(Path file) throws IOException {
        String backupName = file.toString() + "." + System.currentTimeMillis() + ".bak";
        Files.copy(file, Path.of(backupName));
    }

    public static void deleteBackups(Path directory, int keepCount) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.bak")) {
            List<Path> backups = new ArrayList<>();
            stream.forEach(backups::add);
            backups.sort((a, b) -> b.toString().compareTo(a.toString()));

            for (int i = keepCount; i < backups.size(); i++) {
                Files.delete(backups.get(i));
            }
        }
    }
}
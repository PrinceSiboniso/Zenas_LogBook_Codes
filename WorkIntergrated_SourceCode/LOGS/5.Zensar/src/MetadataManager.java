import java.util.*;

public class MetadataManager {
    private FileSystem fileSystem;
    private Map<String, String> fileMetadata;

    public MetadataManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.fileMetadata = new HashMap<>();
    }

    // Store metadata for a file
    public void storeFileMetadata(String filename, String metadata) {
        if (fileSystem.listFiles().contains(filename)) {
            fileMetadata.put(filename, metadata);
            System.out.println("Metadata stored for file: " + filename);
        } else {
            System.out.println("Error: File not found in the system.");
        }
    }

    // Retrieve metadata for a file
    public String retrieveFileMetadata(String filename) {
        String metadata = fileMetadata.get(filename);
        if (metadata != null) {
            return metadata;
        } else {
            System.out.println("Error: Metadata not found for file: " + filename);
            return null;
        }
    }

    // Update metadata for a file
    public void updateFileMetadata(String filename, String newMetadata) {
        if (fileMetadata.containsKey(filename)) {
            fileMetadata.put(filename, newMetadata);
            System.out.println("Metadata updated for file: " + filename);
        } else {
            System.out.println("Error: Metadata not found for file: " + filename);
        }
    }

    // Delete metadata for a file
    public void deleteFileMetadata(String filename) {
        if (fileMetadata.containsKey(filename)) {
            fileMetadata.remove(filename);
            System.out.println("Metadata deleted for file: " + filename);
        } else {
            System.out.println("Error: Metadata not found for file: " + filename);
        }
    }

    // List all files with metadata
    public Map<String, String> listFilesWithMetadata() {
        if (fileMetadata.isEmpty()) {
            System.out.println("No metadata available.");
        }
        return new HashMap<>(fileMetadata);
    }
}

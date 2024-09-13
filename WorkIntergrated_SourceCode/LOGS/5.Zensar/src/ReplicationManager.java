import java.io.*;
import java.net.Socket;
import java.util.*;

public class ReplicationManager {
    private FileSystem fileSystem;
    private Map<String, List<String>> fileLocations;

    public ReplicationManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.fileLocations = new HashMap<>();
    }

    public void replicateFile(String filename, byte[] fileContent) {
        List<String> replicatedServers = new ArrayList<>();
        
        for (String server : fileSystem.getServers()) {
            try {
                // Create a socket to connect to the server
                Socket socket = new Socket(server, 8080);
                // Create an output stream to send the file
                OutputStream os = socket.getOutputStream();
                // Send the file to the server
                os.write(("PUT " + filename).getBytes());
                os.write(fileContent);
                os.flush();
                // Close the socket
                socket.close();

                // Track the servers where the file was successfully replicated
                replicatedServers.add(server);
            } catch (IOException e) {
                // Handle the exception (log or track the failed replication)
                e.printStackTrace();
            }
        }

        // Update the file locations map to reflect where the file has been replicated
        fileLocations.put(filename, replicatedServers);
    }

    public void handleServerFailure(String serverId) {
        // Identify files that were stored on the failed server
        List<String> affectedFiles = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : fileLocations.entrySet()) {
            List<String> servers = entry.getValue();
            if (servers.contains(serverId)) {
                // Remove the failed server from the file's list of servers
                servers.remove(serverId);
                affectedFiles.add(entry.getKey());
            }
        }

        // Re-replicate affected files to other available servers
        for (String filename : affectedFiles) {
            byte[] fileContent = fileSystem.retrieveFile(filename);
            if (fileContent != null) {
                replicateFile(filename, fileContent);
            } else {
                // Handle the case where file content cannot be retrieved
                System.out.println("Error: Could not retrieve file " + filename + " for replication.");
            }
        }
    }

    public Map<String, List<String>> getFileLocations() {
        // Return a map of file locations for checking where files are stored
        return fileLocations;
    }
}

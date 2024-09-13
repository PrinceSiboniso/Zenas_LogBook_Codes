import java.io.*;
import java.net.Socket;
import java.util.*;

public class FileSystem {
    private List<String> servers;
    private Map<String, List<String>> fileLocations;

    public FileSystem(List<String> servers) {
        this.servers = servers;
        this.fileLocations = new HashMap<>();
    }

    public String[] getServers() {
        return servers.toArray(new String[servers.size()]);
    }

    public void createFile(String filename, byte[] fileContent) {
        // Determine the servers to store the file
        List<String> serversToStore = getServersToStore(filename);
        // Send the file to the servers for replication
        replicateFile(filename, fileContent, serversToStore);
        // Update the file locations
        fileLocations.put(filename, serversToStore);
    }

    public byte[] retrieveFile(String filename) {
        // Determine the server to retrieve the file from
        String serverToRetrieveFrom = getFileLocation(filename);
        // Retrieve the file from the server
        return retrieveFileFromServer(serverToRetrieveFrom, filename);
    }

    public List<String> listFiles() {
        // Return a list of all filenames stored in the file system
        return new ArrayList<>(fileLocations.keySet());
    }

    public byte[] retrieveFileFromServer(String server, String filename) {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(server, 8080);
            // Create an output stream to send the file request
            OutputStream os = socket.getOutputStream();
            // Send the file request to the server
            os.write(("GET " + filename).getBytes());
            os.flush();
            // Create an input stream to receive the file
            InputStream is = socket.getInputStream();
            // Receive the file from the server
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            // Close the socket
            socket.close();
            return bos.toByteArray();
        } catch (IOException e) {
            // Handle the exception
            return null;
        }
    }

    private List<String> getServersToStore(String filename) {
        // Implement a simple hash-based logic to distribute the file across servers
        // We'll hash the filename and use the result to determine servers
        int hash = filename.hashCode();
        List<String> selectedServers = new ArrayList<>();
        for (int i = 0; i < servers.size(); i++) {
            if (hash % servers.size() == i) {
                selectedServers.add(servers.get(i));
            }
        }
        // For simplicity, ensure at least 2 replicas are selected
        if (selectedServers.size() < 2) {
            selectedServers.add(servers.get((hash + 1) % servers.size()));
        }
        return selectedServers;
    }

    private String getFileLocation(String filename) {
        // Implement logic to retrieve the server where the file is located
        // If the file exists, return the first server from the file locations list
        List<String> locations = fileLocations.get(filename);
        if (locations != null && !locations.isEmpty()) {
            return locations.get(0); // Assuming the first server contains the file
        }
        return null; // File not found
    }

    private void replicateFile(String filename, byte[] fileContent, List<String> servers) {
        for (String server : servers) {
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
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace(); // Log the error for debugging
            }
        }
    }
}

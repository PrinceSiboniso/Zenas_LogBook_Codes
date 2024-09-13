import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private Set<String> registeredClients;
    private static final String STORAGE_DIR = "server_storage";  // Directory for storing files

    public Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            registeredClients = new HashSet<>();
            // Ensure the storage directory exists
            File dir = new File(STORAGE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port);
            throw e;
        }
    }

    // Start the server to accept client connections
    public void start() throws IOException {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new ServerConnection(clientSocket, this).start(); // Handle client in a new thread
            } catch (IOException e) {
                System.err.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    // Register a new client
    public void registerClient(String clientId) {
        if (registeredClients.add(clientId)) {
            System.out.println("Client " + clientId + " registered successfully.");
        } else {
            System.out.println("Client " + clientId + " is already registered.");
        }
    }

    // Handle file download request
    public void handleDownloadRequest(Socket clientSocket, PrintWriter out, String filename) throws IOException {
        File file = new File(STORAGE_DIR, filename);
        if (!file.exists()) {
            out.println("ERROR: File not found");  // Send an error message if file doesn't exist
            return;
        }

        // Send the file size to the client before starting the file transfer
        out.println(file.length());
        out.flush();

        // Proceed to send the file's content
        try (FileInputStream fis = new FileInputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();  // Ensure all data is sent before closing the stream
        } catch (IOException e) {
            System.err.println("Error sending file content: " + e.getMessage());
        }
    }

    // Handle file upload request
    public void handleUploadRequest(Socket clientSocket, String filename, int fileSize) throws IOException {
        File file = new File(STORAGE_DIR, filename);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            while (totalBytesRead < fileSize && (bytesRead = bis.read(buffer, 0, Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            System.out.println("File " + filename + " uploaded successfully.");
        } catch (IOException e) {
            System.err.println("Error storing file " + filename + ": " + e.getMessage());
        }
    }

    // Delete a file from the server
    public void deleteFile(String filename) {
        File file = new File(STORAGE_DIR, filename);
        if (file.exists() && file.delete()) {
            System.out.println("File " + filename + " deleted successfully.");
        } else {
            System.err.println("Failed to delete file " + filename + " (file not found or unable to delete).");
        }
    }

    // List all files stored on the server
    public List<String> listFiles() {
        File dir = new File(STORAGE_DIR);
        String[] files = dir.list();
        if (files != null) {
            return new ArrayList<>(List.of(files));
        }
        return Collections.emptyList();
    }

    // Search for files on the server by keyword
    public List<String> searchFiles(String keyword) {
        List<String> results = new ArrayList<>();
        File dir = new File(STORAGE_DIR);
        String[] files = dir.list((d, name) -> name.contains(keyword));  // Filter files containing the keyword
        if (files != null) {
            results.addAll(List.of(files));
        }
        return results;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(8080);
            server.start();
        } catch (IOException e) {
            System.err.println("Server encountered an error: " + e.getMessage());
        }
    }

    public void storeFile(String filename, byte[] fileContent) {
        throw new UnsupportedOperationException("Unimplemented method 'storeFile'");
    }
}

class ServerConnection extends Thread {
    private Socket clientSocket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerConnection(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String command;
            while ((command = in.readLine()) != null) {
                String[] tokens = command.split(" ");
                String action = tokens[0];

                switch (action) {
                    case "REGISTER":
                        String clientId = tokens[1];
                        server.registerClient(clientId);
                        break;
                    case "UPLOAD":
                        String filename = tokens[1];
                        int fileSize = Integer.parseInt(in.readLine());
                        server.handleUploadRequest(clientSocket, filename, fileSize);
                        break;
                    case "DOWNLOAD":
                        filename = tokens[1];
                        server.handleDownloadRequest(clientSocket, out, filename);
                        break;
                    case "DELETE":
                        filename = tokens[1];
                        server.deleteFile(filename);
                        break;
                    case "LIST":
                        List<String> files = server.listFiles();
                        for (String file : files) {
                            out.println(file);
                        }
                        out.println("LIST_END");
                        break;
                    case "SEARCH":
                        String keyword = tokens[1];
                        List<String> searchResults = server.searchFiles(keyword);
                        for (String file : searchResults) {
                            out.println(file);
                        }
                        out.println("SEARCH_END");
                        break;
                    default:
                        out.println("ERROR: Unknown command");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error in server connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}

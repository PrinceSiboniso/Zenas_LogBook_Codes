import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 8080); // Ensure this matches the server hostname and port

            // Example usage
            client.uploadFile("example.txt", "Hello World".getBytes());

            byte[] fileContent = client.downloadFile("example.txt");
            System.out.println("Downloaded content: " + new String(fileContent));

            client.deleteFile("example.txt");

            List<String> files = client.listFiles();
            System.out.println("Files on server: " + files);

            List<String> searchResults = client.searchFiles("example");
            System.out.println("Search results: " + searchResults);

            client.close();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    public Client(String hostname, int port) throws IOException {
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server");
        } catch (ConnectException e) {
            throw new IOException("Unable to connect to server at " + hostname + ":" + port, e);
        }
    }

    // Upload a file to the server
    public void uploadFile(String filename, byte[] fileContent) throws IOException {
        out.println("UPLOAD " + filename); // Inform the server of the filename
        out.println(fileContent.length); // Send the file size
        out.flush();

        OutputStream os = socket.getOutputStream();
        os.write(fileContent); // Send the file content
        os.flush();

        // Read server response
        String response = in.readLine();
        if (response == null || !response.equals("UPLOAD_SUCCESS")) {
            throw new IOException("Failed to upload file. Server response: " + response);
        }
    }

    // Download a file from the server
    public byte[] downloadFile(String filename) throws IOException {
        out.println("DOWNLOAD " + filename);
        out.flush();

        // Check the first line of response to ensure the file exists and handle non-numeric responses
        String response = in.readLine();
        if (response.startsWith("ERROR")) {
            throw new IOException("Server error: " + response);  // Error case handling
        }

        // Ensure the response is a valid integer representing the file size
        int fileSize;
        try {
            fileSize = Integer.parseInt(response);  // Parse the file size
        } catch (NumberFormatException e) {
            throw new IOException("Unexpected response from server: " + response);
        }

        byte[] fileContent = new byte[fileSize];
        InputStream is = socket.getInputStream();
        int bytesRead = 0;
        int totalBytesRead = 0;
        while (totalBytesRead < fileSize && (bytesRead = is.read(fileContent, totalBytesRead, fileSize - totalBytesRead)) != -1) {
            totalBytesRead += bytesRead;
        }
        if (totalBytesRead < fileSize) {
            throw new IOException("Failed to download the full file");
        }
        return fileContent;
    }

    // Delete a file on the server
    public void deleteFile(String filename) throws IOException {
        out.println("DELETE " + filename);
        out.flush();

        // Read server response
        String response = in.readLine();
        if (response == null || !response.equals("DELETE_SUCCESS")) {
            throw new IOException("Failed to delete file. Server response: " + response);
        }
    }

    // List all files on the server
    public List<String> listFiles() throws IOException {
        out.println("LIST");
        out.flush();

        List<String> files = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null && !line.equals("LIST_END")) {
            files.add(line);
        }
        return files;
    }

    // Search files on the server by keyword
    public List<String> searchFiles(String keyword) throws IOException {
        out.println("SEARCH " + keyword);
        out.flush();

        List<String> files = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null && !line.equals("SEARCH_END")) {
            files.add(line);
        }
        return files;
    }

    // Close the client connection
    public void close() throws IOException {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}

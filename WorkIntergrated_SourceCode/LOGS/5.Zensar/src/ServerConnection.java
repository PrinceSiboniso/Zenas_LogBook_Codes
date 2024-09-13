import java.io.*;
import java.net.*;
import java.util.List;

public class ServerConnection extends Thread {
    private Socket socket;
    private Server server;

    public ServerConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String command;
            while ((command = in.readLine()) != null) {
                String[] parts = command.split(" ");
                if (parts[0].equals("UPLOAD")) {
                    handleUpload(in, out, parts[1]);
                } else if (parts[0].equals("DOWNLOAD")) {
                    handleDownload(out, parts[1]);
                } else if (parts[0].equals("DELETE")) {
                    handleDelete(parts[1]);
                } else if (parts[0].equals("LIST")) {
                    handleList(out);
                } else if (parts[0].equals("SEARCH")) {
                    handleSearch(out, parts[1]);
                } else if (parts[0].equals("REGISTER")) {
                    handleRegister(parts[1]);
                } else {
                    out.println("ERROR Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUpload(BufferedReader in, PrintWriter out, String filename) throws IOException {
        int fileSize = Integer.parseInt(in.readLine());
        byte[] fileContent = new byte[fileSize];
        InputStream is = socket.getInputStream();
        int bytesRead = 0;
        int totalBytesRead = 0;
        while (totalBytesRead < fileSize && (bytesRead = is.read(fileContent, totalBytesRead, fileSize - totalBytesRead)) != -1) {
            totalBytesRead += bytesRead;
        }
        if (totalBytesRead != fileSize) {
            out.println("ERROR Failed to upload file");
        } else {
            server.storeFile(filename, fileContent);
            out.println("UPLOAD_SUCCESS");
        }
    }

    private void handleDownload(PrintWriter out, String filename) throws IOException {
        byte[] fileContent = server.retrieveFile(filename);
        if (fileContent != null) {
            out.println(fileContent.length);
            out.flush();
            OutputStream os = socket.getOutputStream();
            os.write(fileContent);
            os.flush();
        } else {
            out.println("ERROR File not found");
        }
    }

    private void handleDelete(String filename) {
        server.deleteFile(filename);
    }

    private void handleList(PrintWriter out) throws IOException {
        List<String> files = server.listFiles();
        for (String file : files) {
            out.println(file);
        }
        out.println("LIST_END"); // End of list indicator
    }

    private void handleSearch(PrintWriter out, String keyword) throws IOException {
        List<String> files = server.searchFiles(keyword);
        for (String file : files) {
            out.println(file);
        }
        out.println("SEARCH_END"); // End of search results indicator
    }

    private void handleRegister(String clientId) {
        server.registerClient(clientId);
    }
}

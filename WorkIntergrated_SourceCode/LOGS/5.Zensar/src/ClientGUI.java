import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI {
    private JFrame frame;
    private JTextField fileNameField;
    private JTextArea outputArea;
    private JButton uploadButton, downloadButton, deleteButton, listButton, searchButton, registerButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    public ClientGUI() {
        // Setup GUI
        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        fileNameField = new JTextField();
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");
        deleteButton = new JButton("Delete");
        listButton = new JButton("List Files");
        searchButton = new JButton("Search");
        registerButton = new JButton("Register");

        panel.add(new JLabel("File Name:"));
        panel.add(fileNameField);
        panel.add(uploadButton);
        panel.add(downloadButton);
        panel.add(deleteButton);
        panel.add(listButton);
        panel.add(searchButton);
        panel.add(registerButton);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.setVisible(true);

        // Connect to server
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputArea.append("Connected to server.\n");
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Failed to connect to server.\n");
            return;
        }

        // Event Handlers
        uploadButton.addActionListener(e -> uploadFile());
        downloadButton.addActionListener(e -> downloadFile());
        deleteButton.addActionListener(e -> deleteFile());
        listButton.addActionListener(e -> listFiles());
        searchButton.addActionListener(e -> searchFiles());
        registerButton.addActionListener(e -> registerClient());
    }

    private void uploadFile() {
        String filename = fileNameField.getText();
        if (filename.isEmpty()) {
            outputArea.append("Filename cannot be empty.\n");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            outputArea.append("File selection cancelled.\n");
            return;
        }

        File file = fileChooser.getSelectedFile();
        try {
            byte[] fileContent = readFileToByteArray(file);
            out.println("UPLOAD " + filename);
            out.println(fileContent.length);
            out.flush();

            OutputStream os = socket.getOutputStream();
            os.write(fileContent);
            os.flush();

            String response = in.readLine();
            if ("UPLOAD_SUCCESS".equals(response)) {
                outputArea.append("File uploaded successfully.\n");
            } else {
                outputArea.append("Failed to upload file. Server response: " + response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Failed to upload file.\n");
        }
    }

    private void downloadFile() {
        String filename = fileNameField.getText();
        if (filename.isEmpty()) {
            outputArea.append("Filename cannot be empty.\n");
            return;
        }

        try {
            out.println("DOWNLOAD " + filename);
            out.flush();

            String response = in.readLine();
            if (response.startsWith("ERROR")) {
                outputArea.append("Error: " + response + "\n");
                return;
            }

            int fileSize = Integer.parseInt(response);
            byte[] fileContent = new byte[fileSize];
            InputStream is = socket.getInputStream();
            int totalBytesRead = 0;
            int bytesRead;

            while (totalBytesRead < fileSize && (bytesRead = is.read(fileContent, totalBytesRead, fileSize - totalBytesRead)) != -1) {
                totalBytesRead += bytesRead;
            }

            if (totalBytesRead != fileSize) {
                outputArea.append("Error: Incomplete file download.\n");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(fileContent);
                    outputArea.append("File downloaded and saved successfully.\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Failed to download file.\n");
        }
    }

    private void deleteFile() {
        String filename = fileNameField.getText();
        if (filename.isEmpty()) {
            outputArea.append("Filename cannot be empty.\n");
            return;
        }

        out.println("DELETE " + filename);
        out.flush();

        try {
            String response = in.readLine();
            if ("DELETE_SUCCESS".equals(response)) {
                outputArea.append("File deleted successfully.\n");
            } else {
                outputArea.append("Failed to delete file. Server response: " + response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Error deleting file.\n");
        }
    }

    private void listFiles() {
        try {
            out.println("LIST");
            out.flush();
            outputArea.append("Files on server:\n");

            String line;
            while (!(line = in.readLine()).equals("LIST_END")) {
                outputArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Failed to list files.\n");
        }
    }

    private void searchFiles() {
        String keyword = fileNameField.getText();
        if (keyword.isEmpty()) {
            outputArea.append("Search keyword cannot be empty.\n");
            return;
        }

        try {
            out.println("SEARCH " + keyword);
            out.flush();
            outputArea.append("Search results:\n");

            String line;
            while (!(line = in.readLine()).equals("SEARCH_END")) {
                outputArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Failed to search files.\n");
        }
    }

    private void registerClient() {
        String clientId = JOptionPane.showInputDialog(frame, "Enter client ID:");
        if (clientId == null || clientId.isEmpty()) {
            outputArea.append("Client ID cannot be empty.\n");
            return;
        }

        out.println("REGISTER " + clientId);
        out.flush();

        try {
            String response = in.readLine();
            if ("REGISTER_SUCCESS".equals(response)) {
                outputArea.append("Client registered successfully.\n");
            } else {
                outputArea.append("Failed to register. Server response: " + response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            outputArea.append("Error registering client.\n");
        }
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }
}

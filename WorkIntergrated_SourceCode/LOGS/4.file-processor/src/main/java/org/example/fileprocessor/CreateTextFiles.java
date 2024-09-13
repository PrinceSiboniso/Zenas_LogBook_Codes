package org.example.fileprocessor;

import java.io.FileWriter;
import java.io.IOException;

public class CreateTextFiles {
    public static void main(String[] args) {
        // Specify the directory where you want to create the files
        String directoryPath = "./";

        // Example filenames and content
        String[] filenames = {"Title1.txt", "Title2.txt", "Title3.txt"};
        String[] content = {
            "This is the content of the first file. It has several words.",
            "The second file has a different set of words to count.",
            "Finally, the third file contains yet another set of words."
        };

        // Create the files with the specified content
        for (int i = 0; i < filenames.length; i++) {
            try (FileWriter writer = new FileWriter(directoryPath + filenames[i])) {
                writer.write(content[i]);
                System.out.println("Created file: " + filenames[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All files created successfully.");
    }
}

package org.example.fileprocessor;

import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountApp extends Application {

    private Label resultLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Word Count Application");

        // Label to display results
        resultLabel = new Label("Select a directory to start counting words.");

        // Button to select a directory
        Button button = new Button("Choose Directory");
        button.setOnAction(e -> selectDirectory(primaryStage));

        // Layout setup
        VBox vbox = new VBox(10, button, resultLabel);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void selectDirectory(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");

        // Show the directory chooser dialog
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            // Run the word count in a separate thread
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                String result = runWordCount(selectedDirectory);
                // Update the result label on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> resultLabel.setText(result));
            });
            executorService.shutdown();
        } else {
            resultLabel.setText("No directory selected.");
        }
    }

    private String runWordCount(File directory) {
        // Number of threads
        int numThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger totalWordCount = new AtomicInteger(0);

        // Get list of files
        File[] files = directory.listFiles((dir, name) -> name.startsWith("Title"));

        if (files == null || files.length == 0) {
            return "No files found in the directory that match the criteria.";
        }

        long startTime = System.nanoTime();

        for (File file : files) {
            Callable<Integer> task = new FileWordCounter(file);
            Future<Integer> future = executor.submit(task);

            try {
                int wordCount = future.get();
                totalWordCount.addAndGet(wordCount);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long executionTimeMillis = (endTime - startTime) / 1_000_000;

        return "Final Total Word Count: " + totalWordCount.get() + "\nExecution Time (ms): " + executionTimeMillis;
    }
}

// FileWordCounter class definition
class FileWordCounter implements Callable<Integer> {
    private final File file;

    public FileWordCounter(File file) {
        this.file = file;
    }

    @Override
    public Integer call() {
        int wordCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+"); // Split by whitespace to count words
                wordCount += words.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordCount;
    }
}
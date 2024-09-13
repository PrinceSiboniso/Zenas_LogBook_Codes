


package org.example.sortingalgorithms;

import java.util.Arrays;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SortingAlgorithmsFX extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Sorting Algorithms Performance");
        label.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Button runButton = new Button("Run Sorting Algorithms");
        runButton.setOnAction(e -> runSortingAlgorithms());

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(40);

        root.getChildren().addAll(label, runButton, textArea);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Sorting Algorithms Performance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runSortingAlgorithms() {
        int[] arraySizes = {100, 1000, 10000};
        int numRuns = 5; // Number of runs for each algorithm

        StringBuilder output = new StringBuilder();

        for (int size : arraySizes) {
            output.append("Array size: ").append(size).append("\n");
            for (int i = 0; i < numRuns; i++) {
                int[] randomArray = generateRandomArray(size);

                // Bubble Sort
                int[] bubbleSortedArray = Arrays.copyOf(randomArray, size);
                long startTime = System.currentTimeMillis();
                BubbleSort.sort(bubbleSortedArray);
                long endTime = System.currentTimeMillis();
                output.append("Bubble Sort - Run ").append(i + 1).append(": ").append(endTime - startTime).append(" ms\n");

                // Selection Sort
                int[] selectionSortedArray = Arrays.copyOf(randomArray, size);
                startTime = System.currentTimeMillis();
                SelectionSort.sort(selectionSortedArray);
                endTime = System.currentTimeMillis();
                output.append("Selection Sort - Run ").append(i + 1).append(": ").append(endTime - startTime).append(" ms\n");

                // Insertion Sort
                int[] insertionSortedArray = Arrays.copyOf(randomArray, size);
                startTime = System.currentTimeMillis();
                InsertionSort.sort(insertionSortedArray);
                endTime = System.currentTimeMillis();
                output.append("Insertion Sort - Run ").append(i + 1).append(": ").append(endTime - startTime).append(" ms\n");

                // Merge Sort
                int[] mergeSortedArray = Arrays.copyOf(randomArray, size);
                startTime = System.currentTimeMillis();
                MergeSort.sort(mergeSortedArray);
                endTime = System.currentTimeMillis();
                output.append("Merge Sort - Run ").append(i + 1).append(": ").append(endTime - startTime).append(" ms\n");

                // Quick Sort
                int[] quickSortedArray = Arrays.copyOf(randomArray, size);
                startTime = System.currentTimeMillis();
                QuickSort.sort(quickSortedArray, 0, size - 1);
                endTime = System.currentTimeMillis();
                output.append("Quick Sort - Run ").append(i + 1).append(": ").append(endTime - startTime).append(" ms\n");
            }
            output.append("\n"); // Separate results for different array sizes
        }

        textArea.setText(output.toString());
    }

    // Helper method to generate a random integer array of a given size
    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100000); // Adjust the range as needed
        }
        return array;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Separate classes for each sorting algorithm
class BubbleSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < n; i++) {
                if (arr[i - 1] > arr[i]) {
                    int temp = arr[i - 1];
                    arr[i - 1] = arr[i];
                    arr[i] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }
}

class SelectionSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
			for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
}

class InsertionSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}

class MergeSort {
    public static void sort(int[] arr) {
        if (arr.length > 1) {
            int mid = arr.length / 2;
            int[] left = Arrays.copyOfRange(arr, 0, mid);
            int[] right = Arrays.copyOfRange(arr, mid, arr.length);
            sort(left);
            sort(right);
            merge(left, right, arr);
        }
    }

    private static void merge(int[] left, int[] right, int[] arr) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] < right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }
        while (i < left.length) {
            arr[k++] = left[i++];
        }
        while (j < right.length) {
            arr[k++] = right[j++];
        }
    }
}

class QuickSort {
    public static void sort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sort(arr, low, pivotIndex - 1);
            sort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }
}



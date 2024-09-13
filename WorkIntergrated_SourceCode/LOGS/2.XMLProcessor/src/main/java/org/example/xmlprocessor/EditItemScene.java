package org.example.xmlprocessor;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class EditItemScene extends Scene {
    private final Stage stage;
    private final Scene prevScene;
    private ObservableList<Book> books;
    private int bookIndex;

    private final TextField editTitle = new TextField();
    private final TextField editAuthor = new TextField();
    private final TextField editPublisher = new TextField();
    private final TextField editPublicationYear = new TextField();

    public EditItemScene(Pane root, Book book, int bookIndex, ObservableList<Book> books, Stage stage, Scene prevScene) {
        super(root);

        this.stage = stage;
        this.prevScene = prevScene;
        this.books = books;
        this.bookIndex = bookIndex;

        GridPane layout = new GridPane();

        final Text label = new Text("Edit Book Details:");
        label.setFont(new Font("Arial", 18));
        label.setTextAlignment(TextAlignment.LEFT);
        VBox.setMargin(label, new Insets(0, 0, 0, 10));

        // Labels for text fields
        Label titleLabel = new Label("Title:");
        Label authorLabel = new Label("Author:");
        Label publisherLabel = new Label("Publisher:");
        Label publicationYearLabel = new Label("Publication Year:");

        editTitle.setText(book.getTitle());
        editTitle.setMaxWidth(300);
        editAuthor.setText(book.getAuthor());
        editAuthor.setMaxWidth(300);
        editPublisher.setText(book.getPublisher());
        editPublisher.setMaxWidth(300);
        editPublicationYear.setText(String.valueOf(book.getPublicationYear()));
        editPublicationYear.setMaxWidth(300);

        VBox vBox = new VBox(10);
        VBox formContainer = new VBox(10);
        HBox buttonContainer = new HBox(10);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(this::saveBook);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.setScene(prevScene));

        formContainer.getChildren().addAll(
                titleLabel, editTitle, 
                authorLabel, editAuthor, 
                publisherLabel, editPublisher, 
                publicationYearLabel, editPublicationYear
        );
        buttonContainer.getChildren().addAll(saveButton, cancelButton);
        vBox.getChildren().addAll(formContainer, buttonContainer);

        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(buttonContainer, HPos.CENTER);
        GridPane.setHalignment(vBox, HPos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        layout.getColumnConstraints().add(columnConstraints);

        layout.add(label, 0, 1);
        layout.add(vBox, 0, 2);
        layout.add(buttonContainer, 0, 3);

        this.setRoot(layout);
    }

    private void saveBook(ActionEvent event) {
        try {
            String title = editTitle.getText();
            String author = editAuthor.getText();
            String publisher = editPublisher.getText();
            int publicationYear = Integer.parseInt(editPublicationYear.getText());

            // Basic validation
            if (title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
                showAlert("Validation Error", "Title, Author, and Publisher fields cannot be empty.");
                return;
            }
            if (publicationYear <= 0) {
                showAlert("Validation Error", "Publication Year must be a positive integer.");
                return;
            }

            Book book = books.get(bookIndex);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setPublicationYear(publicationYear);

            books.set(bookIndex, book);
            Controller.editBook(bookIndex, book.getTitle(), book.getAuthor(), book.getPublisher(), book.getPublicationYear());
            stage.setScene(prevScene);

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for the Publication Year.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

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

public class AddItemScene extends Scene {

    private final Stage stage;
    private final Scene prevScene;
    private final Pane rootShowList;
    private final ObservableList<Book> books;

    private final TextField addTitle = new TextField();
    private final TextField addAuthor = new TextField();
    private final TextField addPublisher = new TextField();
    private final TextField addPublicationYear = new TextField();

    public AddItemScene(Pane root, Pane rootShowList, Scene prevScene, Stage stage, ObservableList<Book> books) {
        super(root);
        this.stage = stage;
        this.prevScene = prevScene;
        this.rootShowList = rootShowList;
        this.books = books;

        setupLayout();
    }

    private void setupLayout() {
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setVgap(10);

        final Text label = new Text("Add a New Book:");
        label.setFont(new Font("Arial", 18));
        label.setTextAlignment(TextAlignment.CENTER);

        VBox formContainer = createFormContainer();
        HBox buttonContainer = createButtonContainer();

        layout.add(label, 0, 0);
        layout.add(formContainer, 0, 1);
        layout.add(buttonContainer, 0, 2);

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(buttonContainer, HPos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        layout.getColumnConstraints().add(columnConstraints);

        this.setRoot(layout);
    }

    private VBox createFormContainer() {
        addTitle.setPromptText("Title");
        addAuthor.setPromptText("Author");
        addPublisher.setPromptText("Publisher");
        addPublicationYear.setPromptText("Publication Year");

        addTitle.setMaxWidth(300);
        addAuthor.setMaxWidth(300);
        addPublisher.setMaxWidth(300);
        addPublicationYear.setMaxWidth(300);

        VBox formContainer = new VBox(10);
        formContainer.getChildren().addAll(addTitle, addAuthor, addPublisher, addPublicationYear);
        formContainer.setAlignment(Pos.CENTER);

        return formContainer;
    }

    private HBox createButtonContainer() {
        Button returnButton = new Button("Go back");
        returnButton.setOnAction(e -> stage.setScene(prevScene));

        Button addButton = new Button("Add book");
        addButton.setOnAction(this::handleAddBook);

        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(returnButton, addButton);
        buttonContainer.setAlignment(Pos.CENTER);

        return buttonContainer;
    }

    private void handleAddBook(ActionEvent e) {
        try {
            String title = addTitle.getText();
            String author = addAuthor.getText();
            String publisher = addPublisher.getText();
            int publicationYear = Integer.parseInt(addPublicationYear.getText());

            if (title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled out.");
                return;
            }

            books.add(new Book(title, author, publisher, publicationYear, null));
            Controller.addBook(title, author, publisher, publicationYear);

            clearFormFields();
            stage.setScene(new ShowListScene(rootShowList, prevScene, stage));
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Please enter a valid year.");
        } catch (Exception ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage());
        }
    }

    private void clearFormFields() {
        addTitle.clear();
        addAuthor.clear();
        addPublisher.clear();
        addPublicationYear.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

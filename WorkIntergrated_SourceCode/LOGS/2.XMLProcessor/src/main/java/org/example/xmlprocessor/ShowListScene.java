package org.example.xmlprocessor;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ShowListScene extends Scene {
    private final Stage stage;
    private final Scene prevScene;

    private final TableView<Book> table = new TableView<>();
    private ObservableList<Book> books = FXCollections.observableArrayList(Controller.retrieveBooks());

    public ShowListScene(Pane root, Scene prevScene, Stage stage) {
        super(root, 600, 400);

        this.stage = stage;
        this.prevScene = prevScene;

        GridPane layout = new GridPane();

        HBox buttonContainer = new HBox(10);
        Button returnButton = new Button("Go home");
        returnButton.setOnAction(e -> stage.setScene(prevScene));

        Button addBookButton = new Button("Add new book");
        addBookButton.setOnAction((ActionEvent e) -> {
            stage.setScene(new AddItemScene(new Pane(), root, prevScene, stage, books));
        });
        buttonContainer.getChildren().addAll(returnButton, addBookButton);

        final Text label = new Text("Showing all books:");
        label.setFont(new Font("Arial", 18));
        label.setTextAlignment(TextAlignment.LEFT);
        VBox.setMargin(label, new Insets(0, 0, 0, 10));

        table.setEditable(true);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        TableColumn<Book, Void> editCol = new TableColumn<>("Edit");
        TableColumn<Book, Void> deleteCol = new TableColumn<>("Delete");

        titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        authorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        editCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        deleteCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    int bookIndex = getIndex();
                    stage.setScene(new EditItemScene(new Pane(), book, bookIndex, books, stage, getTableView().getScene()));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });

        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    int index = getIndex();
                    showAlertAndDelete(index);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        table.getColumns().addAll(titleCol, authorCol, editCol, deleteCol);
        table.setItems(books);
        table.setMaxWidth(500);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, table);
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
        setRoot(layout);
    }

    private void showAlertAndDelete(int index) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this book?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            table.getItems().remove(index);
            Controller.deleteBook(index);
        }
    }
}

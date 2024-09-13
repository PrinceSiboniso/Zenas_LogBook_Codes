package org.example.xmlprocessor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Optional;

public class SearchResultsScene extends Scene {
    private final Stage stage;
    private final Scene prevScene;
    private Pane rootAddItem;
    private final TableView<Book> table = new TableView<>();
    private ObservableList<Book> books = FXCollections.observableArrayList(Controller.retrieveBooks());
    final HBox hb = new HBox();

    public SearchResultsScene(Pane root, Pane rootAddItem, Scene prevScene, Stage stage, Book[] searchResultsBooks) {
        super(root, 600, 400);
        this.setFill(Color.RED);

        this.stage = stage;
        this.prevScene = prevScene;
        this.rootAddItem = rootAddItem;
        this.books = FXCollections.observableArrayList(searchResultsBooks);

        GridPane layout = new GridPane();

        HBox buttonContainer = new HBox(10);
        Button returnButton = new Button("Go home");
        returnButton.setOnAction(e -> stage.setScene(prevScene));

        buttonContainer.getChildren().addAll(returnButton);

        final Text label = new Text("Showing search results...");
        label.setFont(new Font("Arial", 18));
        label.setTextAlignment(TextAlignment.LEFT);
        VBox.setMargin(label, new Insets(0, 0, 0, 10));

        table.setEditable(true);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        TableColumn<Book, Void> editCol = new TableColumn<>("E");
        TableColumn<Book, Void> deleteCol = new TableColumn<>("D");

        titleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        authorCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        editCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        deleteCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

        editCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("*");

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
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }

            // private TableView<Book> getTableView() {
            //     return table;
            // }
        });

        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("-");

            {
                deleteButton.setOnAction(event -> {
                    int index = getIndex();
                    showAlertAndDelete(index);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }

            // private TableView<Book> getTableView() {
            //     return table;
            // }
        });

        table.getColumns().addAll(titleCol, authorCol, editCol, deleteCol);
        table.setItems(books);
        table.setMaxWidth(500);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label, table, hb);
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
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

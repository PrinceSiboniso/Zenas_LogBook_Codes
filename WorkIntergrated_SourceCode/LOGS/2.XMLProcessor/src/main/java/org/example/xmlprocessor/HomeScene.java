package org.example.xmlprocessor;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class HomeScene extends Scene 
{
    public HomeScene(Pane root, Stage stage, ObservableList<Book> books) 
    {
        super(root);

        // Setup for grid-based layout
        GridPane mainLayout = new GridPane();
        mainLayout.prefWidthProperty().bind(root.widthProperty());
        mainLayout.prefHeightProperty().bind(root.heightProperty());
        mainLayout.setVgap(30);
        mainLayout.setPadding(new Insets(30, 110, 30, 110));

        // Column configuration for the layout
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(200);
        mainLayout.getColumnConstraints().add(columnConstraints);

        // Add the main layout to the root pane
        root.getChildren().add(mainLayout);

        // Secondary root panes for additional scenes
        final Pane addItemRoot = new Pane();
        final Pane showListRoot = new Pane();

        // Instantiate scenes for adding items and showing the list
        AddItemScene addItemScene = new AddItemScene(addItemRoot, showListRoot, this, stage, books);
        ShowListScene showListScene = new ShowListScene(showListRoot, addItemScene, stage);

        // Background gradient setup
        RadialGradient backgroundGradient = new RadialGradient(0, 0, 0, 0, 0, true, CycleMethod.NO_CYCLE, 
            new Stop(0, Color.WHITE), new Stop(1, Color.BLACK));
        this.setFill(backgroundGradient);

        // VBox container for organizing UI components
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.TOP_CENTER);

        // HBox container for search field
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Type to search...");

        final Pane searchResultsRoot = new Pane();

        // Event handler for search on Enter key press
        searchField.setOnKeyPressed(event -> 
        {
            if (event.getCode() == KeyCode.ENTER) 
            {
                // Execute search logic
                Book[] searchResults = Controller.searchBooks(searchField.getText());

                for (Book book : searchResults) 
                {
                    System.out.println("Book Author: " + book.getAuthor());
                }

                if (searchField.getText().isEmpty()) 
                {
                    System.out.println("Search is empty");
                } 
                else 
                {
                    System.out.println("Search is not empty");
                    stage.setScene(new SearchResultsScene(searchResultsRoot, addItemRoot, this, stage, searchResults));
                }
            }
        });

        searchBox.getChildren().addAll(searchField);

        // Buttons for navigation
        Button showListButton = new Button("Show List");
        showListButton.setOnAction(e -> 
        {
            System.out.println("Switching to show list scene...");
            stage.setScene(showListScene);
        });

        Button addItemButton = new Button("Add Item");
        addItemButton.setOnAction(e -> 
        {
            System.out.println("Switching to add item scene...");
            stage.setScene(addItemScene);
        });

        vbox.getChildren().addAll(searchBox, showListButton, addItemButton);

        // Add VBox to the grid layout
        mainLayout.add(vbox, 0, 1);
    }
}

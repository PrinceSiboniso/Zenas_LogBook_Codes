package org.example.xmlprocessor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

  
public class Main extends Application  
{
	private ObservableList<Book> books = FXCollections.observableArrayList(Controller.retrieveBooks());

    public static void main(String args[])
	{          
        launch(args);     
    } 
      
    @Override    
    public void start(Stage mainStage) throws Exception 
	{
        mainStage.setTitle("XML Processor");
        mainStage.setScene(new HomeScene(new Pane(), mainStage, books));   
        mainStage.show();
    }
}
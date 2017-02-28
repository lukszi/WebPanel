package gui.view;

import dataModel.entities.Website;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.Connection;
import serverInteraction.response.GetRotationResponse;

import java.util.List;

/**
 * Created by lukas on 28.04.2016.
 * Main window including login and the main view
 */
public class MainGui extends Application
{
    private Scene rotationScene;
    private ListView<String> rotationList;
    private List<Website> rotation;
    private Button cmdAdd;
    private Button cmdRemove;
    private Button cmdEdit;
    
    private Connection connection;
    
    public static void main(String args[])
    {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //Try to login
        LoginGui gui = new LoginGui();
        
        if(!gui.loginSuccessful())
        {
            //Login not successful end application
            primaryStage.close();
            return;
        }
        
        //Initialize and show the main window
        initRotationScene();
        primaryStage.setTitle("WebPanel rotation");
        primaryStage.setScene(rotationScene);
        primaryStage.show();
    }
    
    /**
     * Initializes the main window
     */
    private void initRotationScene()
    {
        //Establish a connection
        if(connection==null)
        {
            connection = Connection.getConnection();
        }
        
        //Initialize buttons and add them to the commandBox
        initButtons();
        HBox commandBox = new HBox();
        commandBox.getChildren().add(cmdAdd);
        commandBox.getChildren().add(cmdRemove);
        commandBox.getChildren().add(cmdEdit);
        commandBox.setPadding(new Insets(10,0,0,0));
        
        //Initialize rotationList
        rotationList = new ListView<>();
        updateRotationListData();
        updateRotationList();
        rotationList.setPrefSize(300,400);
    
        VBox rotationPane = new VBox();
        rotationPane.setPadding(new Insets(10,10,10,10));
        rotationPane.getChildren().add(rotationList);
        rotationPane.getChildren().add(commandBox);
        
        rotationScene = new Scene(rotationPane,300,500);
    }
    
    /**
     * Initializes the buttons
     */
    private void initButtons()
    {
        cmdEdit = new Button("edit");
        cmdEdit.setOnAction(event ->
        {
            Website selected = rotation.get(rotationList.getSelectionModel().getSelectedIndex());
            openEditDialogue(selected);
        });
    
        cmdAdd = new Button("add");
        cmdAdd.setOnAction(event -> openAddDialogue());
    
        cmdRemove = new Button("remove");
        cmdRemove.setOnAction(event -> {
            Website selected = rotation.get(rotationList.getSelectionModel().getSelectedIndex());
            connection.removeSite(selected);
            updateRotationList();
        });
    }
    
    /**
     * Opens a dialogue to create a new website
     */
    private void openAddDialogue()
    {
        openEditDialogue(null);
    }
    
    /**
     * Opens a dialogue to edit a Website
     * @param site Website that will be edited
     */
    private void openEditDialogue(Website site)
    {
        //Clone site before changing it so it can be deleted later before reading it
        Website clone = null;
        if(site!=null)
            clone = site.clone();
        //Create new dialogue and wait for it to finish
        AddWebsiteGui addGui = new AddWebsiteGui(site);
        //Check if Website was configured successfully
        if(addGui.getWebsite()!=null)
        {
            //Check if site was edited
            if(clone != null)
            {
                //remove edited site first to avoid duplicates
                connection.removeSite(clone);
            }
            //Add new website to Server
            connection.addSite(addGui.getWebsite());
            updateRotationListData();
            updateRotationList();
        }
    }
    
    /**
     * Updates the rotationList view
     */
    private void updateRotationList()
    {
        ObservableList<String> items = FXCollections.observableArrayList();
        rotation.forEach(site ->items.add(site.getUrl()));
        rotationList.setItems(items);
    }
    
    /**
     * Gets fresh rotationList from the server
     */
    private void updateRotationListData()
    {
        GetRotationResponse resp = connection.getRotation();
        rotation = resp.rotation;
    }
}

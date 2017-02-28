package gui.view;

import dataModel.entities.DisplayGroup;
import dataModel.entities.Website;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.MalformedURLException;

/**
 * Created by Lukas Szimtenings on 09.01.2017.
 * A window that enables the User to modify or add a website
 */
class AddWebsiteGui
{
    private Website website;
    private boolean correct = false;
    private Stage editWindow;
    private Scene editScene;
    
    private Button cmdOk;
    private Button cmdCancel;
    private TextField txtUrl;
    private Button cmdRotationConfig;
    private Label lblCurrentDpGroup;
    
    /**
     * Creates a new Window that enables the user to create or modify a website
     * @param website Website that should be modified, or null if a new Website should be created
     */
    AddWebsiteGui(Website website)
    {
        if(website != null)
        {
            //Existing Website should be modified
            this.website = website;
        }
        else
        {
            //New Website should be created
            this.website = new Website();
        }
        editWindow = new Stage();
        initEditScene();
        editWindow.setTitle("Edit Website");
        editWindow.setScene(editScene);
        editWindow.showAndWait();
    }
    
    private void initEditScene()
    {
        GridPane addSitePane = new GridPane();
        
        initButtons();
        
        lblCurrentDpGroup = new Label();
        String lblText;
        if(website.getGroup() == null)
            lblText = "none";
        else
            lblText = Integer.toString(website.getGroup().getGroupID());
        lblCurrentDpGroup.setText(lblText);
        
        txtUrl = new TextField();
        if(website.getUrl()!=null)
            txtUrl.setText(website.getUrl());
        txtUrl.setPromptText("Website URL");
        
        addSitePane.add(new Label("Website Url: "),1,1);
        addSitePane.add(txtUrl,2,1);
        addSitePane.add(new Label("Current DisplayGroup: "),1,2);
        addSitePane.add(lblCurrentDpGroup,2,2);
        addSitePane.add(cmdRotationConfig,3,2);
        addSitePane.add(cmdOk,1,3);
        addSitePane.add(cmdCancel,2,3);
        
        addSitePane.setPadding(new Insets(10,10,10,10));
        addSitePane.setVgap(8);
        addSitePane.setHgap(10);
    
        editScene = new Scene(addSitePane);
    }
    
    /**
     * Initializes the Buttons
     */
    private void initButtons()
    {
        cmdOk = new Button("Save");
        cmdCancel = new Button("Cancel");
        cmdRotationConfig = new Button("Select DisplayGroup");
        
        cmdRotationConfig.setOnAction(event -> openDisplayGroupConfigurator());
        cmdOk.setOnAction(event ->
        {
            try
            {
                website.setUrl(txtUrl.getText());
            }
            catch (MalformedURLException e)
            {
                //Website String was invalid, prompt user and don't save
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Malformed URL");
                alert.setHeaderText("URL was Malformed");
                alert.setContentText("The URL you entered was malformed and could not be saved. Please enter a valid URL!");
                alert.showAndWait();
                return;
            }
            if(website.getGroup()==null)
            {
                //DisplayGroup wasn't set, prompt user and don't save
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No DisplayGroup selected");
                alert.setHeaderText("Please select a DisplayGroup");
                alert.setContentText("You didn't select a DisplayGroup, please do so!");
                alert.showAndWait();
                return;
            }
            //Everything correct, save and quit
            correct = true;
            editWindow.close();
        });
    
        cmdCancel.setOnAction(event ->
        {
            correct = false;
            editWindow.close();
        });
    }
    
    /**
     * Opens a new DisplayConfigurator window with the current DisplayGroup of the website
     */
    private void openDisplayGroupConfigurator()
    {
        DisplayGroupConfiguratorGui config = new DisplayGroupConfiguratorGui(website.getGroup());
        //Wait for the configuration tobe finished
        if(config.getDisplayGroup()!=null)
        {
            //Configuration successful, set new Group in Website and display ID on the label
            DisplayGroup grp = config.getDisplayGroup();
            website.setGroup(grp);
            lblCurrentDpGroup.setText(Integer.toString(grp.getGroupID()));
        }
    }
    
    /**
     *
     * @return The configured website or null if the configuration was cancelled
    / */
    Website getWebsite()
    {
        if(!correct)
            return null;
        return website;
    }
}

package gui.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import network.Connection;

/**
 * Created by Lukas Szimtenings on 12.01.2017.
 * A window that allows the user to enter the connection information and his login credentials
 */
class LoginGui
{
    private Stage loginWindow;
    
    private Scene loginScene;
    private Button cmdLogin;
    private Button cmdCancel;
    private TextField txtUsername;
    private PasswordField pwdUserPassword;
    private TextField txtServerAddress;
    
    private boolean success = false;
    private Connection connection;
    
    /**
     * Creates a Window that allows the user to Login
     */
    LoginGui()
    {
        loginWindow = new Stage();
        initLoginScene();
        loginWindow.setTitle("WebPanel Login");
        loginWindow.setScene(loginScene);
        loginWindow.showAndWait();
    }
    
    /**
     * initializes the login scene
     */
    private void initLoginScene()
    {
        initButtons();
        
        //Init input fields
        txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        
        pwdUserPassword = new PasswordField();
        pwdUserPassword.setPromptText("Password");
        
        txtServerAddress = new TextField();
        txtServerAddress.setPromptText("ServerAddress");
    
        GridPane loginPane = new GridPane();
        //Add all elements to the loginPane
        loginPane.add(new Label("Server: "),1,1);
        loginPane.add(txtServerAddress,2,1);
        loginPane.add(new Label("User: "),1,2);
        loginPane.add(txtUsername,2,2);
        loginPane.add(new Label("Password: "),1,3);
        loginPane.add(pwdUserPassword,2,3);
        loginPane.add(cmdLogin,1,4);
        loginPane.add(cmdCancel,2,4);
        
        //Format the loginPane
        loginPane.setPadding(new Insets(10,10,10,10));
        loginPane.setVgap(8);
        loginPane.setHgap(10);
        
        loginScene = new Scene(loginPane,260,160);
    }
    
    /**
     * initializes the buttons
     */
    private void initButtons()
    {
        cmdLogin = new Button("Login");
        cmdLogin.setOnAction(event ->
        {
            //Try to login with the entered credentials
            connection = Connection.getConnection();
            connection.configureConnection(txtServerAddress.getText(),"900",txtUsername.getText(), pwdUserPassword.getText());
            if(connection.isConfigured())
            {
                //Login info was right
                success = true;
                loginWindow.close();
            }
            else
            {
                //Login ifo was wrong
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login failed");
                alert.setHeaderText("Login was unsuccessful");
                alert.setContentText("Couldn't log in, please review your entries");
                alert.showAndWait();
            }
        });
        cmdCancel = new Button("Cancel");
        cmdCancel.setOnAction(event ->
        {
            //Abort and exit the application
            success = false;
            loginWindow.close();
        });
    }
    
    /**
     *
     * @return true if Connection was configured successfully otherwise false
     */
    boolean loginSuccessful()
    {
        return success;
    }
    
}

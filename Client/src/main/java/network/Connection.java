package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dataModel.entities.Website;
import serverInteraction.command.*;
import serverInteraction.response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by lukas on 13.04.2016.
 * Singleton that manages the connections to the server
 */
public class Connection
{
    private String serverAddress;
    private String serverPort;
    private String userName;
    private String password;
    
    private static Connection connection;
    private boolean configured = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    private Connection(){}
    public static Connection getConnection()
    {
        if(connection == null)
        {
            connection = new Connection();
        }
        return connection;
    }
    
    /**
     * Adds siteToAdd to the rotation
     * @param siteToAdd Website that will be added to the rotation
     * @return Server response or null if the connection wasn't configured
     */
    public AddSiteResponse addSite(Website siteToAdd)
    {
        if(configured)
        {
            AddSiteCommand command = new AddSiteCommand();
            command.setSite(siteToAdd);
            return (AddSiteResponse) sendCommand(command);
        }
        return null;
    }
    
    /**
     * Removes siteToRemove from the rotation
     * @param siteToRemove Website that will be removed from the rotation
     * @return Server response or null if the connection wasn't configured
     */
    public RemoveSiteResponse removeSite(Website siteToRemove)
    {
        if(configured)
        {
            RemoveSiteCommand command = new RemoveSiteCommand();
            command.setSite(siteToRemove);
            return (RemoveSiteResponse) sendCommand(command);
        }
        return null;
    }
    
    /**
     *
     * @return The server response containing the current rotation or null if connection wasn't configured
     */
    public GetRotationResponse getRotation()
    {
        if(configured)
            return (GetRotationResponse)sendCommand(new GetRotationCommand());
        return null;
    }
    
    /**
     * Fetches the Map of DisplayGroups and their corresponding IDs
     * @return Server response containing the ID to/ DisplayGroup map or null if connection wasn't configured
     */
    public GetGroupMapResponse getGroupMap()
    {
        if(configured)
            return (GetGroupMapResponse)sendCommand(new GetGroupMapCommand());
        return null;
    }
    
    /**
     * Sends a Command to the server
     * @param command Command that should be send to the server
     * @return Server response or null if connection wasn't configured
     */
    private Response sendCommand(Command command)
    {
        ObjectInputStream socketReader = null;
        ObjectOutputStream socketWriter = null;
        Socket socket = null;
        //Check if connection is properly configured
        if(!configured)
            return null;
        try
        {
            socket = new Socket(serverAddress, Integer.parseInt(serverPort));
            
            LOGGER.debug("Sending Command");
            socketWriter = new ObjectOutputStream(socket.getOutputStream());
            socketWriter.writeObject(command);
            socketWriter.flush();
            
            LOGGER.debug("Receiving Answer");
            socketReader = new ObjectInputStream(socket.getInputStream());
            Response ret = (Response)socketReader.readObject();
            socketReader.close();
            
            return ret;
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            LOGGER.error(Arrays.toString(e.getStackTrace()));
        }
        finally
        {
            //try to shutdown the connection gracefully
            try
            {
                if(socketReader!= null)
                    socketReader.close();
                if(socketWriter!=null)
                    socketWriter.close();
                if(socket != null)
                    socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Configure the connection
     * @param serverAddress Address of the server
     * @param serverPort Port of the server
     * @param user User that should get logged in
     * @param password Password of the above mentioned user
     */
    public void configureConnection(String serverAddress, String serverPort, String user, String password)
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.userName = user;
        this.password = password;
        configured = testConnection();
    }
    
    /**
     * Tests if connection can connect to the server with it's current configuration
     * @return true if successful otherwise false
     */
    private boolean testConnection()
    {
        configured = true;
        Response re = sendCommand(new GetGroupMapCommand());
        return re != null;
    }
    
    public boolean isConfigured()
    {
        return configured;
    }
}

package network;

import dataModel.repositories.DisplayGroupRepository;
import dataModel.repositories.WebsiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverInteraction.response.*;
import serverInteraction.command.AddSiteCommand;
import serverInteraction.command.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Created by lukas on 13.04.2016.
 * Thread that handles an incoming Request
 */
class RequestHandler implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private Socket socket;
    private ObjectInputStream socketReader;
    private ObjectOutputStream socketWriter;
    
    /**
     * Creates a new RequestHandler thread, reading instructions from the socket and executing them
     * @param socket Socket of the incoming connection
     */
    RequestHandler(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            //Read command from socket
            socketReader = new ObjectInputStream(socket.getInputStream());
            Command command = (Command)socketReader.readObject();
            LOGGER.debug("Received Command");
            Response response = null;
            
            //Figure out which type of command we received
            if(command.getType().equals(Command.ADDSITE))
            {
                //Add site to rotation
                (new WebsiteRepository()).saveWebsite(((AddSiteCommand)command).getSite());
                response = new AddSiteResponse();
                ((AddSiteResponse)response).successful = true;
            }
            else if(command.getType().equals(Command.REMOVESITE))
            {
                //remove site from rotation
                (new WebsiteRepository()).removeWebsite(((AddSiteCommand)command).getSite());
                response = new RemoveSiteResponse();
                ((RemoveSiteResponse)response).successfull = true;
            }
            else if(command.getType().equals(Command.GETSITEROTATION))
            {
                //Get Full site Rotation
                response = new GetRotationResponse();
                ((GetRotationResponse)response).rotation = (new WebsiteRepository()).getAll();
            }
            else if(command.getType().equals(Command.GETGROUPMAP))
            {
                //Get Mapping of groups with the corresponding ids
                response = new GetGroupMapResponse();
                ((GetGroupMapResponse)response).groupMap = (new DisplayGroupRepository()).getGroupIdMap();
            }
            //Send our response to the client and close the socket
            socketWriter = new ObjectOutputStream(socket.getOutputStream());
            socketWriter.writeObject(response);
            socketWriter.flush();
            socketReader.close();
            socketWriter.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            LOGGER.error("There was a Problem handling a command");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                //Try to gracefully shutdown the connection if there is a failure
                socketWriter.close();
                socketReader.close();
            }
            catch (IOException e)
            {
                LOGGER.error("Could not gracefully shutdown connection");
                e.printStackTrace();
            }
        }
    }
}

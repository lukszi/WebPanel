package main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import network.Server;
import siteRotation.BrowserController;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by lukas on 05.03.2016.
 * Main class: Initializes the two threads needed, the serverThread handling all network related stuff and the
 * displayThread handling the browser window
 */
public class ServerInitializer
{
    private Server server;
    private BrowserController display;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInitializer.class);
    
    public static void main(String[] args) throws IOException
    {
        ServerInitializer serverInitializer = new ServerInitializer();
        
        //Start display thread
        LOGGER.debug("Initializing Display");
        serverInitializer.initializeDisplay();
    
        //Start server thread
        LOGGER.debug("Initializing server");
        serverInitializer.initializeServer();
        
        //Loop waiting for a shutdown command
        LOGGER.debug("Waiting for input");
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            String input = scanner.nextLine();
            if(input.equals("stop"))
            {
                break;
            }
        }
        serverInitializer.server.stop();
        serverInitializer.display.stop();
    }
    
    /**
     * Creates a new Server Thread and starts it
      */
    private void initializeServer()
    {
        server = Server.getServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        LOGGER.debug("Server up and running");
    }
    
    /**
     * Creates a new Display thread and starts it
     */
    private void initializeDisplay()
    {
        display = BrowserController.getController();
        Thread displayThread = new Thread(display);
        displayThread.start();
        LOGGER.debug("Display up and running");
    }
}

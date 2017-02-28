package network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SettingsProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by lukas on 13.04.2016.
 * Server waiting for incoming connections and spawning threads for requests
 */
public class Server implements Runnable
{
    private int port;
    private boolean shutdown = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static Server server;
    
    /**
     * Creates an instance of A Server listening on the port specified in the config.properties
     */
    private Server()
    {
        //Get the port to listen on from the configuration
        port = Integer.parseInt(SettingsProvider.getSettingProvider().getProperty("serverport"));
    }

    @Override
    public void run()
    {
        ServerSocket serverSocket;
        Socket socket;
        try
        {
            //Try to bind to the configured port
            serverSocket = new ServerSocket(port);
            while(!shutdown)
            {
                //Handle incoming connections until we get the shutdown signal
                socket = serverSocket.accept();
                LOGGER.info("Receiving new Connection from " + socket.getInetAddress().toString());
                //Spawn request handler for request
                RequestHandler requestHandler = new RequestHandler(socket);
                new Thread(requestHandler).start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    
    /**
     * @return returns the Server object
     */
    public static synchronized Server getServer()
    {
        if(server == null)
        {
            server = new Server();
        }
        return server;
    }
    
    /**
     * Tells the Server thread to gracefully shutdown
     */
    public synchronized void stop()
    {
        shutdown = true;
    }
}

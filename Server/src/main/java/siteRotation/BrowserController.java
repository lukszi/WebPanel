package siteRotation;

import dataModel.repositories.WebsiteRepository;
import org.openqa.selenium.firefox.FirefoxDriver;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import util.SettingsProvider;

/**
 * Created by lukas on 05.03.2016.
 * Thread that handling the Browser
 */
public class BrowserController implements Runnable
{
    //private static final Logger LOGGER = LoggerFactory.getLogger(BrowserController.class);
    private static BrowserController controller;
    private FirefoxDriver ffDriver;
    private long standardDisplayDuration;
    private boolean shutdown = false;
    
    /**
     * Creates a new BrowserController controlling a firefox
     * should only be called from within the class to guarantee this class is singleton
     */
    private BrowserController()
    {
        //Use Gecko driver in resources to create a selenium firefox driver
        // TODO: Make this system independent by using all binaries
        System.setProperty("webdriver.gecko.driver","server\\src\\main\\resources\\drivers\\geckodriver.exe");
        ffDriver = new FirefoxDriver();
        
        //Maximise the window
        ffDriver.manage().window().maximize();
        
        //Get standard displayDuration
        SettingsProvider provider = SettingsProvider.getSettingProvider();
        standardDisplayDuration = Long.parseLong(provider.getProperty("displayduration"));
    }
    public static BrowserController getController()
    {
        if(controller == null)
        {
            controller = new BrowserController();
        }
        return controller;
    }

    @Override
    public void run()
    {
        //Open database repository
        WebsiteRepository websiteRepository = new WebsiteRepository();
        while(!shutdown)
        {
            //Until thread receives shutdown signal display websites
            websiteRepository.getAll().forEach(site->
            {
                //Check if display should be displayed
                if (site.getGroup() == null || site.getGroup().display())
                {
                    //Load website into browser
                    ffDriver.get(site.getUrl());
                    //Display site for either standard display duration or a specified duration
                    sleep(site.getDisplayDuration() == -1 ? standardDisplayDuration : site.getDisplayDuration());
                }
            });
        }
        ffDriver.close();
    }
    
    /**
     * Sleeps for a specified amount of time
     * @param time time in millisecond this thread should sleep
     */
    private void sleep(long time)
    {
        long start, end, slept;
        boolean interrupted = false;

        while(time> 0)
        {
            start=System.currentTimeMillis();
            try{
                Thread.sleep(time);
                break;
            }
            catch(InterruptedException e)
            {
                end=System.currentTimeMillis();
                slept=end-start;
                time-=slept;
                interrupted=true;
            }
        }

        if(interrupted)
        {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Tells the thread to stop so it has time to safely shutdown
     */
    public void stop()
    {
        shutdown = true;
    }
}

package util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by lukas on 12.04.2016.
 * Abstracts access to the configuration method
 */
public class SettingsProvider
{
    private Properties prop;
    private static SettingsProvider provider;
    
    /**
     * Creates a new SettingsProvider and loads the configuration properties
     */
    private SettingsProvider()
    {
        prop = new Properties();
        try
        {
            prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates or returns the existing SettingsProvider
     * @return The instance of the SettingsProvider
     */
    public static SettingsProvider getSettingProvider()
    {
        if(provider == null)
        {
            provider = new SettingsProvider();
        }
        return provider;

    }
    
    /**
     * Returns the property with the key key
     * @param key Key of the Property that should be returned
     * @return Property referenced by key
     */
    public String getProperty(String key)
    {
        return prop.getProperty(key);
    }
    
}

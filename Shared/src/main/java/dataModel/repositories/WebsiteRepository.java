package dataModel.repositories;

import dataModel.entities.Website;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas Szimtenings on 29.01.2017.
 * Repository managing the access to Websites
 */
public class WebsiteRepository extends Repository
{
    /**
     * Persists website into database
     * @param site website to be persisted
     */
    public void saveWebsite(Website site)
    {
        initSession();
        session.saveOrUpdate(site);
        endSession();
    }
    
    /**
     * Removes website from database
     * @param site website to be removed
     */
    public void removeWebsite(Website site)
    {
        initSession();
        session.remove(site);
        endSession();
    }
    
    /**
     *
     * @return List of all Websites currently in the database
     */
    public List<Website> getAll()
    {
        initSession();
        List<Website> ret = new ArrayList<>();
        
        //Query all websites from database
        Query query = session.createQuery("from website ");
        List websites = query.list();
        
        //Cast every website and get all their information from db by cloning
        websites.forEach(site -> {
            Website castSite = (Website)site;
            ret.add(castSite.clone());
        });
        
        endSession();
        return ret;
    }
}

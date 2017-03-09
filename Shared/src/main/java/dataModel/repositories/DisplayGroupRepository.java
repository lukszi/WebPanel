package dataModel.repositories;

import dataModel.entities.DisplayGroup;
import dataModel.entities.DisplayInterval;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lukas Szimtenings on 29.01.2017.
 */
public class DisplayGroupRepository extends Repository
{
    /**
     *
     * @return Map containing all DisplayGroups, mapped by their id
     */
    public Map<Integer,DisplayGroup> getGroupIdMap()
    {
        initSession();
        Map<Integer,DisplayGroup> ret = new HashMap<>();
    
        //Get List of all DisplayGroups in database
        Query query = session.createQuery("from displayGroup ");
        List groups = query.list();
        
        //Fill map and recursively instantiate all intervals in the groups by calling clone
        groups.forEach(group -> {
            DisplayGroup castGroup = (DisplayGroup)group;
            ret.put(castGroup.getGroupID(),castGroup.clone());
        });
        
        endSession();
        return ret;
    }
}

package serverInteraction.response;

import dataModel.entities.DisplayGroup;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Lukas Szimtenings on 09.01.2017.
 */
public class GetGroupMapResponse extends Response implements Serializable
{
    private static final long serialVersionUID = 7526474195623876147L;
    
    public Map<Integer,DisplayGroup> groupMap;
    
    @Override
    public String getType()
    {
        return super.GETGROUPMAP;
    }
}

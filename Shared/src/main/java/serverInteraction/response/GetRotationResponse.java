package serverInteraction.response;

import dataModel.entities.Website;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lukas on 13.04.2016.
 */
public class GetRotationResponse extends Response implements Serializable
{
    private static final long serialVersionUID = 7526472295623876148L;
    public List<Website> rotation;
    @Override
    public String getType()
    {
        return super.GETSITEROTATION;
    }
}

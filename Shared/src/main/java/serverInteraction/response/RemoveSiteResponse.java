package serverInteraction.response;

import java.io.Serializable;

/**
 * Created by lukas on 13.04.2016.
 */
public class RemoveSiteResponse extends Response implements Serializable
{
    private static final long serialVersionUID = 7526472294442776148L;
    public boolean successfull = false;
    public Exception exception;

    @Override
    public String getType()
    {
        return super.REMOVESITE;
    }

}

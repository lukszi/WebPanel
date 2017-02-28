package serverInteraction.response;

import java.io.Serializable;

/**
 * Created by lukas on 13.04.2016.
 */
public class AddSiteResponse extends Response implements Serializable
{
    private static final long serialVersionUID = 7526472292123876148L;
    public boolean successful = false;
    public Exception exception;
    @Override
    public String getType()
    {
        return super.ADDSITE;
    }
}

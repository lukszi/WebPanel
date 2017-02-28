package serverInteraction.command;

import java.io.Serializable;

/**
 * Created by Lukas Szimtenings on 09.01.2017.
 */
public class GetGroupMapCommand extends Command implements Serializable
{
    private static final long serialVersionUID = 7526472295623873147L;
    @Override
    public String getType()
    {
        return super.GETGROUPMAP;
    }
}

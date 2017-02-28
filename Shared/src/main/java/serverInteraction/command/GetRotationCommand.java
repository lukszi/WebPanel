package serverInteraction.command;

import java.io.Serializable;

/**
 * Created by lukas on 13.04.2016.
 */
public class GetRotationCommand extends Command implements Serializable
{
    private static final long serialVersionUID = 7526472295623876147L;
    @Override
    public String getType()
    {
        return super.GETSITEROTATION;
    }
}

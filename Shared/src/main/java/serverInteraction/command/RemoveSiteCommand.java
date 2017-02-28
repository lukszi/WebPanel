package serverInteraction.command;

import dataModel.entities.Website;

import java.io.Serializable;

/**
 * Created by lukas on 13.04.2016.
 */
public class RemoveSiteCommand extends Command implements Serializable
{
    private static final long serialVersionUID = 7526472294442776147L;
    public Website getSite()
    {
        return site;
    }

    public void setSite(Website site)
    {
        this.site = site;
    }

    private Website site;

    @Override
    public String getType()
    {
        return super.REMOVESITE;
    }

}

package dataModel.entities;

import lombok.Data;
import org.apache.commons.validator.routines.UrlValidator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * Created by lukas on 07.03.2016.
 * POJO representing a Website in the rotation
 */
@Entity(name="website")
@Data
public class Website implements Serializable
{
    private static final long serialVersionUID = 7526472293823876147L;
    @Id
    @GeneratedValue
    private Integer id;
    //Url of the website
    private String url;
    private long displayDuration = -1;
    @ManyToOne
    //DisplayGroup determining the websites rotation schedule
    private DisplayGroup group;
    
    /**
     * Sets this websites url
     * @param url The url to be set on the website
     * @throws MalformedURLException Checks if url is in the right format, if it isn't an error is thrown
     */
    public void setUrl(String url) throws MalformedURLException
    {
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(url))
        {
            this.url = url;
        }
        else
        {
            throw new MalformedURLException();
        }

    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Website website = (Website) o;

        return url.equals(website.url);

    }
    
    /**
     *
     * @return a clone of the website
     */
    public Website clone()
    {
        Website clone = new Website();
        if(getGroup()!=null)
        {
            clone.setGroup(getGroup().clone());
        }
        try
        {
            clone.setUrl(getUrl());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return clone;
    }
}

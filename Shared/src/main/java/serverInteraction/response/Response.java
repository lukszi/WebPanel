package serverInteraction.response;

import java.io.Serializable;

/**
 * Created by lukas on 13.04.2016.
 */
public abstract class Response implements Serializable
{
    public static final String REMOVESITE = "remove";
    public static final String ADDSITE = "addToRotation";
    public static final String GETSITEROTATION = "get";
    public static final String ADDDISPLAYGROUP = "addDisplayGroup";
    public static final String GETGROUPMAP = "getGroupMap";
    private static final long serialVersionUID = 7526472295622776133L;
    public abstract String getType();
}

package gui.util;

import dataModel.entities.DisplayInterval;

/**
 * Created by Lukas Szimtenings on 12.01.2017.
 */
public class InvalidDisplayGroupException extends Exception
{
    private ErrorType errorType;
    private DisplayInterval wrongInterval;
    public enum ErrorType
    {
        NOINTERVALL, INTERVALINVALID, STARTTIMENOTSET, ENDTIMENOTSET
    }
    
    public ErrorType getErrorType()
    {
        return errorType;
    }
    
    public void setErrorType(ErrorType errorType)
    {
        this.errorType = errorType;
    }
    
    public DisplayInterval getWrongInterval()
    {
        return wrongInterval;
    }
    
    public void setWrongInterval(DisplayInterval wrongInterval)
    {
        this.wrongInterval = wrongInterval;
    }
    
}

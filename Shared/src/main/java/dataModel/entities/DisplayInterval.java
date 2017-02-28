package dataModel.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * Created by lukas on 19.04.2016.
 */
@Data
@Entity(name="displayInterval")
public class DisplayInterval implements Serializable
{
    private static final long serialVersionUID = 7526472294123876147L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    //Day of the week this interval should be active, 1 to 7 are monday to sunday
    private Integer activeDay;
    private LocalTime displayStart;
    private LocalTime displayStop;
    
    /**
     * returns the day of the week this interval should be active
     * @return day of the week, 1 to 7 are monday to sunday
     */
    public Integer getActiveDay()
    {
        return activeDay;
    }
    
    /**
     * sets the day of the week this interval should be active
     * @param activeDay day of the week, 1 to 7 are monday to sunday
     */
    public void setActiveDay(Integer activeDay)
    {
        this.activeDay = activeDay;
    }
    
    /**
     * Checks if this interval is currently active
     * @return true if interval is active at the moment
     */
    public boolean isActive()
    {
        LocalTime currentTime = LocalTime.now();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(currentDay);
        if(currentDay == activeDay)
        {
            if(displayStart.isBefore(currentTime)&&displayStop.isAfter(currentTime))
                return true;
        }
        return false;
    }
    
    /**
     *
     * @return a clone of itself
     */
    public DisplayInterval clone()
    {
        DisplayInterval interval = new DisplayInterval();
        interval.setDisplayStop(LocalTime.parse(getDisplayStop().toString()));
        interval.setDisplayStart(LocalTime.parse(getDisplayStart().toString()));
        interval.setActiveDay(getActiveDay().intValue());
        return interval;
    }
}

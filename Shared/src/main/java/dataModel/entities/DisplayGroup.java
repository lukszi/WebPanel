package dataModel.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 19.04.2016.
 */
@Data
@Entity(name="displayGroup")
public class DisplayGroup implements Serializable
{
    private static final long serialVersionUID = 7536472294123876147L;
    @Id
    @GeneratedValue
    private int groupID;
    @OneToMany()
    private List<DisplayInterval> intervals;
    
    public boolean display()
    {
        boolean ret = false;
        for(DisplayInterval interval : intervals)
        {
            ret |= interval.isActive();
        }
        return ret;
    }
    public DisplayGroup clone()
    {
        DisplayGroup clone = new DisplayGroup();
        clone.setGroupID(getGroupID());
        List<DisplayInterval> intervals = new ArrayList<>();
        this.getIntervals().forEach(intervals::add);
        clone.setIntervals(intervals);
        return clone;
    }
    public DisplayGroup()
    {
        intervals = new ArrayList<>();
    }
}

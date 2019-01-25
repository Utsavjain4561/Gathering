package com.mehulgarg.hack36;

/**
 * Created by Mehul Garg on 26-01-2019.
 */

public class Event {

    private String name, startDate, endDate, location;

    public Event()
    {

    }

    public Event(String name, String startDate, String endDate, String location)
    {
        this.name=name;
        this.startDate=startDate;
        this.endDate=endDate;
        this.location=location;
    }

    public String getEventName()
    {
        return name;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getLocation()
    {
        return location;
    }
}

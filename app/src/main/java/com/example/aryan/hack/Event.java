package com.example.aryan.hack;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class Event {
    private String eventName, startDate, endDate, location;

    public Event()
    {

    }

    public Event(String name, String startDate, String endDate, String location)
    {
        this.eventName=name;
        this.startDate=startDate;
        this.endDate=endDate;
        this.location=location;
    }

    public String getEventName()
    {
        return eventName;
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

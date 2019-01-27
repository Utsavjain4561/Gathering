package com.example.aryan.hack;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class Event {
    private String eventName, startDate, endDate, location;
    private String latitude;
    private String longitude;

    public Event()
    {

    }

    public Event(String name, String startDate, String endDate, String location,String latitude,String longitude)
    {
        this.eventName=name;
        this.startDate=startDate;
        this.endDate=endDate;
        this.location=location;
        this.latitude = latitude;
        this.longitude = longitude;
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
    public  String  getLatitude(){return latitude;}

    public String getLongitude(){return longitude;}
}

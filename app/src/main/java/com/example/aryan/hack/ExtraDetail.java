package com.example.aryan.hack;

import java.util.Date;

/**
 * Created by UTSAV JAIN on 1/14/2019.
 */

public class ExtraDetail  {
    public String latlng;
    public int upvotes;
    public Date date;

    public ExtraDetail(int upvotes,String latlng,Date date) {
        this.latlng = latlng;
        this.upvotes = upvotes;
        this.date  =date;


    }
}

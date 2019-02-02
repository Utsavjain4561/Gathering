package com.example.aryan.hack;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    public PagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {
        if(position ==0){
            return new EventFragment();
        }
        else if(position == 1){
            return new RequestListForAdmin();
        }
        else
            return new ReviewListForAdmin();

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        if(position==0)
            title = "Events";
        else if(position == 1)
            title = "Requests";
        else
            title = "Reviews";
        return title;
    }
}

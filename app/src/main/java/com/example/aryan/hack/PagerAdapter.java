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
        else{
            return new TabFragment1();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        if(position==0)
            title = "Events";
        else
            title = "Tab 2";

        return title;
    }
}

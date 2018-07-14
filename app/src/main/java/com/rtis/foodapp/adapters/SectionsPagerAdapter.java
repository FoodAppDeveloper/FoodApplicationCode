package com.rtis.foodapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rtis.foodapp.ui.fragments.EachDayFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajul on 11/18/2016.
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<String> swipeStrings = null;
    private List<String> dateStrings = null;
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionsPagerAdapter(FragmentManager fm, List<String> swipeStrings, List<String> dateStrings) {
        super(fm);
        this.swipeStrings = swipeStrings;
        this.dateStrings = dateStrings;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a EachDayFragment (defined as a static inner class below).
        //  return EachDayFragment.newInstance(position + 1);
        return EachDayFragment.newInstance(swipeStrings.get(position), dateStrings.get(position));
    }

    @Override
    public int getCount() {
        return swipeStrings.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
        }
        return null;
    }
}
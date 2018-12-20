package com.rtis.foodapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.rtis.foodapp.ui.fragments.EachDayFragment;

import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bmorris on 12/12/2018.
 */

/**
 * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.  Use of FragmentStatePagerAdapter rather than FragmentPagerAdapter since the later has fixed size.  This means it should be better handling scrolling beyond the given week.
 */
public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> swipeStrings = null;
    private List<String> dateStrings = null;
    private EachDayFragment currentFragment;

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Constructor
     *
     * @param fm the fragment manager
     * @param swipeStrings List of Strings containing the name of the fragment
     * @param dateStrings List of Strings containing date of fragment
     */
    public SectionsStatePagerAdapter(FragmentManager fm, List<String> swipeStrings, List<String> dateStrings) {
        super(fm);
        //this.swipeStrings = swipeStrings;
        //this.dateStrings = dateStrings;
        updateStrings(swipeStrings, dateStrings);
    }

    public void updateStrings(List<String> swipeStrings, List<String> dateStrings) {
        this.swipeStrings = swipeStrings;
        this.dateStrings = dateStrings;
    }

    public List<String> getSwipeStrings() { return this.swipeStrings; }
    public List<String> getDateStrings() { return this.dateStrings; }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a EachDayFragment (defined as a static inner class below).
        // return EachDayFragment.newInstance(position + 1);

        //EachDayFragment edf = new EachDayFragment();
        //edf.newInstance(swipeStrings.get(position), dateStrings.get(position));
        //return edf;
        return EachDayFragment.newInstance(swipeStrings.get(position), dateStrings.get(position));

    }

    /*@Override
    public int getItemPosition (Object object) {

        return POSITION_NONE;
    }*/

    @Override
    public int getCount() {
        return swipeStrings.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*switch (position) {
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
        */
        return swipeStrings.get(position);
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {

        return super.instantiateItem(container, position);
    }*/
}
package com.rtis.foodapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.backendless.Backendless;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.SectionsPagerAdapter;
import com.rtis.foodapp.adapters.SectionsStatePagerAdapter;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.ui.fragments.SettingsFragment;
import com.rtis.foodapp.utils.Util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;

/**
 * Sets up the main activity of the app after logging in.
 */
public class MainActivity extends AppCompatActivity{

    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private SectionsStatePagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    private String[] mDayNames;
    private int weekOffset = 0;
    private int FLAG_sliderSet = 0;
    private List<String> swipeStrings;
    private List<String> dateStrings;
    private Calendar calendar;
    private SettingsFragment settingsFragment;
    private Fragment currentFragment;
    //handle end swipes
    private boolean firstDayWeekVisible = false;
    private boolean finalDayWeekVisible = false;
    private int lastScrollState = 0;
    private int finalDayWeek = 6;
    private int mLastPos;       //last SwipeSelector position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Map ImageText object to FoodData data table
        Backendless.Data.mapTableToClass( "FoodData", ImageText.class);

        // Initialize the current week date values and fragment date names
        mDayNames = new DateFormatSymbols().getWeekdays();  //setup day of week names
        swipeStrings = new ArrayList<>(7);      //title for the day SwipeSelector
        dateStrings = new ArrayList<>(7);       //date for day SwipeSelector used for naming convention?
        //need to size string appropriately and add dummy variables
        swipeStrings = Arrays.asList("0", "1", "2", "3", "4", "5", "6");
        dateStrings = Arrays.asList("0", "1", "2", "3", "4", "5", "6");
        calendar = new GregorianCalendar();
        Calendar currentDate = Calendar.getInstance(Locale.US);
        calendar.setTime(currentDate.getTime());
        final int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1; //-1 for zero indexing
        mLastPos = currentDayOfWeek;
        //set visibility
        setDayVisibility(currentDayOfWeek);

        //initialize SwipeSelector dates
        updateSwipeSelectorInfo(calendar, weekOffset, swipeStrings, dateStrings);

        //create day fragments
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), swipeStrings, dateStrings);
        mSectionsPagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager(), swipeStrings, dateStrings);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setOffscreenPageLimit(7);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final ViewPager.OnPageChangeListener pageChangeListener =
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        swipeSelector.selectItemAt(position);
                        setDayVisibility(position);
                        Log.v("MA:PageSelected()", "position="+position);
                        //Log.v("PageSelected", "sectionDate=" + mSectionsPagerAdapter.getDateStrings());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                        //https://stackoverflow.com/questions/22674060/android-viewpager-when-swiping-on-last-screen

                        // The normal state sequence for changing pages is
                        // DRAGGING (user starts drag), SETTLING (next page is chosen), IDLE (animation is complete).
                        // No next page is chosen when dragging right on last page, or left on first page.
                        // In this case the SETTLING state is missing and the sequence is (DRAGGING, IDLE).

                        if (lastScrollState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_IDLE) {
                            // The user scrolled to an unavailable page.
                            int newDay=currentDayOfWeek;
                            if (finalDayWeekVisible) {
                                //update week +1
                                weekOffset = weekOffset + 1;
                                updateSwipeSelectorInfo(calendar, weekOffset, swipeStrings, dateStrings);
                                //setSwipeItems(swipeStrings);
                                //mViewPager.setCurrentItem(0); // set to index of Sun
                                newDay = 0;
                            } else if (firstDayWeekVisible) {
                                //update week -1
                                weekOffset = weekOffset - 1;
                                updateSwipeSelectorInfo(calendar, weekOffset, swipeStrings, dateStrings);
                                //setSwipeItems(swipeStrings);
                                //mViewPager.setCurrentItem(finalDayWeek); // set to index of Sat
                                newDay = finalDayWeek;
                            }
                            //update the Section Pager Adapter
                            mSectionsPagerAdapter.updateStrings(swipeStrings, dateStrings);
                            mViewPager.setCurrentItem(newDay);
                            Log.v("MA:ScrollStateChanged()", "hit end of week");
                        }
                        lastScrollState = state;

                        SwipeItem selectedItem = swipeSelector.getSelectedItem();
                        int value = (Integer) selectedItem.value;
                        Log.v("MA:ScrollStateChanged()", "state= " + state + " currDay=" + currentDayOfWeek + " swipeSelector.Item=" + value + " " + swipeStrings.toString());

                    }
                };

        mViewPager.addOnPageChangeListener(pageChangeListener);

        // Initialize SwipeSelector
        swipeSelector = (SwipeSelector) findViewById(R.id.conditionSelector);
        setSwipeItems(swipeStrings);
        mViewPager.setCurrentItem(currentDayOfWeek); // set to index of today

        // Swipe listener
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {

                int position = (Integer) item.value-1;

                setDayVisibility(position);
                //mViewPager.setCurrentItem(position);

                Log.v("MA:SwipeItemSelected()", "positions=" + position + " item= " + (Integer) item.value + " swipeSelector.Item=" + (Integer) swipeSelector.getSelectedItem().value);

            }
        });

        // Fragment for settings tab
        settingsFragment = SettingsFragment.newInstance();

        // Menu tab
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        // Menu bar selection listener
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);

                        // Switch between tabs pressed
                        switch (item.getItemId()) {
                            case R.id.tab_today:
                                if (currentFragment != null &&
                                        currentFragment == settingsFragment) {
                                    transaction.hide(settingsFragment);
                                    transaction.commit();

                                    currentFragment = null;
                                } else if (currentFragment == null) {
                                    // Go to today's page

                                    if(weekOffset!=0) {
                                        weekOffset = 0;
                                        updateSwipeSelectorInfo(calendar, weekOffset, swipeStrings, dateStrings);
                                        //setSwipeItems(swipeStrings);

                                        //set views
                                        mSectionsPagerAdapter.updateStrings(swipeStrings, dateStrings);

                                        Log.v("tab_today", "currDay=" + currentDayOfWeek + " " + swipeStrings.toString());

                                    }

                                    mViewPager.setCurrentItem(currentDayOfWeek);
                                    swipeSelector.selectItemAt(currentDayOfWeek,true);

                                    Log.v("tab_today", "currDay=" + currentDayOfWeek + " " + swipeStrings.toString());
                                }
                                break;
                            //case R.id.tab_coach:
                            //    break;
                            //case R.id.tab_tips:
                            //    break;
                            case R.id.tab_settings:
                                if (currentFragment != settingsFragment) {
                                    transaction.replace(R.id.frame_layout, settingsFragment);
                                    transaction.show(settingsFragment);
                                    transaction.commit();

                                    currentFragment = settingsFragment;
                                }
                                break;
                        }

                        return true;
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bottombar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void setSwipeItems(List<String> swipeStrings) {

        //create new swipe items every call
        swipeSelector.setItems(
                new SwipeItem(Util.MONDAY, swipeStrings.get(0), ""),
                new SwipeItem(Util.TUESDAY, swipeStrings.get(1), ""),
                new SwipeItem(Util.WEDNESDAY, swipeStrings.get(2), ""),
                new SwipeItem(Util.THURSDAY, swipeStrings.get(3), ""),
                new SwipeItem(Util.FRIDAY, swipeStrings.get(4), ""),
                new SwipeItem(Util.SATURDAY, swipeStrings.get(5), ""),
                new SwipeItem(Util.SUNDAY, swipeStrings.get(6), "")
        );
        System.gc();
    }

    public void updateSwipeSelectorInfo(Calendar calendar, int weekOffset, List<String> swipeStrings, List<String> dateStrings) {

        //set the calendar based on the day of the week
        Calendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance(Locale.US).getTime());
        //cal.clear(Calendar.WEEK_OF_YEAR);   // so doesn't override
        cal.add(Calendar.WEEK_OF_YEAR, weekOffset);

        //calendar.add(Calendar.WEEK_OF_MONTH, weekOffset);
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
             cal.set(Calendar.DAY_OF_WEEK, i);

            // Create everyday fragment date
            String eachDayString = mDayNames[cal.get(Calendar.DAY_OF_WEEK)] + " " +
                    cal.get(Calendar.DAY_OF_MONTH) + "/" +
                    (cal.get(Calendar.MONTH) + 1);
            swipeStrings.set(i-1,eachDayString);

            // Create meal fragment identifier for date taken
            String dateString = new SimpleDateFormat(Util.DATE_FORMAT).format(cal.getTime());
            dateStrings.set(i-1,dateString);
        }
    }

    private void setDayVisibility(int i) {
        firstDayWeekVisible = (i==0);
        finalDayWeekVisible = (i==finalDayWeek);
    }
}

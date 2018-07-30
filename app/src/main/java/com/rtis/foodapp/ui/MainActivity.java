package com.rtis.foodapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.backendless.Backendless;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.SectionsPagerAdapter;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.ui.fragments.SettingsFragment;
import com.rtis.foodapp.utils.Util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Sets up the main activity of the app after logging in.
 */
public class MainActivity extends AppCompatActivity{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    private String[] mDayNames;
    private SettingsFragment settingsFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Map ImageText object to FoodData data table
        Backendless.Data.mapTableToClass( "FoodData", ImageText.class);

        // Initialize the current week date values and fragment date names
        mDayNames = new DateFormatSymbols().getWeekdays();
        List<String> swipeStrings = new ArrayList<>();
        List<String> dateStrings = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        Calendar currentDate = Calendar.getInstance(Locale.US);
        calendar.setTime(currentDate.getTime());
        final int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);

            // Create everyday fragment date
            String eachDayString = mDayNames[calendar.get(Calendar.DAY_OF_WEEK)] + " " +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    (calendar.get(Calendar.MONTH) + 1);
            swipeStrings.add(eachDayString);

            // Create meal fragment identifier for date taken
            String dateString = new SimpleDateFormat(Util.DATE_FORMAT).format(calendar.getTime());
            dateStrings.add(dateString);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                swipeStrings, dateStrings);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(pageChangeListener);

        // Initialize SwipeSelector
        swipeSelector = (SwipeSelector) findViewById(R.id.conditionSelector);
        swipeSelector.setItems(
                new SwipeItem(Util.MONDAY, swipeStrings.get(0), ""),
                new SwipeItem(Util.TUESDAY, swipeStrings.get(1), ""),
                new SwipeItem(Util.WEDNESDAY, swipeStrings.get(2), ""),
                new SwipeItem(Util.THURSDAY, swipeStrings.get(3), ""),
                new SwipeItem(Util.FRIDAY, swipeStrings.get(4), ""),
                new SwipeItem(Util.SATURDAY, swipeStrings.get(5), ""),
                new SwipeItem(Util.SUNDAY, swipeStrings.get(6), "")
        );
        mViewPager.setCurrentItem(currentDay - 1); // set to index of today

        // Swipe listener
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {

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
                                    mViewPager.setCurrentItem(currentDay - 1);
                                }
                                break;
                            case R.id.tab_coach:
                                break;
                            case R.id.tab_tips:
                                break;
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

}

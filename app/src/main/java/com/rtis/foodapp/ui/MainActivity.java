package com.rtis.foodapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.roughike.bottombar.BottomBar;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.SectionsPagerAdapter;
import com.rtis.foodapp.ui.fragments.SettingsFragment;
import com.rtis.foodapp.utils.Util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        settingsFragment = SettingsFragment.newInstance();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

                // initialize the current week date values
        mDayNames = new DateFormatSymbols().getWeekdays();
        List<String> swipeStrings = new ArrayList<>();
        List<String> dateStrings = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        Calendar currentDate = Calendar.getInstance(Locale.US);
        calendar.setTime(currentDate.getTime());
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            String eachDayString = mDayNames[calendar.get(Calendar.DAY_OF_WEEK)] + " " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1);
            swipeStrings.add(eachDayString);
            //Log.v("Day "," " +mDayNames[calendar.get(Calendar.DAY_OF_WEEK)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+"/" + (calendar.get(Calendar.MONTH)+1));
            String dateString = new SimpleDateFormat("ddMMyy").format(calendar.getTime());
            //dateString = new SimpleDateFormat("dd").format(calendar.get(Calendar.DAY_OF_MONTH));
            dateStrings.add(dateString);
            //Log.v("Date String: ", dateString);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), swipeStrings, dateStrings);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.v("Slider","Page Selected to "+position);
                swipeSelector.selectItemAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(pageChangeListener);

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
        mViewPager.setCurrentItem(currentDay - 1);
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        Log.v(" Menu pressed", "");
                        switch (item.getItemId()) {
                            case R.id.tab_today:
                                if (currentFragment != null && currentFragment == settingsFragment) {
                                    transaction.hide(settingsFragment);
                                    transaction.commit();

                                    currentFragment = null;
                                }

                                Log.v(" Today tab pressed", "");
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
                                Log.v(" Settings tab pressed", "");
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

package com.rtis.foodapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.SectionsPagerAdapter;
import com.rtis.foodapp.utils.Util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    private String[] mDayNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mDayNames= new DateFormatSymbols().getWeekdays();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
        ArrayList<String> swipeStrings= new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        Calendar currentDate = Calendar.getInstance(Locale.US);
        calendar.setTime(currentDate.getTime());
        int currentDay=calendar.get(Calendar.DAY_OF_WEEK);
        for(int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            String eachDayString=mDayNames[calendar.get(Calendar.DAY_OF_WEEK)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+"/" + (calendar.get(Calendar.MONTH)+1);
           swipeStrings.add(eachDayString);
            //Log.v("Day "," " +mDayNames[calendar.get(Calendar.DAY_OF_WEEK)] + " "+calendar.get(Calendar.DAY_OF_MONTH)+"/" + (calendar.get(Calendar.MONTH)+1));
        }

        swipeSelector= (SwipeSelector) findViewById(R.id.conditionSelector);
        swipeSelector.setItems(
                new SwipeItem(Util.MONDAY, swipeStrings.get(0), ""),
                new SwipeItem(Util.TUESDAY,swipeStrings.get(1), ""),
                new SwipeItem(Util.WEDNESDAY,swipeStrings.get(2), ""),
                new SwipeItem(Util.THURSDAY,swipeStrings.get(3), ""),
                new SwipeItem(Util.FRIDAY,swipeStrings.get(4), ""),
                new SwipeItem(Util.SATURDAY,swipeStrings.get(5), ""),
                new SwipeItem(Util.SUNDAY,swipeStrings.get(6),"")
        );
        mViewPager.setCurrentItem(currentDay-1);
        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {

            }
        });

    }

    private void logout()
    {
        Backendless.UserService.logout(new AsyncCallback<Void>()
        {
            public void handleResponse( Void response )
            {
                // user has been logged out.
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
            }

            public void handleFault( BackendlessFault fault )
            {
                // something went wrong and logout failed, to get the error code call fault.getCode()
            }
        });
    }
}

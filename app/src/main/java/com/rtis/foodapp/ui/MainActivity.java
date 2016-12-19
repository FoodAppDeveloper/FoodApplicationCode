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

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private SwipeSelector sizeSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.v("Slider","Page Selected to "+position);
                sizeSelector.selectItemAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        sizeSelector= (SwipeSelector) findViewById(R.id.conditionSelector);
        sizeSelector.setItems(
                new SwipeItem(-1, "Day1", "DATE1"),
                new SwipeItem(Util.DAY2,"Day2", "DATE2"),
                new SwipeItem(Util.DAY3,"Day3", "DATE3"),
                new SwipeItem(Util.DAY4,"Day4", "DATE4"),
                new SwipeItem(Util.DAY5,"Day5", "DATE5"),
                new SwipeItem(Util.DAY6,"Day", "DATE6"),
                new SwipeItem(Util.DAY7,"Day7","DATE7")
        );

        sizeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
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

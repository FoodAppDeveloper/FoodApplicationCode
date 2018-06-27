package com.rtis.foodapp.adapters;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rtis.foodapp.R;
import com.rtis.foodapp.ui.fragments.TimePickerFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.Time;
import java.util.Calendar;

/**
 * Created by rajul on 1/14/2017.
 */

public class EachMealSectionAdapter extends PagerAdapter implements TimePickerDialog.OnTimeSetListener {

    private final FragmentManager fragmentManager;
    private Context mContext;
    private TextView timeTextView=null;
    private File imageFile;
    private ImageView mImageView;
    private String timeText;
    //private TimePickerFragment timeFragment;

    private final String HOUR = "Hour";
    private final String MINUTE = "Minute";

    public EachMealSectionAdapter(Context context, FragmentManager fm, File file) {
        mContext = context;
        fragmentManager=fm;
        imageFile = file;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        //ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.demo, collection, false);
        collection.addView(layout);
        timeTextView=(TextView)layout.findViewById(R.id.timeTextView);


        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        setTime(hour,minute);

        LinearLayout timeView = (LinearLayout)layout.findViewById(R.id.timeView);

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.v("TimeView","Time View clicked");
                //timeFragment = new TimePickerFragment();
                //timeFragment.show(fragmentManager,"TimePicker");

                //timeText = timeFragment.getTimeSet();
                timeTextView = (TextView) view.findViewById(R.id.timeTextView);
                //timeTextView.setText(timeText);

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                //Create and return a new instance of TimePickerDialog
                TimePickerDialog tm = new TimePickerDialog(mContext, EachMealSectionAdapter.this, hour, minute,
                        DateFormat.is24HourFormat(mContext));
                tm.show();

                /*
                Bundle bundle = new Bundle();
                bundle.putInt(HOUR, hour);
                bundle.putInt(MINUTE, minute);
                TimePickerFragment timeFragment = new TimePickerFragment();
                timeFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(timeFragment, "TimePicker");
                transaction.commit();

                timeText = timeFragment.getTimeSet();
                timeTextView.setText(timeText);*/
            }
        });

        /*if (timeFragment != null) {
            timeText = timeFragment.getTimeSet();
            timeTextView.setText(timeText);
        }*/

        mImageView = (ImageView) layout.findViewById(R.id.capturedImage);
        if (imageFile != null) {
            Picasso.with(mContext).load(imageFile).into(mImageView);
        } else {
            // take photo button?
        }

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    private void setTime(int hourOfDay,int minutes )
    {
        StringBuilder time=new StringBuilder("");
        String aMpM = "AM";
        if (hourOfDay > 11) {
            aMpM = "PM";
        }
        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if (hourOfDay > 12) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }
        if(currentHour<10)
            time.append("0").append(Integer.toString(currentHour)).append(":");
        else
            time.append(Integer.toString(currentHour)).append(":");
        if(minutes<10)
            time.append("0").append(Integer.toString(minutes)).append(" ");
        else
            time.append(Integer.toString(minutes)).append(" ");
        time.append(aMpM);
       // Log.v("Time","Time Selected");
        if(timeTextView!=null) {
           // Log.v("Time12","Time Selected"+ time.toString());
            timeTextView.setText(time.toString());
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setTime(hourOfDay,minute);
    }

}

package com.rtis.foodapp.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by rajul on 1/17/2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment { // implements TimePickerDialog.OnTimeSetListener {

    private int hour;
    private int minute;
    private String timeText;
    private TimePickerDialog timePicker;
    private Handler handler;

    public void setHandler(Handler h) {
        handler = h;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current time as the default values for the time picker

        /*if (savedInstanceState == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }*/

        Bundle bundle = getArguments();
        hour = bundle.getInt("Hour");
        minute = bundle.getInt("Minute");
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                hour = hourOfDay;
                minute = minutes;
                Bundle b = new Bundle();
                b.putInt("Hour", hour);
                b.putInt("Minute", minute);
                //Message msg = new Message();
                //msg.setData(b);
                //handler.sendMessage(msg);
            }
        };
        return new TimePickerDialog(getActivity(), listener, hour, minute, false);

        //Create and return a new instance of TimePickerDialog
        /*
        timePicker = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        timePicker.show();
        return timePicker;*/
    }

    //onTimeSet() callback method


    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
        //Do something with the user chosen time
        //Get the AM or PM for current time
        /*
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
        }*/
        hour = hourOfDay;
        minute = minutes;

        StringBuilder time = new StringBuilder("");
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
        if(currentHour < 10)
            time.append("0").append(Integer.toString(currentHour)).append(":");
        else
            time.append(Integer.toString(currentHour)).append(":");
        if(minutes < 10)
            time.append("0").append(Integer.toString(minutes)).append(" ");
        else
            time.append(Integer.toString(minutes)).append(" ");

        time.append(aMpM);

        timeText = time.toString();
    }

    public String getTimeSet() {
        StringBuilder time = new StringBuilder("");
        String aMpM = "AM";
        if (hour > 11) {
            aMpM = "PM";
        }
        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if (hour > 12) {
            currentHour = hour - 12;
        } else {
            currentHour = hour;
        }
        if(currentHour < 10)
            time.append("0").append(Integer.toString(currentHour)).append(":");
        else
            time.append(Integer.toString(currentHour)).append(":");
        if(minute < 10)
            time.append("0").append(Integer.toString(minute)).append(" ");
        else
            time.append(Integer.toString(minute)).append(" ");

        time.append(aMpM);

        return time.toString();
    }

}

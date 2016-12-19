package com.rtis.foodapp.utils;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rtis.foodapp.BuildConfig;


/**
 * Custom Logger class to print app level logs
 * 
  
 */
public class Logger
{
	public static final String TAG="Food App";

	//Boolean to enable logging
	public static final boolean LOGGING_ENABLED = true;

	//This is the static boolean which has to be strictly changed during production to false
	//So as to disable logging
	//move this to BuildConfig.DEBUG condition

	/**
	 * Internal Logger , maintain a bool to disable or enable debugging of logs
	 * @param content
	 */
	public static void v(String content)
	{
		v(TAG, content);
	}

	/**
	 * Internal Logger , maintain a boolean to disable or enable debugging of logs
	 * @param content
	 */
	public static void v(String tag , String content)
	{
		if( BuildConfig.DEBUG && LOGGING_ENABLED )
		{
			Log.v(tag, "" + content);
		}
	}

	/**
	 * Check if user is passing Activity context by mistake if so get Application object out of it
	 *
	 * Treat it as application context , or else catch exception
	 * @param context Application context
	 * @param mesg Toast mesg to be shown
	 */
	public static void toast(final Context context , final String mesg)
	{
		if( BuildConfig.DEBUG && LOGGING_ENABLED )
		{
			try
			{
				if (context instanceof Activity) //Check if user is passing Activity context by mistake if so get Application object out of it
				{
					Toast.makeText(context.getApplicationContext(), mesg, Toast.LENGTH_SHORT).show();
				}
				else //Treat it as application context
				{
					Toast.makeText(context, mesg, Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				Logger.v("Possibly a leak or typecast exception in context from Activity to Application");
			}
		}
	}
}

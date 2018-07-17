package com.rtis.foodapp.adapters;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.QueryOptions;
import com.rtis.foodapp.R;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by rajul on 1/14/2017.
 */

public class EachMealSectionAdapter extends PagerAdapter implements TimePickerDialog.OnTimeSetListener {

    private Context mContext;
    private TextView timeTextView=null;
    private PopupWindow mPopupWindow;
    private ImageView mImageView;
    private List<String> times;
    private String meal_name;
    private List<ImageText> imageTextList;
    private File mCurrentFile;
    private String fragmentDate;

    private final String HOUR = "Hour";
    private final String MINUTE = "Minute";

    public EachMealSectionAdapter(Context context, List<ImageText> list, String meal, String date) {
        mContext = context;
        times = new ArrayList<>();
        meal_name = meal;
        imageTextList = list;
        fragmentDate = date;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
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

        final View v = layout.getRootView();
        ImageButton editButton = (ImageButton) layout.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle keyboard input
                addText(position);
            }
        });

        mImageView = (ImageView) layout.findViewById(R.id.capturedImage);
        if (imageTextList.size() > 0) {
            loadImageView(position);
        }

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return imageTextList.size();
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

    private void addText(final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.description_popup, null);
        mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        ConstraintLayout mLayout = (ConstraintLayout) customView.findViewById(R.id.popup_2);
        final EditText textView = (EditText) customView.findViewById(R.id.description);

        // if file exists, show text
        if (!imageTextList.get(position).isTextEmpty()) {
            StringBuffer data = new StringBuffer();
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                URL url = new URL(imageTextList.get(position).getTextFile());
                URLConnection uc1 = url.openConnection();
                uc1.setDoInput(true);

                // read text returned by server
                BufferedReader in = new BufferedReader(new InputStreamReader(uc1.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    data.append(line);
                    data.append('\n');
                }
                in.close();

            }
            catch (MalformedURLException e) {
                Log.v(" Text Malformed URL: ", e.getMessage());
            }
            catch (IOException e) {
                Log.v(" Fetch Text I/O Error: ", e.getMessage());
            }

            /*try {
                FileReader fr = new FileReader(imageTextList.get(position).getTextFile());
                BufferedReader br = new BufferedReader(fr);
                String line;
                while((line = br.readLine()) != null) {
                    data.append(line);
                    data.append("\n");
                }
            } catch (IOException e) {
                Util.showToast(mContext, "Text failed to open.");
            }*/

            textView.setText(data);
        }

        //final String data = textView.getText().toString();

        Button doneButton = (Button) customView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();

                // Save text to file
                String data = textView.getText().toString();

                String image = imageTextList.get(position).getImageFile();
                String imageName = image.substring(image.indexOf("JPEG_") + 5, image.length() - 4);

                String fileName = "TXT_" + imageName + ".txt";

                File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

                try {
                    File file = new File(storageDir, fileName);
                    FileWriter writer = new FileWriter(file);
                    writer.append(data);
                    writer.flush();
                    writer.close();
                    mCurrentFile = file;
                    uploadTextFile(position);
                    //imageTextList.get(position).setTextFile(file.getAbsolutePath());

                } catch (IOException e) {
                    Util.showToast(mContext, "Text failed to save.");
                }

                /*if (imageTextList.get(position).isTextEmpty()) {
                    // If doesn't exist, create new file
                    File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

                    try {
                        File file = new File(storageDir, fileName);
                        FileWriter writer = new FileWriter(file);
                        writer.append(data);
                        writer.flush();
                        writer.close();
                        mCurrentFile = file;
                        uploadTextFile(position);
                        //imageTextList.get(position).setTextFile(file.getAbsolutePath());

                    } catch (IOException e) {
                        Util.showToast(mContext, "Text failed to save.");
                    }
                } else {
                    // If exists, pull file from database
                    try {
                        // Code derived from: https://stackoverflow.com/questions/16585728/write-data-to-a-text-file-on-url-in-android
                        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        //StrictMode.setThreadPolicy(policy);

                        URL u1 = new URL(imageTextList.get(position).getTextFile());
                        URLConnection uc1 = u1.openConnection();
                        uc1.setDoOutput(true);
                        OutputStreamWriter out = new OutputStreamWriter(uc1.getOutputStream());
                        out.append(data);
                        out.flush();
                        out.close();
                        //ImageText.updateImageText(imageTextList.get(position));
                    } catch (IOException e) {
                        Util.showToast(mContext, "Text failed to save.");
                    }
                }*/
                //ImageText.updateImageText(imageTextList.get(position));
                //notifyDataSetChanged();
            }
        });

        mPopupWindow.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
    }

    private void uploadTextFile(final int position) {
        Backendless.Files.upload(mCurrentFile, Defaults.FILES_IMAGETEXT_DIRECTORY, true,
                new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                imageTextList.get(position).setTextFile(backendlessFile.getFileURL());
                Logger.v(" Text File Url " + backendlessFile.getFileURL());
                //imageTextList.set(position, imageText);
                ImageText.updateImageText(imageTextList.get(position));
                mCurrentFile.delete();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v(" Text Upload Failed fault " + backendlessFault.toString());
                Logger.v(" Text Upload Failed " + backendlessFault.getDetail());
                Logger.v(" Text Upload Failed message " + backendlessFault.getMessage());
            }

        });
    }

    private Bitmap getImageBitmap(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            return BitmapFactory.decodeStream(new URL(url).openStream());
        } catch(IOException e) {
            //Log.v(e.getMessage());
            return null;
        }

    }

    private void loadImageView(final int position) {

        String fileURL = imageTextList.get(position).getImageFile();
        if (fileURL.isEmpty()) {
            // show image not found txt
        } else {
            Picasso.with(mContext).load(fileURL).rotate(90).into(mImageView);
            Logger.v(" Image File URL: ", fileURL);
            Logger.v(" Number of image files: " + imageTextList.size());
        }

        //String ownerID = "ownerId = '" + Backendless.UserService.CurrentUser().getUserId() + "'";
        //String date = "fragmentDate = " + fragmentDate;
        //String meal = "meal = " + meal_name;
        /*
        String whereClause = "ownerId = '" + Backendless.UserService.CurrentUser().getUserId() +
                "' and fragmentDate = '" + fragmentDate + "' and meal = '" + meal_name + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("created");
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(ImageText.class).find(queryBuilder,
                new AsyncCallback<List<ImageText>>() {
                    @Override
                    public void handleResponse(final List<ImageText> itList) {
                        if(itList.isEmpty()) {
                            Logger.v(" No Image Files Queried.");
                        } else if (itList.size() == 1) {
                            imageTextList = itList;
                            String fileURL = itList.get(0).getImageFile();
                            Picasso.with(mContext).load(fileURL).rotate(90).into(mImageView);
                            Logger.v(" Retrieved Image File URL: ", fileURL);
                            Logger.v(" Number of image files: " + itList.size());
                        } else {
                            imageTextList = itList;
                            String fileURL = imageTextList.get(position).getImageFile();
                                //try {
                                    ExifInterface exif = new ExifInterface(getImageBitmap(fileURL));
                                    String x = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                                    Logger.v(" Image Orientation: " + x);
                                } catch (IOException e) {

                                }
                            // Picasso auto rotates image by 90 degrees when EXIF orientation is 90
                            Picasso.with(mContext).load(fileURL).rotate(90).into(mImageView);

                            Logger.v(" Retrieved Image File URL: ", fileURL);
                            Logger.v(" Number of image files: " + itList.size());
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Logger.v(" Failed to retrieve image file URL", backendlessFault.getMessage());
                    }

                }); */
    }


}

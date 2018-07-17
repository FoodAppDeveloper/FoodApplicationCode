package com.rtis.foodapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.rtis.foodapp.R;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajul on 1/14/2017.
 */

public class EachMealSectionAdapter extends PagerAdapter {

    private Context mContext;
    private TextView timeTextView=null;
    private PopupWindow mPopupWindow;
    private ImageView mImageView;
    private String meal_name;
    private List<ImageText> imageTextList;
    private File mCurrentFile;
    private String fragmentDate;

    public EachMealSectionAdapter(Context context, List<ImageText> list, String meal, String date) {
        mContext = context;
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
        //timeTextView=(TextView)layout.findViewById(R.id.timeTextView);

        timeTextView = (TextView) layout.findViewById(R.id.timeText);

        String timestamp = "00:00";
        try {
            timestamp = new SimpleDateFormat("hh:mm aaa").format(imageTextList.get(position).getCreated());
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        timeTextView.setText(timestamp);

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

            textView.setText(data);
        }

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

                } catch (IOException e) {
                    Util.showToast(mContext, "Text failed to save.");
                }
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

    }


}

package com.rtis.foodapp.adapters;

import android.content.Context;
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
import java.util.Date;
import java.util.List;

/**
 * Created by rajul on 1/14/2017.
 *
 * Adapter class to show images in each meal fragment when pressed and handle text input.
 * Extends PagerAdapter.
 */
public class EachMealSectionAdapter extends PagerAdapter {

    // Used to get substring of image file name
    private static int FRONT_IDX = 5;
    private static int BACK_IDX = 4;

    // Used for rotating image 90 degrees
    private static int ROTATE_DEGREES = 90;

    // View variables
    private Context mContext;
    private TextView timeTextView=null;
    private PopupWindow mPopupWindow;
    private ImageView mImageView;
    private TextView uploadTextView;

    private List<ImageText> imageTextList;
    private File mCurrentFile; // Needed to upload to backendless

    /**
     * Constructor to initiate variables.
     *
     * @param context the Context in which the adapter exists
     * @param list List of ImageText to show
     */
    public EachMealSectionAdapter(Context context, List<ImageText> list) {
        mContext = context;
        imageTextList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.demo, collection, false);
        collection.addView(layout);

        // Set up text view to display loading state of image
        uploadTextView = (TextView) layout.findViewById(R.id.uploading);

        // Set up timestamp. Defaults to 00:00 when nothing is image hasnt finished
        // uploading/downloading. Otherwise, shows the time the image was taken.
        timeTextView = (TextView) layout.findViewById(R.id.timeText);
        String timestamp = "00:00";
        try {
            timestamp = new SimpleDateFormat(Util.TIMESTAMP_FORMAT).format(
                    imageTextList.get(position).getCreated());
        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (NullPointerException e) {

        }
        timeTextView.setText(timestamp);

        // Set up edit text button and listener
        ImageButton editButton = (ImageButton) layout.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(position);
            }
        });

        // Show captured image
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

    /**
     * Method to handle text input for each image.
     *
     * @param position the position of ImageText in list to save text file for
     */
    private void addText(final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View customView = inflater.inflate(R.layout.description_popup, null);

        // Set up popup window to hold text
        mPopupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        ConstraintLayout mLayout = (ConstraintLayout) customView.findViewById(R.id.popup_2);
        final EditText textView = (EditText) customView.findViewById(R.id.description);

        // If file exists, show text first
        if (!imageTextList.get(position).isTextEmpty()) {
            StringBuffer data = new StringBuffer();
            try {
                // Need to run on separate thread to connect to URL.
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().
                        permitAll().build();
                StrictMode.setThreadPolicy(policy);

                // Connect to file location
                URL url = new URL(imageTextList.get(position).getTextFile());
                URLConnection uc1 = url.openConnection();
                uc1.setDoInput(true);

                // Read text returned by server
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

        // Create done button and listener for when user finishes typing and needs to save text.
        Button doneButton = (Button) customView.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();

                // Save text to file
                String data = textView.getText().toString();

                String image = imageTextList.get(position).getImageFile();
                String imageName = image.substring(image.indexOf(Util.IMAGE_PREFIX) + FRONT_IDX,
                        image.length() - BACK_IDX);

                // Uses same format as image name: TXT_<ddMMyyHHmmss>_<meal>_<unique#>.txt
                String fileName = Util.TEXT_PREFIX + imageName + Util.TEXT_SUFFIX;

                File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

                try {
                    // Write to file and upload
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

    /**
     * Uploads text file to database and saves ImageText object.
     *
     * @param position the position of ImageText in list to save text file for
     */
    private void uploadTextFile(final int position) {
        Backendless.Files.upload(mCurrentFile, Defaults.FILES_IMAGETEXT_DIRECTORY, true,
                new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                Logger.v(" Text File Url " + backendlessFile.getFileURL());

                imageTextList.get(position).setTextFile(backendlessFile.getFileURL());
                ImageText.updateImageText(imageTextList.get(position));

                // delete temporary local file
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

    /**
     * Helper method to load the image into view using Picasso library.
     *
     * @param position the position of ImageText in list to load image for
     */
    private void loadImageView(final int position) {

        String fileURL = imageTextList.get(position).getImageFile();

        if (!fileURL.isEmpty()) {
            // Picasso auto rotates portrait images, so rotate back
            Picasso.with(mContext).load(fileURL).rotate(ROTATE_DEGREES).into(mImageView);
            Logger.v(" Image File URL: ", fileURL);
            Logger.v(" Number of image files: " + imageTextList.size());

            // Set timestamp view for image or set to current time if image hasn't loaded yet
            String timestamp;
            try {
                timestamp = new SimpleDateFormat(Util.TIMESTAMP_FORMAT).format(
                        imageTextList.get(position).getCreated());
                timeTextView.setText(timestamp);
                Logger.v(" Set Timestamp to", timestamp);
            } catch (ArrayIndexOutOfBoundsException e) {
                Date time = new Date();
                timestamp = new SimpleDateFormat(Util.TIMESTAMP_FORMAT).format(time);
            } catch (NullPointerException e) {
                Date time = new Date();
                timestamp = new SimpleDateFormat(Util.TIMESTAMP_FORMAT).format(time);
            }
            timeTextView.setText(timestamp);

            uploadTextView.setText(R.string.loading_message);
            mImageView.bringToFront();              // Show image on top of text
        }

    }

    /**
     * Helper method to notify Adapter when new image is taken and load image.
     *
     * @param it new List of ImageText objects to display
     * @param position position of ImageText in List to load
     */
    public void refresh(List<ImageText> it, int position) {
        imageTextList = it;
        notifyDataSetChanged();
        loadImageView(position);
    }

    /**
     * Reset the ImageText List and notify Adapter of change
     *
     * @param it new List of ImageText objects
     */
    public void setImageTextList(List<ImageText> it) {
        imageTextList = it;
        notifyDataSetChanged();
    }

}

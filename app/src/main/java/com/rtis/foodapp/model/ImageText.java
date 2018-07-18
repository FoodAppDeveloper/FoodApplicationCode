package com.rtis.foodapp.model;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.ui.RegisterActivity;
import com.rtis.foodapp.ui.fragments.EachMealFragment;
import com.rtis.foodapp.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weborb.service.MapToProperty;

/**
 * Class that stores the image file and text file associated with it.
 * Implements Parcelable to save in EachMealFragment.
 */
public class ImageText implements Parcelable {

    private String imageFile;
    private String textFile;
    private String meal;
    private String fragmentDate;
    private String objectId;
    private Date created;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageFile);
        dest.writeString(textFile);
    }

    public static final Parcelable.Creator<ImageText> CREATOR =
            new Parcelable.Creator<ImageText>() {
        public ImageText createFromParcel(Parcel in) {
            return new ImageText(in);
        }

        public ImageText[] newArray(int size) {
            return new ImageText[size];
        }
    };

    private ImageText(Parcel in) {
        imageFile = in.readString();
        textFile = in.readString();
    }

    public ImageText() {
        imageFile = "";
        textFile = "";
    }

    public ImageText(String meal, String date) {
        imageFile = "";
        textFile = "";
        this.meal = meal;
        this.fragmentDate = date;
    }

    public Date getCreated() {
        return created;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMeal(){
        return meal;
    }

    public void setObjectId(String id) {
        objectId = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setDate(String date) {
        fragmentDate = date;
    }

    public String getFragmentDate() {
        return fragmentDate;
    }

    public boolean isTextEmpty() {
        return (textFile == null || textFile.isEmpty() || textFile.equals(""));

    }

    public String getImageFile() {
        return imageFile;
    }

    public String getTextFile() {
        return textFile;
    }

    public void setImageFile(String file) {
        imageFile = file;
    }

    public void setTextFile(String file) {
        textFile = file;
    }

    public static void saveImageText(ImageText it) {
        Backendless.Data.of(ImageText.class).save(it, new AsyncCallback<ImageText>() {
            @Override
            public void handleResponse(ImageText response) {
                Logger.v(" ImageText object saved ");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.v(" ImageText Save Failed: " + fault.getMessage());
            }
        });
    }

    public static void updateImageText(ImageText it){
        Backendless.Persistence.of(ImageText.class).save(it, new AsyncCallback<ImageText>()
        {
            @Override
            public void handleResponse( ImageText person ) {
                Logger.v(" ImageText object updated ");
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                Logger.v(" ImageText Update Failed: " + fault.getMessage());
            }
        } );

    }

}

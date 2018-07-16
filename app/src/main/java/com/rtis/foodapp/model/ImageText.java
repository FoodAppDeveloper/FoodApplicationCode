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
import java.util.Date;

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

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMeal(){
        return meal;
    }

    public void setDate(String date) {
        fragmentDate = date;
    }

    public String getFragmentDate() {
        return fragmentDate;
    }

    public boolean isTextEmpty() {
        return (textFile.isEmpty() || textFile.equals(""));

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
/*
    public void uploadImageFile(final String fileName, Bitmap bitmapImage) {
        final ImageText it = this;
        Backendless.Files.Android.upload(bitmapImage, Bitmap.CompressFormat.JPEG, 100,
                fileName + ".jpg", Defaults.FILES_IMAGETEXT_DIRECTORY,
                new AsyncCallback<BackendlessFile>() {

                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    it.image = backendlessFile;
                    Logger.v(" Image File Url " + backendlessFile.getFileURL());

                    Backendless.Persistence.of(ImageText.class).save(it, new AsyncCallback<ImageText>() {
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

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Logger.v(" Image Upload Failed fault " +  backendlessFault.toString());
                    Logger.v(" Image Upload Failed " + backendlessFault.getDetail());
                    Logger.v(" Image Upload Failed message " + backendlessFault.getMessage());
                }

        });
    }

    public void uploadTextFile(File textFile) {
        final ImageText it = this;
        Backendless.Files.upload(textFile, Defaults.FILES_IMAGETEXT_DIRECTORY, true, new AsyncCallback<BackendlessFile> () {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    text = backendlessFile;
                    Logger.v(" Text File Url " + backendlessFile.getFileURL());

                    Backendless.Persistence.of(ImageText.class).save(it, new AsyncCallback<ImageText>() {
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

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Logger.v(" Text Upload Failed fault " +  backendlessFault.toString());
                    Logger.v(" Text Upload Failed " + backendlessFault.getDetail());
                    Logger.v(" Text Upload Failed message " + backendlessFault.getMessage());
                }

        });

    }*/

    public static void saveImageText(ImageText it) {
        Backendless.Persistence.save(it, new AsyncCallback<ImageText>() {
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

}

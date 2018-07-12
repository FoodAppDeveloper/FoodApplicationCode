package com.rtis.foodapp.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.ui.RegisterActivity;
import com.rtis.foodapp.ui.fragments.EachMealFragment;
import com.rtis.foodapp.utils.Logger;

import java.net.URI;

import weborb.service.MapToProperty;

/**
 * Class that stores the image file and text file associated with it.
 * Implements Parcelable to save in EachMealFragment.
 */
public class ImageText implements Parcelable {

    @MapToProperty(property = "image")
    private String imageFile;

    @MapToProperty(property = "text")
    private String textFile;

    private Uri imageUri;

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

    public boolean isTextEmpty() {
        if (textFile.isEmpty() || textFile.equals("")) {
            return true;
        }
        return false;
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

    public void uploadImageFile(final String fileName, Bitmap bitmapImage) {


        Backendless.Files.Android.upload(bitmapImage, Bitmap.CompressFormat.JPEG, 100,
                fileName + ".jpg", Defaults.FILES_IMAGETEXT_DIRECTORY,
                new AsyncCallback<BackendlessFile>() {

                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    Logger.v(" Profile Pic Url " + backendlessFile.getFileURL());
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Logger.v(" Image Upload Failed fault " +  backendlessFault.toString());
                    Logger.v(" Image Upload Failed " + backendlessFault.getDetail());
                    Logger.v(" Image Upload Failed message " + backendlessFault.getMessage());
                }

        });
    }
}

package com.rtis.foodapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that stores the image file and text file associated with it.
 * Implements Parcelable to save in EachMealFragment.
 */
public class ImageText implements Parcelable {

    private String imageFile;
    private String textFile;

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
}

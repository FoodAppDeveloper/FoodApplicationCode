package com.rtis.foodapp.model;

import android.media.Image;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rtis.foodapp.utils.Logger;

import java.util.Date;

/**
 * Class that stores the image file and text file associated with it.
 * Implements Parcelable to save in EachMealFragment.
 */
public class ImageText {

    // Stores URL to image and text files
    private String imageFile;
    private String textFile;

    // Identifier variables
    private String meal;            // See Utils for format
    private String fragmentDate;    // ddMMyy
    private Date created;
    // Allows queuing multiple data objects with the same day and meal
    private String objectId;

    /**
     * Empty constructor.
     */
    public ImageText() {
        imageFile = "";
        textFile = "";

    }

    /**
     * Constructor to initialize variables.
     *
     * @param meal the meal the object belongs to
     * @param date the date identifier for the object
     */
    public ImageText(String meal, String date) {
        imageFile = "";
        textFile = "";
        this.meal = meal;
        this.fragmentDate = date;

    }

    /**
     * Checks if textFile stores data.
     *
     * @return true if empty, false otherwise
     */
    public boolean isTextEmpty() {
        return (textFile == null || textFile.isEmpty() || textFile.equals(""));
    }

    /** Getters and Setters */

    public void setCreated(Date created) {
        this.created = created;
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

    /**
     * Static method to save object to cloud.
     *
     * @param it the ImageText object to save
     */
    public static void saveImageText(ImageText it) {
        Backendless.Data.of(ImageText.class).save(it, new AsyncCallback<ImageText>() {
            @Override
            public void handleResponse(ImageText response) { Logger.v("ImageText.java", " ImageText object saved ");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.v("ImageText.java", " ImageText Save Failed: " + fault.getMessage());
            }
        });
    }

    /**
     * Static method to update existing ImageText object.
     *
     * @param it the ImageText object to update.
     */
    public static void updateImageText(ImageText it){
        Backendless.Persistence.of(ImageText.class).save(it, new AsyncCallback<ImageText>()
        {
            @Override
            public void handleResponse( ImageText person ) {
                Logger.v("ImageText.java"," ImageText object updated ");
            }

            @Override
            public void handleFault( BackendlessFault fault ) {
                Logger.v("ImageText.java"," ImageText Update Failed: " + fault.getMessage());
            }
        } );

    }

}

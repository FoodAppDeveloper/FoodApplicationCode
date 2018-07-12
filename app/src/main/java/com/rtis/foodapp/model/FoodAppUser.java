package com.rtis.foodapp.model;

import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodAppUser extends BackendlessUser {
    private final String KEY_NAME = "name";
    private final String KEY_PHONO = "phno";
    private final String KEY_IS_PROFILE_PIC_AVAILABLE = "isProfilePicAvailable";
    private final String KEY_IMAGETEXT = "FoodData";
    private List<ImageText> imageTextList;

   public FoodAppUser()
    {
        // default constructor
        imageTextList = new ArrayList<>();
    }

    public FoodAppUser(BackendlessUser user)
    {
        // user constructor

        setEmail(user.getEmail());
        setPassword(user.getPassword());
        setName((String) user.getProperty(KEY_NAME));
        setPhoneNumber((String) user.getProperty(KEY_PHONO));
        setIsProfilePicAvailable((Boolean) user.getProperty(KEY_IS_PROFILE_PIC_AVAILABLE));

        setImageTextList(getImageTextList(user));
    }

    public List<ImageText> getImageTextList(BackendlessUser user) {
        List<ImageText> list = new ArrayList<>();
        Object imageTextObject = user.getProperty(KEY_IMAGETEXT);
        if ((imageTextObject != null) && (imageTextObject instanceof ImageText[]))
        {
            ImageText[] imageTexts = (ImageText[]) imageTextObject;
            list.addAll(Arrays.asList(imageTexts));
        }
        return list;
    }

    public List<ImageText> getImageTextList() {
       return imageTextList;
    }

    public void setImageTextList(List<ImageText> imageTextList) {
        this.imageTextList = imageTextList;
    }

    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public String getName() {
        return (String) super.getProperty(KEY_NAME);
    }

    public void setName(String name) {
        super.setProperty(KEY_NAME, name);
    }

    public String getPhoneNumber() {
        return (String) super.getProperty(KEY_PHONO);
    }

    public void setPhoneNumber(String phno) {
        super.setProperty(KEY_PHONO, phno);
    }

    public boolean getIsProfilePicAvailable() {
        return (Boolean) super.getProperty(KEY_IS_PROFILE_PIC_AVAILABLE);
    }

    public void setIsProfilePicAvailable(Boolean isAvailable) {
        super.setProperty(KEY_IS_PROFILE_PIC_AVAILABLE, isAvailable);
    }
}
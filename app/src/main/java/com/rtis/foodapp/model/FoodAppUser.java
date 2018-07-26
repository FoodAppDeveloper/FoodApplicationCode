package com.rtis.foodapp.model;

import com.backendless.BackendlessUser;

/**
 * BackendlessUser class to store data about each user in database.
 */
public class FoodAppUser extends BackendlessUser {
    private final String KEY_NAME = "name";
    private final String KEY_PHONE = "phone";
    private final String KEY_IS_PROFILE_PIC_AVAILABLE = "isProfilePicAvailable";

    /**
     * Empty Constructor
     */
    public FoodAppUser()
    {

    }

    /**
     * Initiate from user.
     *
     * @param user BackendlessUser to draw data from
     */
    public FoodAppUser(BackendlessUser user)
    {
        setEmail(user.getEmail());
        setPassword(user.getPassword());
        setName((String) user.getProperty(KEY_NAME));
        setPhoneNumber((String) user.getProperty(KEY_PHONE));
        setIsProfilePicAvailable((Boolean) user.getProperty(KEY_IS_PROFILE_PIC_AVAILABLE));

    }

    /* Getters and Setters */

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
        return (String) super.getProperty(KEY_PHONE);
    }

    public void setPhoneNumber(String phno) {
        super.setProperty(KEY_PHONE, phno);
    }

    public boolean getIsProfilePicAvailable() {
        return (Boolean) super.getProperty(KEY_IS_PROFILE_PIC_AVAILABLE);
    }

    public void setIsProfilePicAvailable(Boolean isAvailable) {
        super.setProperty(KEY_IS_PROFILE_PIC_AVAILABLE, isAvailable);
    }
}
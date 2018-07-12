package com.rtis.foodapp.backendless;

public class Defaults {
    public static final String APPLICATION_ID = "212C4FAA-5880-A76A-FF3C-9ACD99367F00";
    public static final String API_KEY = "3F14AA9E-379E-F856-FF42-A45837DEFC00";
    public static final String VERSION = "v1";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static final String SERVER_CLIENT_ID = "991633786815-3jml8nrhvecfr99t64ok569a911q67r5.apps.googleusercontent.com";
    public static final int RC_SIGN_IN = 1007;
    public static final int REQUEST_AUTHORIZATION = 1008;
    // login error codes
    public static final String LOGIN_DISABLED = "3000";
    public static final String LOGIN_DISABLED_MULTIPLE="3002";
    public static final String LOGIN_INVALID_CREDENTIALS="3003";
    public static final String LOGIN_ACCOUNT_LOCKED="3036";
    public static final String LOGIN_ACCOUNT_MULTIPLE_LOGIN_LIMIT_REACHED="3044";

    public static final String REGISTRATION_FAILED="3014";
    public static final String REGISTRATION_USER_ALREADY_EXISTS="3033";
    public static final String REGISTRATION_EMAIL_WRONG="3040";
    public static final String REGISTRATION_ERROR="3021";

    public static final int CAMERA_RESULT_CODE = 1001;
    public static final int GALLERY_RESULT_CODE = 1002;

    public static final String FILES_PROFILE_PIC_DIRECTORY = "profilePics";
    public static final String FILES_PROFILE_PIC_URL = SERVER_URL + "/" + APPLICATION_ID + "/" + VERSION + "/files/" + FILES_PROFILE_PIC_DIRECTORY;

    public static final String FILES_IMAGETEXT_DIRECTORY = "imageText";
}
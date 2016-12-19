package com.rtis.foodapp.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.rtis.foodapp.R;
import com.rtis.foodapp.backendless.DefaultCallback;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.model.FoodAppUser;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPhoneNo;
    private EditText mName;

    private ImageView uploadImageView;
    private Uri imageUri;
    private Bitmap uploadImageBitmap;
    private boolean isProfilePicAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    public void init() {
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPhoneNo = (EditText) findViewById(R.id.phoneno);
        mName = (EditText) findViewById(R.id.name);

        // upload profile pic
        Button uploadProfileBtn = (Button) findViewById(R.id.btn_profile_pic_upload);
        uploadImageView = (ImageView) findViewById(R.id.uploadedImage);
        imageUri = null;
        isProfilePicAvailable = false;
        uploadProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePicDialog();
            }
        });
        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Defaults.CAMERA_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                displayImage();
            }
        } else if (requestCode == Defaults.GALLERY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                Logger.v("uri= "+imageUri.toString());
                displayImage();
            }
        }
    }


    public void uploadProfilePicDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] ch = new String[]{"Use Camera", "Pick Image from Gallery"};
        builder.setItems(ch, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    launchCamera();
                } else if (which == 1) {
                    launchGallery();
                }
            }
        });
        builder.create().show();
    }

    private void launchGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), Defaults.GALLERY_RESULT_CODE);
    }


    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForCamera());
        startActivityForResult(intent, Defaults.CAMERA_RESULT_CODE);
    }


    // getting Uri for Camera
    private Uri getUriForCamera() {
        File directory = Util.getStorageDirectory(this);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.ENGLISH);
        String imagePath = "IMG_" + simpleDateFormat.format(calendar.getTime()) + ".jpg";
        Uri uploadFileUri = Uri.fromFile(new File(directory + File.separator + imagePath));
        imageUri = uploadFileUri;
        return uploadFileUri;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mName.setError(null);
        mPhoneNo.setError(null);

        // Store values at the time of the login attempt.
        String emailText = mEmailView.getText().toString().trim();
        String nameText = mName.getText().toString().trim();
        String phoneText = mPhoneNo.getText().toString().trim();
        String passwordText = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordText) && !Util.isPasswordValid(passwordText)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailText)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Util.isEmailValid(emailText)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // check for a valid phone number
        if (TextUtils.isEmpty(phoneText)) {
            mPhoneNo.setError(getString(R.string.error_field_required));
            focusView = mPhoneNo;
            cancel = true;
        } else if (!Util.isPhoneNoValid(phoneText)) {
            mPhoneNo.setError(getString(R.string.error_invalid_phoneno));
            focusView = mPhoneNo;
            cancel = true;
        }


        // check for a valid name
        if (TextUtils.isEmpty(nameText)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        } else if (!Util.isNameValid(nameText)) {
            mName.setError(getString(R.string.error_invalid_phoneno));
            focusView = mName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the register user attempt.
            if (Util.isNetworkAvailable(this)) {
                FoodAppUser user = new FoodAppUser();
                user.setEmail(emailText);
                user.setName(nameText);
                user.setPassword(passwordText);
                user.setPhoneNumber(phoneText);
                user.setIsProfilePicAvailable(isProfilePicAvailable);

                Backendless.UserService.register(user, new DefaultCallback<BackendlessUser>(RegisterActivity.this,"Registering the User. Please Wait..") {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        super.handleResponse(response);
                        String userObjId = response.getObjectId();
                        Log.v("registered", userObjId);

                        // upload image if it is available
                        if (isProfilePicAvailable) {
                            uploadProfilePic(userObjId);
                        } else
                            registrationCompleted();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        super.handleFault(fault);
                        String message=null;
                        switch (fault.getCode()) {
                            case Defaults.REGISTRATION_ERROR:message= getResources().getString(R.string.MSG_REGISTER_FAILED);
                                break;
                            case Defaults.REGISTRATION_FAILED:message= getResources().getString(R.string.MSG_REGISTER_FAILED);
                                break;
                            case Defaults.REGISTRATION_EMAIL_WRONG:message= getResources().getString(R.string.MSG_REGISTER_EMAIL_WRONG);
                                break;
                            case Defaults.REGISTRATION_USER_ALREADY_EXISTS:message= getResources().getString(R.string.MSG_REGISTER_USER_ALREADY_EXISTS);
                                break;
                        }
                        Util.showToast(RegisterActivity.this, message);
                    }
                });
            }
            else
                Util.showToast(this, getResources().getString(R.string.network_error));

        }
    }





    private void registrationCompleted()
    {
        Intent loginIntent=new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.putExtra("mUserEmail",mEmailView.getText().toString());
        loginIntent.putExtra("mUserPassword",mPasswordView.getText().toString());
        startActivity(loginIntent);
        finish();
    }

    void displayImage() {
        ContentResolver contentResolver = this.getContentResolver();
        try {
            uploadImageBitmap = Util.scaleDown(MediaStore.Images.Media.getBitmap(contentResolver, imageUri), 300, true);
            if (uploadImageBitmap != null) {
                uploadImageView.setImageBitmap(uploadImageBitmap);
                isProfilePicAvailable = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadProfilePic(final String imageName)
    {
        // testing file upload
        Backendless.Files.Android.upload(uploadImageBitmap, Bitmap.CompressFormat.PNG, 100,imageName+".png", Defaults.FILES_PROFILE_PIC_DIRECTORY, new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                Logger.v(" Profile Pic Url " + backendlessFile.getFileURL());
                registrationCompleted();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

                Toast.makeText(RegisterActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
                Logger.v(" Profile Pic Upload Failed fault " +  backendlessFault.toString());
                Logger.v(" Profile Pic Upload Failed " + backendlessFault.getDetail());
                Logger.v(" Profile Pic Upload Failed message " + backendlessFault.getMessage());
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}

package com.rtis.foodapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rtis.foodapp.R;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;

import static android.Manifest.permission.READ_CONTACTS;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private boolean requestPermissionBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);
        // Logger.v(" Splash On Create ");
        init();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissionBool = true;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS}, 111);
            } else
                requestPermissionBool = false;
        } else
            requestPermissionBool = false;
    }


    private void init() {
        //  Logger.v("Splash init start");
        TextView appname = (TextView) findViewById(R.id.text_appname);
        TextView subtitle = (TextView) findViewById(R.id.text_caption);

        Typeface tf = Util.getCustomTypeFace(this, 0);
        if (tf != null) {
            appname.setTypeface(tf);
            subtitle.setTypeface(tf);
        }
        Logger.v("Splash init end");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestPermissionBool)
            performUserLoginCheck();
    }


    private void performUserLoginCheck() {
        progressBar.setVisibility(View.VISIBLE);
        if (Util.isNetworkAvailable(this))
            checkIfUserIsAlreadyLoggedIn();
        else {
            progressBar.setVisibility(View.GONE);
            showNetworkUnavailableDialog();
        }
    }

    private void userShouldLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    private void loginSuccessful(BackendlessUser backendlessUser) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void checkIfUserIsAlreadyLoggedIn() {
        Backendless.UserService.isValidLogin(new BackendlessCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean isValidLogin) {
                if (isValidLogin && Backendless.UserService.CurrentUser() == null) {
                    String currentUserId = Backendless.UserService.loggedInUser();
                    if (!currentUserId.equals("")) {
                        Backendless.UserService.findById(currentUserId, new BackendlessCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser currentUser) {
                                Backendless.UserService.setCurrentUser(currentUser);
                                // login successful
                                loginSuccessful(currentUser);
                            }
                        });
                    }
                } else {
                    Logger.v("User Should Login");
                    userShouldLogin();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v("Splash Check Login Error " + backendlessFault.toString());
                if (backendlessFault.getMessage().contains("Unable to resolve host \"api.backendless.com\"")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SplashActivity.this, " Check your Network And try again ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showNetworkUnavailableDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getResources().getString(R.string.network_dialog_title));
        dialogBuilder.setMessage(getResources().getString(R.string.network_error));
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        dialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
                finish();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 111) {
            requestPermissionBool = false;
            if (grantResults.length > 0) {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    performUserLoginCheck();
                } else if (grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(SplashActivity.this, "Camera Access permission has been successfully invoked by the user, but External Storage Access also needed by the app", Toast.LENGTH_SHORT).show();
                else if (grantResults[2] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(SplashActivity.this, "External Storage Access permission has been successfully invoked by the user, but Camera Access also needed by the app", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Camera And External Storage Access are required by this app to take advantage of all the functionalities.I f you want to revoke access, you have to do it from Settings", Toast.LENGTH_SHORT).show();
        }
    }
}

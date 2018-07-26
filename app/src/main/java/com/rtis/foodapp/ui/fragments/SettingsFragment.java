package com.rtis.foodapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rtis.foodapp.R;
import com.rtis.foodapp.ui.SplashActivity;

/**
 * Fragment for settings page.
 * Instantiated in Main Activity.
 */
public class SettingsFragment extends Fragment {
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        // Create Logout button
        Button logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return view;
    }

    /**
     * Helper method to log user out of account when button is pressed.
     */
    private void logout() {
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            public void handleResponse(Void response) {
                // user has been logged out.
                startActivity(new Intent(getContext(), SplashActivity.class));
            }

            public void handleFault(BackendlessFault fault) {
                // something went wrong and logout failed, to get the error code call fault.getCode()
            }
        });
    }
}

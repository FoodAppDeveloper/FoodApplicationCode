package com.rtis.foodapp.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EachMealSectionAdapter;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.model.MealTimeItems;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EachMealFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath = "null";
    private PopupWindow mPopupWindow;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    Uri mCurrentPhotoUri;

    private File mCurrentFile;

    private List<ImageText> imageTextList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEAL = "meal";
    private static final String ARG_DATE = "date";

    // TODO: Rename and change types of parameters
    private String meal;
    private String date;

    public EachMealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param meal Parameter 2.
     * @return A new instance of fragment EachMealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EachMealFragment newInstance(String meal, String date) {
        EachMealFragment fragment = new EachMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(ARG_DATE);
            meal = getArguments().getString(ARG_MEAL);
        }

        if (savedInstanceState != null) {
            imageTextList = savedInstanceState.getParcelableArrayList("imageTextFiles");
        } else {
            queryImageText();
        }
        /*
        if (imageTextList == null) {
            imageTextList = new ArrayList<>();
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.captured_meal_popup, container, false);
        queryImageText();

        if (!containsImages()) {
            dispatchTakePictureIntent();
        } else {
            showPopUp();
        }

        return customView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void mealClicked() {
        //queryImageText();
        if (!containsImages()) {
            dispatchTakePictureIntent();
        } else {
            showPopUp();
        }
    }

    private void showPopUp() {
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.captured_meal_popup, null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        swipeSelector = (SwipeSelector) customView.findViewById(R.id.eachMealselector);
        SwipeItem[] swipeItems = new SwipeItem[imageTextList.size()];
        for (int i = 0; i < imageTextList.size(); i++) {
            swipeItems[i] = new SwipeItem(i, "Image" + i, "Image" + i);
        }
        swipeSelector.setItems(swipeItems);

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.close_popup);
        ImageButton cameraButton = (ImageButton) customView.findViewById(R.id.take_picture);
        RelativeLayout mLayout = (RelativeLayout) customView.findViewById(R.id.popup_1);

        mViewPager = (ViewPager) customView.findViewById(R.id.eachMealViewPager);

        mViewPager.setAdapter(new EachMealSectionAdapter(getContext(), imageTextList, meal, date));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Log.v("Slider","Page Selected to "+position);
                swipeSelector.selectItemAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });


        // Set a click listener for the take picture button
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryImageText();
                mPopupWindow.dismiss();
                dispatchTakePictureIntent();
            }
        });

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(mLayout, Gravity.CENTER, 0, 0);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("imageTextFiles", (ArrayList) imageTextList);
    }

    /* Deal with Camera */

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
                Util.showToast(getContext(), "Failed to capture image.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.rtis.foodapp.fileprovider",
                        photoFile);

                mCurrentPhotoUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "JPEG_" + date + timeStamp + "_" + meal + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        //File image = new File(storageDir, imageFileName + ".jpg");
        mCurrentFile = image;

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    // Gets called after dispatchTakePictureIntent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageTextList.add(new ImageText(meal, date));
            uploadImageFile(imageTextList.size() - 1); // point to last added element

            if(mViewPager != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * Add current photo path to internal storage. Doesn't show up in gallery.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public void uploadImageFile(final int position) {
        Backendless.Files.upload(mCurrentFile, Defaults.FILES_IMAGETEXT_DIRECTORY, new AsyncCallback<BackendlessFile>() {

            @Override
            public void handleResponse(BackendlessFile backendlessFile) {
                ImageText imageText = imageTextList.get(position);
                imageText.setImageFile(backendlessFile.getFileURL());
                Logger.v(" Image File Url " + backendlessFile.getFileURL());
                imageTextList.set(position, imageText);
                ImageText.saveImageText(imageText);
                mCurrentFile.delete();
                showPopUp();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v(" Image Upload Failed fault " +  backendlessFault.toString());
                Logger.v(" Image Upload Failed " + backendlessFault.getDetail());
                Logger.v(" Image Upload Failed message " + backendlessFault.getMessage());
            }

        });
    }

    public void queryImageText() {
        String whereClause = "ownerId = '" + Backendless.UserService.CurrentUser().getUserId() +
                "' and fragmentDate = '" + date + "' and meal = '" + meal + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(ImageText.class).find(queryBuilder,
                new AsyncCallback<List<ImageText>>() {
                    @Override
                    public void handleResponse(final List<ImageText> itList) {
                        if(itList.isEmpty()) {
                            Logger.v(" No Image Files Queried.");
                        } else if (itList.size() == 1) {
                            imageTextList = itList;
                            Logger.v(" Queried one image file.");
                        } else {
                            imageTextList = itList;
                            Logger.v(" Number of image files queried: " + itList.size());
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Logger.v(" Failed to retrieve image file URL", backendlessFault.getMessage());
                    }
                });
    }

    public boolean containsImages() {
        return imageTextList != null && !imageTextList.isEmpty();
    }

    public void setImageTextList(List<ImageText> imageTextList) {
        this.imageTextList = imageTextList;
    }

}

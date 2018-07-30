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
import android.widget.Button;
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
import com.rtis.foodapp.backendless.Defaults;
import com.rtis.foodapp.model.ImageText;
import com.rtis.foodapp.model.MealTimeItems;
import com.rtis.foodapp.utils.Logger;
import com.rtis.foodapp.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Fragment class to contain information for each meal and allows user to capture image
 * using built-in camera.
 * Initialized upon EachDayFragment onCreateView.
 */
public class EachMealFragment extends Fragment {
    // Constant for image capturing
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Constant for imageText directory name
    static final String IMAGETEXT_DIR = "imageText";

    // Fragment identifiers
    private static final String ARG_MEAL = "meal";
    private static final String ARG_DATE = "date";
    private String meal;
    private String date;

    // Fragment view variables
    private PopupWindow mPopupWindow;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    private EachMealSectionAdapter mAdapter;

    // Image capture variables
    private String mCurrentPhotoPath;
    private Uri mCurrentPhotoUri;
    private File mCurrentFile;

    // Variables called upon to update view
    private EachDayFragment parentFragment;
    private MealTimeItems mealItem;

    // List of imageText objects stored in the fragment
    private List<ImageText> imageTextList;

    // Keeps track of ImageText
    private int currentPos;

    /**
     * Empty Constructor
     */
    public EachMealFragment() {
    }

    /**
     * Creates a new instance of EachMealFragment using parameters
     *
     * @param meal the String meal the fragment belongs to
     * @param date the String date identifier for the fragment
     * @return A new instance of fragment EachMealFragment
     */
    public static EachMealFragment newInstance(String meal, String date) {
        EachMealFragment fragment = new EachMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method called along with newInstance to populate global variables.
     *
     * @param meal the String meal the fragment belongs to
     * @param date the String date identifier for the fragment
     * @param fragment the parent EachDayFragment this fragment belongs to
     * @param item the MealTimeItems associated with this meal fragment
     */
    public void setUp(String meal, String date, EachDayFragment fragment, MealTimeItems item) {
        this.meal = meal;
        this.date = date;
        this.parentFragment = fragment;
        this.mealItem = item;
        queryImageText();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets fragment arguments and imageTextList.
     *
     * @param savedInstanceState Bundle containing previous state of fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(ARG_DATE);
            meal = getArguments().getString(ARG_MEAL);
        }

        queryImageText();

        currentPos = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.captured_meal_popup, container,
                false);
        queryImageText();

        // Shows camera if no images associated with this meal, otherwise show images
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
        // Shows camera if no images associated with this meal, otherwise show images
        if (!containsImages()) {
            dispatchTakePictureIntent();
        } else {
            showPopUp();
        }
    }

    /**
     * Show popup window to display images in that meal.
     */
    private void showPopUp() {
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        final View customView = inflater.inflate(R.layout.captured_meal_popup, null);

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

        // Initiate up SwipeSelector and its items
        swipeSelector = (SwipeSelector) customView.findViewById(R.id.eachMealselector);
        SwipeItem[] swipeItems = new SwipeItem[imageTextList.size()];
        for (int i = 0; i < imageTextList.size(); i++) {
            swipeItems[i] = new SwipeItem(i, "Image" + i, "Image" + i);
        }
        swipeSelector.setItems(swipeItems);

        // Get a reference for the custom view buttons
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.close_popup);
        ImageButton cameraButton = (ImageButton) customView.findViewById(R.id.take_picture);
        ImageButton deleteButton = (ImageButton) customView.findViewById(R.id.delete_button);

        // Initiate layout, view, and adapter
        RelativeLayout mLayout = (RelativeLayout) customView.findViewById(R.id.popup_1);
        mViewPager = (ViewPager) customView.findViewById(R.id.eachMealViewPager);
        mAdapter = new EachMealSectionAdapter(getContext(), imageTextList);
        mViewPager.setAdapter(mAdapter);

        // Change pager position
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Log.v("Slider","Page Selected to "+position);
                currentPos = position;
                swipeSelector.selectItemAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        // Set a click listener for the take picture button
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                dispatchTakePictureIntent();
            }
        });

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        // Set a click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImageText(currentPos);
            }
        });

        mPopupWindow.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
    }

    /* Deal with Camera */

    /**
     * Brings up phone's camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException e) {
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

    /**
     * Helper method to create image file in storage.
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        // Format: JPEG_<ddMMyyHHmmss>_<meal>_<unique#>.jpg
        String timeStamp = new SimpleDateFormat(Util.TIME_FORMAT).format(new Date());
        String imageFileName = Util.IMAGE_PREFIX + date + timeStamp + "_" + meal + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                Util.IMAGE_SUFFIX,      /* suffix */
                storageDir              /* directory */
        );

        //File image = new File(storageDir, imageFileName + ".jpg");
        mCurrentFile = image;

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    /**
     * Method to handle camera activity. Uploads image to cloud once picture is
     * taken and accepted.
     * Called after dispatchTakePictureIntent
     *
     * @param requestCode code for request type
     * @param resultCode code for result type
     * @param data data stored in take picture intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Initiate imageTextList and add new object
            if (imageTextList == null) {
                imageTextList = new ArrayList<>();
            }
            imageTextList.add(new ImageText(meal, date));

            // point to last added element for update
            uploadImageFile(imageTextList.size() - 1);

            // update adapter view
            if(mViewPager != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }

            showPopUp();
        }
    }

    /**
     * Method to upload taken image and associated imageText object to cloud.
     *
     * @param position the position of the imageText in list
     */
    private void uploadImageFile(final int position) {
        String directory = Defaults.FILES_IMAGETEXT_DIRECTORY + "/"
                + Backendless.UserService.CurrentUser().getEmail();
        Backendless.Files.upload(mCurrentFile, directory, new AsyncCallback<BackendlessFile>() {

            @Override
            public void handleResponse(BackendlessFile backendlessFile) {
                ImageText imageText = imageTextList.get(position);
                imageText.setImageFile(backendlessFile.getFileURL());
                Logger.v(" Image File Url " + backendlessFile.getFileURL());
                imageTextList.set(position, imageText);
                ImageText.saveImageText(imageText);
                mCurrentFile.delete();

                if (mAdapter != null) {
                    mAdapter.refresh(imageTextList, position);
                }

                mealItem.setFill(true);
                updateParent();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v(" Image Upload Failed fault " +  backendlessFault.toString());
                Logger.v(" Image Upload Failed " + backendlessFault.getDetail());
                Logger.v(" Image Upload Failed message " + backendlessFault.getMessage());
            }

        });
    }

    /**
     * Method to delete the ImageText object and associated files in the database.
     * Updates fragment view.
     *
     * @param position the position of the ImageText to delete
     */
    private void deleteImageText(final int position) {
        // Get file name according to format imageText/<user_email>/<filename>
        ImageText it = imageTextList.get(position);
        int splitIndex = it.getImageFile().indexOf(IMAGETEXT_DIR);

        String fileName;
        try {
            // Decode URL format
            fileName = java.net.URLDecoder.decode(it.getImageFile().substring(splitIndex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Manually filter out at least the "@"
            fileName = it.getTextFile().substring(splitIndex).
                    replace("%40", "@");
        } catch (StringIndexOutOfBoundsException e) {
            // Known occurrence: User deletes image before it uploads
            return;
        }

        /* Delete image file */
        Backendless.Files.remove(fileName, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Logger.v("Deleted Image File");

                /* Delete text File if exists */
                if (!imageTextList.get(position).isTextEmpty()) {
                    ImageText it = imageTextList.get(position);
                    int splitIndex = it.getImageFile().indexOf(IMAGETEXT_DIR);

                    String fileName;
                    try {
                        fileName = java.net.URLDecoder.decode(it.getTextFile().substring(splitIndex), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        fileName = it.getTextFile().substring(splitIndex).
                                replace("%40", "@");
                    } catch (StringIndexOutOfBoundsException e) {
                        return;
                    }

                    Backendless.Files.remove(fileName, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                            Logger.v("Deleted Text File");

                            // Finally, delete object
                            deleteObject(position);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Logger.v(" Delete Text File Failed fault " +  fault.toString());
                            Logger.v(" Delete Text File Failed " + fault.getDetail());
                            Logger.v(" Delete Text File Failed message " + fault.getMessage());
                        }
                    });
                } else {
                    // Finally, delete object
                    deleteObject(position);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.v(" Delete Image File Failed fault " +  fault.toString());
                Logger.v(" Delete Image File Failed " + fault.getDetail());
                Logger.v(" Delete Image File Failed message " + fault.getMessage());
            }
        });

    }

    /**
     * Helper function to delete ImageText Object.
     *
     * @param position the position of the object in ImageText list
     */
    private void deleteObject(final int position) {
        Backendless.Persistence.of(ImageText.class).remove(imageTextList.get(position),
                new AsyncCallback<Long>() {
                    @Override
                    public void handleResponse(Long response) {
                        Logger.v("Deleted ImageText Object: " + response.toString());

                        // Remove from local list and update view
                        imageTextList.remove(position);
                        mAdapter.setImageTextList(imageTextList);

                        // Clear popup if no more images, otherwise show again
                        mPopupWindow.dismiss();
                        if (containsImages()) {
                            showPopUp();
                        } else {
                            // Update fragment view
                            mealItem.setFill(false);
                            updateParent();
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Logger.v(" Delete ImageText Failed fault " +  fault.toString());
                        Logger.v(" Delete ImageText Failed " + fault.getDetail());
                        Logger.v(" Delete ImageText Failed message " + fault.getMessage());
                    }
                });
    }

    /**
     * Queries ImageText objects from database and updates view.
     */
    public void queryImageText() {
        /* Create a where clause identifying all database objects associated with current user,
        fragment date, and meal */
        String whereClause = "ownerId = '" + Backendless.UserService.CurrentUser().getUserId() +
                "' and fragmentDate = '" + date + "' and meal = '" + meal + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(ImageText.class).find(queryBuilder,
                new AsyncCallback<List<ImageText>>() {
                    @Override
                    public void handleResponse(final List<ImageText> itList) {
                        imageTextList = itList;
                        if(!containsImages()) {
                            // set item view fill to false
                            if (mealItem != null) {
                                mealItem.setFill(false);
                            }
                            Logger.v(" No Image Files Queried.");
                        } else {
                            // set item view fill to true
                            if (mealItem != null) {
                                mealItem.setFill(true);
                            }
                            Logger.v(" Number of image files queried: " + itList.size());
                        }

                        // Update view
                        if (mAdapter != null) {
                            mAdapter.setImageTextList(imageTextList);
                        }
                        updateParent();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Logger.v(" Failed to retrieve image file URL",
                                backendlessFault.getMessage());
                    }
                });
    }

    /**
     * Helper method to determine whether this fragment contains images.
     *
     * @return
     */
    private boolean containsImages() {
        return imageTextList != null && !imageTextList.isEmpty();
    }

    /**
     * Helper method to update parent fragment view.
     */
    private void updateParent() {
        if (parentFragment != null) {
            parentFragment.update();
        }
    }

}

package com.rtis.foodapp.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EachMealSectionAdapter;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;

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

    private String mCurrentPhotoPath;
    private PopupWindow mPopupWindow;
    private List<File> imageFiles;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;

    private EachMealSectionAdapter mAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DAY = "day";
    private static final String ARG_MEAL = "meal";

    // TODO: Rename and change types of parameters
    private String day;
    private String meal;


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
    public static EachMealFragment newInstance(String meal) {
        EachMealFragment fragment = new EachMealFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_DAY, day);
        args.putString(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //day = getArguments().getString(ARG_DAY);
            meal = getArguments().getString(ARG_MEAL);
        }
        imageFiles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.captured_meal_popup, container, false);

        if (imageFiles.isEmpty()) {
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
        if (imageFiles.isEmpty()) {
            dispatchTakePictureIntent();
        } else {
            showPopUp();
        }
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
                imageFiles.add(photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.rtis.foodapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    };

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + meal + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        galleryAddPic();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            showPopUp();

            //  extras.getInt("Position");

            /* crashes with below code */
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
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

    // otherwise have this called on onCreateView
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
        SwipeItem[] swipeItems = new SwipeItem[imageFiles.size()];
        for (int i = 0; i < imageFiles.size(); i++) {
            swipeItems[i] = new SwipeItem(i, "Image" + i, "Image" + i);
        }
        swipeSelector.setItems(swipeItems);

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.close_popup);
        ImageButton cameraButton = (ImageButton) customView.findViewById(R.id.take_picture);
        RelativeLayout mLayout = (RelativeLayout) customView.findViewById(R.id.popup_1);

//        RecyclerView myList = (RecyclerView) customView.findViewById(R.id.eachMealList);
//        LinearLayoutManager horizontalLayoutManagaer
//                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        myList.setLayoutManager(horizontalLayoutManagaer);
//        myList.setAdapter(new EachMealRecylerAdapter(getContext()));

        //ImageView popUpImageView=(ImageView) customView.findViewById(R.id.pop_imageView);
        mViewPager = (ViewPager) customView.findViewById(R.id.eachMealViewPager);

        mViewPager.setAdapter(new EachMealSectionAdapter(getContext(), imageFiles));

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

}

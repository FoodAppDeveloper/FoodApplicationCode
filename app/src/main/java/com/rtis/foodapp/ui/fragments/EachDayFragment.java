package com.rtis.foodapp.ui.fragments;

/**
 * Created by rajul on 11/18/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EachMealSectionAdapter;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;
import com.rtis.foodapp.callbacks.ItemClickSupport;
import com.rtis.foodapp.model.MealTimeItems;
import com.rtis.foodapp.utils.ItemOffsetDecoration;
import com.rtis.foodapp.utils.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * fragment which represents a particular day
 */
public class EachDayFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NAME = "section_name";
    String mCurrentPhotoPath;
    private List<MealTimeItems> mItems;
    private String mCurrentPageName = null;
    private EveryDayMealTimingsListAdapter madapter;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private File photoFile;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;

    public EachDayFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EachDayFragment newInstance(String sectionName) {
        EachDayFragment fragment = new EachDayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPageName = getArguments().getString(ARG_SECTION_NAME);
        Log.v("Fragment", mCurrentPageName + " Created");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_everyday_list, container, false);
        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_relativeLayout);
        RecyclerView myList = (RecyclerView) rootView.findViewById(R.id.myList);
        mItems = new ArrayList<>();
        mItems.add(new MealTimeItems(Util.BREAKFAST_STRING));
        mItems.add(new MealTimeItems(Util.MORNING_SNACK_STRING));
        mItems.add(new MealTimeItems(Util.LUNCH_STRING));
        mItems.add(new MealTimeItems(Util.AFTERNOON_SNACK_STRING));
        mItems.add(new MealTimeItems(Util.DINNER_STRING));
        mItems.add(new MealTimeItems(Util.EVENING_SNACK_STRING));

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        myList.addItemDecoration(itemDecoration);
        myList.setLayoutManager(manager);
        madapter = new EveryDayMealTimingsListAdapter(getContext(), mItems);
        myList.setAdapter(madapter);

        ItemClickSupport.addTo(myList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.v("EachList", mCurrentPageName + " " + position);
                dispatchTakePictureIntent(position);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dispatchTakePictureIntent(int clickedItemPosition) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
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
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            showPopUp();
            //  extras.getInt("Position");
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
        swipeSelector.setItems(new SwipeItem(0, "Some", "SomeH"), new SwipeItem(1, "Some2", "SomeH"), new SwipeItem(2, "Some3", "SomeH"));

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.close_popup);
        RelativeLayout mLayout = (RelativeLayout) customView.findViewById(R.id.popup_1);
//        RecyclerView myList = (RecyclerView) customView.findViewById(R.id.eachMealList);
//        LinearLayoutManager horizontalLayoutManagaer
//                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        myList.setLayoutManager(horizontalLayoutManagaer);
//        myList.setAdapter(new EachMealRecylerAdapter(getContext()));

        //  ImageView popUpImageView=(ImageView) customView.findViewById(R.id.pop_imageView);
        mViewPager = (ViewPager) customView.findViewById(R.id.eachMealViewPager);

        mViewPager.setAdapter(new EachMealSectionAdapter(getContext(), getChildFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.v("Slider","Page Selected to "+position);
                swipeSelector.selectItemAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (photoFile != null) {
            // Picasso.with(getActivity()).load(photoFile).into(popUpImageView);
            photoFile.delete();
        }
        //  setPic(popUpImageView);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);

    }
}
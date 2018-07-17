package com.rtis.foodapp.ui.fragments;

/**
 * Created by rajul on 11/18/2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.lang.IllegalStateException;

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
    private static final String ARG_SECTION_DATE = "section_date";
    String mCurrentPhotoPath;
    private List<MealTimeItems> mItems;
    private String mCurrentPageName = null;
    private EveryDayMealTimingsListAdapter madapter;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private File photoFile;
    private ViewPager mViewPager;
    private SwipeSelector swipeSelector;
    private ImageView mImageView;
    private String dateString;

    private List<EachMealFragment> mealFragments;

    public EachDayFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EachDayFragment newInstance(String sectionName, String dateString) {
        EachDayFragment fragment = new EachDayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        args.putString(ARG_SECTION_DATE, dateString);
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
        dateString = getArguments().getString(ARG_SECTION_DATE);
        Log.v("Fragment", mCurrentPageName + " Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_everyday_list, container, false);
        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_relativeLayout);
        RecyclerView myList = (RecyclerView) rootView.findViewById(R.id.myList);
        mItems = new ArrayList<>();
        mItems.add(new MealTimeItems(Util.BREAKFAST_STRING));           // 0
        mItems.add(new MealTimeItems(Util.MORNING_SNACK_STRING));       // 1
        mItems.add(new MealTimeItems(Util.LUNCH_STRING));               // 2
        mItems.add(new MealTimeItems(Util.AFTERNOON_SNACK_STRING));     // 3
        mItems.add(new MealTimeItems(Util.DINNER_STRING));              // 4
        mItems.add(new MealTimeItems(Util.EVENING_SNACK_STRING));       // 5

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        myList.addItemDecoration(itemDecoration);
        myList.setLayoutManager(manager);
        madapter = new EveryDayMealTimingsListAdapter(getContext(), mItems);
        myList.setAdapter(madapter);

        mealFragments = new ArrayList<>();
        mealFragments.add(EachMealFragment.newInstance(Util.BREAKFAST_FILE, dateString));
        mealFragments.add(EachMealFragment.newInstance(Util.MORNING_SNACK_FILE, dateString));
        mealFragments.add(EachMealFragment.newInstance(Util.LUNCH_FILE, dateString));
        mealFragments.add(EachMealFragment.newInstance(Util.AFTERNOON_SNACK_FILE, dateString));
        mealFragments.add(EachMealFragment.newInstance(Util.DINNER_FILE, dateString));
        mealFragments.add(EachMealFragment.newInstance(Util.EVENING_SNACK_FILE, dateString));

        // Update fragments to store images from database
        /*for (EachMealFragment emf : mealFragments) {
            emf.queryImageText();
        }*/

        ItemClickSupport.addTo(myList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // Position is recorded as position of selected meal in List
                Log.v("EachList", mCurrentPageName + " " + position);
                //dispatchTakePictureIntent(position);
                //mealFragments.get(position).mealClicked();

                if (!mealFragments.get(position).isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(mealFragments.get(position), "meal_fragment")
                            .addToBackStack(null)
                            .commit();
                } else {
                    mealFragments.get(position).mealClicked();
                    /*getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.popup_1, mealFragments.get(position))
                            .addToBackStack(null)
                            .commit();*/
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
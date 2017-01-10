package com.rtis.foodapp.ui.fragments;

/**
 * Created by rajul on 11/18/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;
import com.rtis.foodapp.callbacks.ItemClickSupport;
import com.rtis.foodapp.model.MealTimeItems;
import com.rtis.foodapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public  class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NAME = "section_name";
    private List<MealTimeItems> mItems;
    private String mCurrentPageName = null;
    public PlaceholderFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(String sectionName) {
       PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPageName = getArguments().getString(ARG_SECTION_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_everyday_list, container, false);
        RecyclerView myList=(RecyclerView)rootView.findViewById(R.id.myList);
        mItems = new ArrayList<>();
        mItems.add(new MealTimeItems(Util.BREAKFAST_STRING));
        mItems.add(new MealTimeItems(Util.MORNING_SNACK_STRING));
        mItems.add(new MealTimeItems(Util.LUNCH_STRING));
        mItems.add(new MealTimeItems(Util.AFTERNOON_SNACK_STRING));
        mItems.add(new MealTimeItems(Util.DINNER_STRING));
        mItems.add(new MealTimeItems(Util.EVENING_SNACK_STRING));

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        myList.setLayoutManager(manager);

        EveryDayMealTimingsListAdapter madapter = new EveryDayMealTimingsListAdapter(getContext(), mItems);
        myList.setAdapter(madapter);

        ItemClickSupport.addTo(myList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.v("EachList", mCurrentPageName + " " + position);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
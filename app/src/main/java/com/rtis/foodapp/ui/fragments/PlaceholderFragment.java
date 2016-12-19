package com.rtis.foodapp.ui.fragments;

/**
 * Created by rajul on 11/18/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;
import com.rtis.foodapp.model.MealTimeItems;

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
    private static final String ARG_SECTION_NUMBER = "section_number";
    private List<MealTimeItems> mItems;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
       PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_everyday_list, container, false);
        RecyclerView myList=(RecyclerView)rootView.findViewById(R.id.myList);
        mItems = new ArrayList<>();
        mItems.add(new MealTimeItems(" item 1"));
        mItems.add(new MealTimeItems(" item 2"));
        mItems.add(new MealTimeItems(" item 3"));
        mItems.add(new MealTimeItems(" item 4"));
        mItems.add(new MealTimeItems(" item 5"));
        mItems.add(new MealTimeItems(" item 6"));

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        myList.setLayoutManager(manager);

        EveryDayMealTimingsListAdapter madapter = new EveryDayMealTimingsListAdapter(getContext(), mItems);
        myList.setAdapter(madapter);
        return rootView;
    }
}
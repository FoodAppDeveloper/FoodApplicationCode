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
import android.widget.RelativeLayout;

import com.rtis.foodapp.R;
import com.rtis.foodapp.adapters.EveryDayMealTimingsListAdapter;
import com.rtis.foodapp.callbacks.ItemClickSupport;
import com.rtis.foodapp.model.MealTimeItems;
import com.rtis.foodapp.utils.ItemOffsetDecoration;
import com.rtis.foodapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that represents a particular day, containing each day's meals.
 * Instantiated in SectionsPagerAdapter.
 */
public class EachDayFragment extends Fragment {

    // The fragment argument representing the section number for this fragment.
    private static final String ARG_SECTION_NAME = "section_name";
    private static final String ARG_SECTION_DATE = "section_date";

    // Identifier variables
    private String dateString;
    private String mCurrentPageName;

    // Fragment view variables
    private EveryDayMealTimingsListAdapter mAdapter;
    private RelativeLayout mRelativeLayout;
    private RecyclerView myList;

    // Meal fragments and items contained in view
    private List<EachMealFragment> mealFragments;
    private List<MealTimeItems> mItems;

    /**
     * Empty Constructor
     */
    public EachDayFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section number.
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
        Log.v("Fragment:onCreate()", mCurrentPageName + " Created date="+dateString);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_everyday_list, container,
                false);
        mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.fragment_relativeLayout);

        // Populate MealTimeItems list
        myList = (RecyclerView) rootView.findViewById(R.id.myList);

        mItems = new ArrayList<>();
        mItems.add(new MealTimeItems(Util.BREAKFAST_STRING));           // 0
        mItems.add(new MealTimeItems(Util.MORNING_SNACK_STRING));       // 1
        mItems.add(new MealTimeItems(Util.LUNCH_STRING));               // 2
        mItems.add(new MealTimeItems(Util.AFTERNOON_SNACK_STRING));     // 3
        mItems.add(new MealTimeItems(Util.DINNER_STRING));              // 4
        mItems.add(new MealTimeItems(Util.EVENING_SNACK_STRING));       // 5

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

        // Set up layout of meals
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(),
                R.dimen.item_offset);
        myList.addItemDecoration(itemDecoration);
        myList.setLayoutManager(manager);
        mAdapter = new EveryDayMealTimingsListAdapter(getContext(), mItems);
        myList.setAdapter(mAdapter);

        // Populate and set up EachMealFragment list
        mealFragments = new ArrayList<>();

        Log.v("EDF:onCreateView()", dateString + " create meals");
        EachMealFragment breakfast = EachMealFragment.newInstance(Util.BREAKFAST_FILE,
                dateString);
        //Log.v("EDF", dateString + " create breakfast");
        breakfast.setUp(Util.BREAKFAST_FILE, dateString, this,
                mItems.get(Util.BREAKFAST));
        mealFragments.add(breakfast);

        EachMealFragment mornSnack = EachMealFragment.newInstance(Util.MORNING_SNACK_FILE,
                dateString);
        //Log.v("EDF", dateString + " create breakfast snack");
        mornSnack.setUp(Util.MORNING_SNACK_FILE, dateString, this,
                mItems.get(Util.MORNING_SNACK));
        mealFragments.add(mornSnack);

        EachMealFragment lunch = EachMealFragment.newInstance(Util.LUNCH_FILE, dateString);
        //Log.v("EDF", dateString + " create lunch");
        lunch.setUp(Util.LUNCH_FILE, dateString, this, mItems.get(Util.LUNCH));
        mealFragments.add(lunch);

        EachMealFragment afterSnack = EachMealFragment.newInstance(Util.AFTERNOON_SNACK_FILE,
                dateString);
        //Log.v("EDF", dateString + " create lunch snack");
        afterSnack.setUp(Util.AFTERNOON_SNACK_FILE, dateString, this,
                mItems.get(Util.AFTERNOON_SNACK));
        mealFragments.add(afterSnack);

        EachMealFragment dinner = EachMealFragment.newInstance(Util.DINNER_FILE, dateString);
        //Log.v("EDF", dateString + " create dinner");
        dinner.setUp(Util.DINNER_FILE, dateString, this, mItems.get(Util.DINNER));
        mealFragments.add(dinner);

        EachMealFragment evenSnack = EachMealFragment.newInstance(Util.EVENING_SNACK_FILE,
                dateString);
        //Log.v("EDF", dateString + " create dinner snack");
        evenSnack.setUp(Util.EVENING_SNACK_FILE, dateString, this,
                mItems.get(Util.EVENING_SNACK));
        mealFragments.add(evenSnack);

        setItemClickSupport();

        return rootView;
    }

    /**
     * Helper method to set EachMealFragment as clickable object.
     */
    private void setItemClickSupport() {
        ItemClickSupport.addTo(myList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // Position is recorded as position of selected meal in List
                Log.v("EDF", "EachList" + mCurrentPageName + " " + position);

                if (!mealFragments.get(position).isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(mealFragments.get(position), "meal_fragment")
                            .addToBackStack(null)
                            .commit();
                    Log.v("EDF", "Adding Meal Fragment: " + Integer.toString(position));
                } else {
                    mealFragments.get(position).mealClicked();
                }
            }
        });
    }

    /**
     * Helper method to update adapter.
     */
    public void update() {
        mAdapter.notifyDataSetChanged();
    }

}
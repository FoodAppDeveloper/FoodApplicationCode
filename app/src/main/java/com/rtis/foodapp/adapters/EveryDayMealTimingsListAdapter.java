package com.rtis.foodapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtis.foodapp.R;
import com.rtis.foodapp.model.MealTimeItems;

import java.util.List;

/**
 * Created by rajul on 12/9/2016.
 *
 * Adapter class to display each meal fragment on each day fragment.
 */
public class EveryDayMealTimingsListAdapter
        extends RecyclerView.Adapter<EveryDayMealTimingsListAdapter.EachItemHolder> {

    // Stores plate image to show on fragment
    private static Drawable d;

    // View variables
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mView;

    // Keep track of all the meal items
    private List<MealTimeItems> mItems;

    /**
     * Constructor to initialize member variables.
     *
     * @param context the Context in which the adapter is in
     * @param items List of MealTimeItems on the fragment page
     */
    public EveryDayMealTimingsListAdapter(Context context, List<MealTimeItems> items) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;
        int id = R.drawable.plate;
        d = context.getDrawable(id);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public EachItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = mLayoutInflater.inflate(R.layout.each_time_list_item, parent, false);
        // work here if you need to control height of the items
        // keep in mind that parent is RecyclerView in this case
        int height = parent.getMeasuredHeight() / 3;
        mView.setMinimumHeight(height - 9);
        mView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                height - 9));

        return new EachItemHolder(mView);
    }

    @Override
    public void onBindViewHolder(EachItemHolder holder, int position) {
        MealTimeItems item =  mItems.get(position);
        holder.txt_label.setText(item.getLabel());
        holder.fill(item.isFill());
    }

    /**
     * Static member class that contains the view of each meal fragment.
     */
    public static class EachItemHolder extends RecyclerView.ViewHolder {
        public TextView txt_label;
        public ImageView imageView;

        /**
         * Constructor to set variables.
         *
         * @param itemView the View to set.
         */
        public EachItemHolder(View itemView) {
            super(itemView);

            txt_label = (TextView) itemView.findViewById(R.id.txt_label);
            imageView = (ImageView) itemView.findViewById(R.id.eachMealImageView);
        }

        /**
         * Helper method to draw plate on fragment if needed.
         *
         * @param fill true if need be filled, false otherwise
         */
        public void fill(boolean fill) {
            if (fill) {
                imageView.setImageDrawable(d);
            } else {
                imageView.setImageDrawable(null);
            }
        }
    }
}

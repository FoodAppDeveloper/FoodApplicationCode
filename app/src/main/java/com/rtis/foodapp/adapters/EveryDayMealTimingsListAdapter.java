package com.rtis.foodapp.adapters;

import android.content.Context;
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
 */

public class EveryDayMealTimingsListAdapter extends RecyclerView.Adapter<EveryDayMealTimingsListAdapter.EachItemHolder>{

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<MealTimeItems> mItems;
    private EachItemHolder mCurrentHolder;

    public EveryDayMealTimingsListAdapter(Context context, List<MealTimeItems> items) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;
    }

    public EachItemHolder getmCurrentHolder() {
        return mCurrentHolder;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    @Override
    public EachItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.each_time_list_item, parent, false);
        // work here if you need to control height of your items
        // keep in mind that parent is RecyclerView in this case
        int height = parent.getMeasuredHeight() / 3;
        itemView.setMinimumHeight(height - 9);
        return new EachItemHolder(itemView);

    }

    @Override
    public void onBindViewHolder(EachItemHolder holder, int position) {
        MealTimeItems item =  mItems.get(position);
        holder.txt_label.setText(item.getLabel());
        mCurrentHolder = holder;
    }

    public static class EachItemHolder extends RecyclerView.ViewHolder {
        public TextView txt_label;
        public ImageView imageView;
        public EachItemHolder(View itemView) {
            super(itemView);
            txt_label = (TextView) itemView.findViewById(R.id.txt_label);
            imageView = (ImageView) itemView.findViewById(R.id.eachMealImageView);
        }

    }
}

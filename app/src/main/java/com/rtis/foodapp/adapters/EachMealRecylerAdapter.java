package com.rtis.foodapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtis.foodapp.R;

/**
 * Created by rajul on 1/13/2017.
 */

public class EachMealRecylerAdapter extends RecyclerView.Adapter<EachMealRecylerAdapter.EachMealHolder> {

    private final LayoutInflater mLayoutInflater;
    private  Context mContext;

    public EachMealRecylerAdapter(Context ctx)
    {
        mContext = ctx;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public EachMealHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.demo, parent, false);
        return new EachMealHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EachMealHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }
       static class EachMealHolder extends RecyclerView.ViewHolder {

        public EachMealHolder(View itemView) {
            super(itemView);
        }

    }
}

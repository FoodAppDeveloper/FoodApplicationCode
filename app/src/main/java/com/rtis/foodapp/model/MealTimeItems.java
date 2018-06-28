package com.rtis.foodapp.model;

/**
 * Created by rajul on 12/9/2016.
 */

public class MealTimeItems {
    public MealTimeItems(String itemLabel)
    {
        this.label=itemLabel;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String labels) {
        this.label = labels;
    }

    private String label;

}
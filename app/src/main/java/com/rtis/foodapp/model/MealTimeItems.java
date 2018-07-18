package com.rtis.foodapp.model;

/**
 * Created by rajul on 12/9/2016.
 */

public class MealTimeItems {
    private String label;
    private boolean fill;

    public MealTimeItems(String itemLabel)
    {
        this.label=itemLabel;
        fill = false;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String labels) {
        this.label = labels;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public boolean isFill() {
        return fill;
    }

}
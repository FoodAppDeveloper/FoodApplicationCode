package com.rtis.foodapp.model;

/**
 * Created by rajul on 12/9/2016.
 *
 * Identifies each meal fragment with label and whether it contains images.
 */
public class MealTimeItems {
    // Label to display which meal
    private String label;

    // Whether there is an image associated with the meal
    private boolean fill;

    /**
     * Constructor setting member variables.
     *
     * @param itemLabel the String label identifier to show meal name
     */
    public MealTimeItems(String itemLabel)
    {
        this.label=itemLabel;
        fill = false;
    }

    /* Setters and Getters */

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
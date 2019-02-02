package com.example.aryan.hack;

/**
 * Created by UTSAV JAIN on 1/26/2019.
 */
public class ReviewTemplate {
    private float rating;
    private String description;

    public ReviewTemplate() {
    }

    public ReviewTemplate(float rating, String description) {

        this.rating = rating;
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

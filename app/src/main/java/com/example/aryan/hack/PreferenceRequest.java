package com.example.aryan.hack;

/**
 * Created by user on 1/20/19.
 */

public class PreferenceRequest {

    private String name;
    private String pref1;
    private String pref2;

    public PreferenceRequest(String name, String pref1, String pref2) {
        this.name = name;
        this.pref1 = pref1;
        this.pref2 = pref2;
    }

    public String getName() {
        return name;
    }

    public String getPref1() {
        return pref1;
    }

    public String getPref2() {
        return pref2;
    }
}
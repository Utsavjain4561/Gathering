package com.example.aryan.hack;

/**
 * Created by user on 1/20/19.
 */

public class Admin {

    private String adminName;
    private String phoneNumber;
    private String password;
    private String uname;

    public Admin(String adminName, String phoneNumber, String password) {
        this.adminName = adminName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getAdminName() {

        return adminName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }
    
    public String getUserName() {
        return uname;
    }
}

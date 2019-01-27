package com.example.aryan.hack;

/**
 * Created by user on 1/20/19.
 */

public class Employee {

    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Employee(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
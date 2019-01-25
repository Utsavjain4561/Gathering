package com.example.aryan.hack;

/**
 * Created by user on 1/19/19.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Mehul Garg on 01-09-2018.
 */

public class signupAdmin extends AppCompatActivity {

    private TextInputEditText name_field;
    private TextInputEditText phone_field;
    private TextInputEditText city_field;
    private TextInputEditText pin_field;
    private TextInputEditText password_field;
    private TextView login;
    private Button signUp_button;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private int tmp = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_admin);

        auth = FirebaseAuth.getInstance();
        login=findViewById(R.id.login);
        signUp_button=findViewById(R.id.signUp_button);
        name_field = findViewById(R.id.name);
        city_field = findViewById(R.id.cityOfService);
        phone_field = findViewById(R.id.mobile);
        password_field = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);


        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_data(view);
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signupAdmin.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // for underline purpose(UI)
        SpannableString text = new SpannableString("Login");
        text.setSpan(new UnderlineSpan(), 0, 5, 0);
        login.setText(text);
    }

    void write_data(View v){


        final String name = name_field.getText().toString();
        final String city = city_field.getText().toString();
        final String num = phone_field.getText().toString();
        final String password = password_field.getText().toString();

        //Empty check
        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(num)){

            Toast.makeText(getApplicationContext(), "All fields not filled !!", Toast.LENGTH_SHORT).show();
            return;
        }

        //password length check
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        // mobile number check
        if(!isValidMobile(num)){
            Toast.makeText(getApplicationContext(), "Invalid mobile number !!", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        Admin adminObj = new Admin(name, num, password);
        //Added data to database
        databaseRef = FirebaseDatabase.getInstance().getReference("Admin");
        String key = city.toLowerCase();
        databaseRef.child(key).setValue(adminObj);

        // session created using sharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createLoginSession(key, "Admin");

        //register user on fireBase Authentication

        //create user using mobile verification

    }


    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(signupAdmin.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
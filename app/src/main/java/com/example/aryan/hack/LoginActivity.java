package com.example.aryan.hack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private TextView admin, employee;
    private Button login, guestLogin;
    private TextInputEditText inputPhone, inputPassword;
    private Button forgot_password;
    private FirebaseAuth auth;
    private RadioGroup radioGroup_type;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private String phone, password;
    private int flag = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SessionManager sm = new SessionManager(getApplicationContext());
            HashMap<String, String> details = sm.getUserDetails();
            String tmp1 = details.get("id");
            String tmp2 = details.get("role");
            if (!TextUtils.isEmpty(tmp1) && !TextUtils.isEmpty(tmp2)) {
                Toast.makeText(this, "User " + tmp1 + " logged in", Toast.LENGTH_SHORT).show();

                if (tmp2.equals("admin")) {
                    goto_Admin();
                } else {
                    goto_Employee();
                }
            }
        }

        setContentView(R.layout.login);

        admin = findViewById(R.id.goAdmin);
        employee = findViewById(R.id.goEmployee);
        login = findViewById(R.id.login_press);
        guestLogin = findViewById(R.id.guest_login_press);
        radioGroup_type = findViewById(R.id.radioGroup_type_person);
        inputPhone = findViewById(R.id.phone);
        inputPassword = findViewById(R.id.password);
        forgot_password = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, signupAdmin.class);
                startActivity(intent);
                finish();
            }
        });

        employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, signupEmployee.class);
                startActivity(intent);
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = inputPhone.getText().toString();
                password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    Snackbar snackbar = Snackbar.make(v, "Enter Phone!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar snackbar = Snackbar.make(v, "Enter Password!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user


            }
        });

       /* // for underline purpose(UI)
        SpannableString text = new SpannableString("Sign up as a Admin");
        text.setSpan(new UnderlineSpan(), 0, 20, 0);
        admin.setText(text);
        // for underline purpose(UI)
        SpannableString text1 = new SpannableString("Sign up as a Employee");
        text1.setSpan(new UnderlineSpan(), 0, 24, 0);
        employee.setText(text1);*/

    }


    void goto_Admin() {
        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        startActivity(intent);
        finish();
    }

    void goto_Employee() {
        Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
        startActivity(intent);
        finish();
    }

    String RadioButtonSelect(int selectId) {
        RadioButton radioButton = findViewById(selectId);
        return radioButton.getText().toString();
    }
}

package com.example.aryan.hack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


/**
 * Created by Mehul Garg on 01-09-2018.
 */

public class signupEmployee extends AppCompatActivity {

    private TextInputEditText name_field;
    private TextInputEditText num_field;
    private TextInputEditText city_field;
    private TextInputEditText password_field;
    private TextInputEditText pref1_field;
    private TextInputEditText pref2_field;
    private TextView login;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button signUp;
    private ProgressBar progressBar;
    DatabaseReference databaseRef,databaseRef2;
    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_employee);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        login=findViewById(R.id.login);
        signUp=findViewById(R.id.signUp_button);
        name_field = findViewById(R.id.name1);
        num_field = findViewById(R.id.mobile1);
        city_field = findViewById(R.id.cityofService1);
        pref1_field = findViewById(R.id.area1);
        pref2_field = findViewById(R.id.area2);
        password_field = findViewById(R.id.password1);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_data();
            }
        });

    }

    void write_data(){

        final String name = name_field.getText().toString();
        final String city = city_field.getText().toString();
        final String num = num_field.getText().toString();
        final String password = password_field.getText().toString();
        final String prefArea1 = pref1_field.getText().toString();
        final String prefArea2 = pref2_field.getText().toString();

        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(num) || TextUtils.isEmpty(prefArea1) || TextUtils.isEmpty(prefArea2)){

            Toast.makeText(getApplicationContext(), "All fields not filled !!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidMobile(num)){
            Toast.makeText(getApplicationContext(), "Invalid mobile number !!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        Employee employee = new Employee(name, password);
        //Added data to database
        databaseRef = FirebaseDatabase.getInstance().getReference("Employee");
        final String key1 =  city.toLowerCase();
        final String key = num;
        databaseRef.child(key1).child(key).setValue(employee);

        // session created using sharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createLoginSession(key1+"_"+key,"Employee");

        databaseRef2 = FirebaseDatabase.getInstance().getReference("Requests");
        PreferenceRequest preferenceRequest = new PreferenceRequest(name, prefArea1, prefArea2);
        String keyreq = databaseRef2.child(city).push().getKey();
        databaseRef2.child(city).child(keyreq).setValue(preferenceRequest);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("error",e.toString());
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        mVerificationId = s;
                        mResendToken = forceResendingToken;
//                        AlertDialog.Builder builder = new AlertDialog.Builder(signupEmployee.this);
//                        final EditText input = new EditText(signupEmployee.this);
//                        input.setHint("Verification Code");
//                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.MATCH_PARENT,
//                                LinearLayout.LayoutParams.MATCH_PARENT);
//                        input.setLayoutParams(lp);
//                        builder.setView(input);
//                        builder.setTitle("Enter Verification Code").setNegativeButton("Submit", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Snackbar.make(getCurrentFocus(),"Checking Status Code",Snackbar.LENGTH_LONG).show();
//                            }
//                        }).show();


                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(signupEmployee.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential)
    {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            Intent intent = new Intent(signupEmployee.this,DriverActivity.class);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }
}
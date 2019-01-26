package com.example.aryan.hack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;


/**
 * Created by Mehul Garg on 01-09-2018.
 */

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private TextInputEditText name_field;
    private TextInputEditText num_field;
    private TextInputEditText city_field;
    private TextInputEditText password_field;
    private TextInputEditText place_field;
    private Spinner sp;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private Button signUp;
    private ProgressBar progressBar;
    DatabaseReference databaseRef,databaseRef2;
    private FirebaseAuth mAuth;
    private String role;
    String num;
    // ...
// Initialize Firebase Auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        signUp=findViewById(R.id.signUp_button);
        name_field = findViewById(R.id.name1);
        num_field = findViewById(R.id.mobile1);
        city_field = findViewById(R.id.cityofService1);
        place_field = findViewById(R.id.place);
        password_field = findViewById(R.id.password1);
        sp = findViewById(R.id.spinner2);

        // spinner code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                write_data();
                go_to_respective_activity();
            }
        });

    }



    void write_data(){

        final String name = name_field.getText().toString();
        final String city = city_field.getText().toString();
        num = num_field.getText().toString();
        final String password = password_field.getText().toString();
        final String placeName = place_field.getText().toString();

        if (TextUtils.isEmpty(city) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(num) || TextUtils.isEmpty(placeName)){

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


        //Added data to database
        databaseRef = FirebaseDatabase.getInstance().getReference("gatherings");
        final String key1 = placeName.toLowerCase().replaceAll(" ","");
        AuthDetails authDetails = new AuthDetails(name, password);
        databaseRef.child(key1).child(role).child(num).setValue(authDetails);
        // session created using sharedPreference
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createLoginSession(num,role,placeName);

        //add request for validation by admin

        databaseRef2 = FirebaseDatabase.getInstance().getReference("gatherings").child(key1);
        validationRequest request = new validationRequest(name, num, role);
        String keyreq = databaseRef2.child(city).child(num).getKey();
        databaseRef2.child("requests").child(keyreq).setValue(request);

        //authentication by phone

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
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, String.valueOf(mResendToken)));
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    void go_to_respective_activity(){
        if(role.equals("cleaner")){
            Intent intent=new Intent(SignUp.this, CleanerMainActivity.class);
            intent.putExtra("number",num);
            startActivity(intent);
            finish();

        }
        else if(role.equals("admin")){
            Intent intent=new Intent(SignUp.this, AdminMainActivity.class);
            intent.putExtra("number",num);
            startActivity(intent);
            finish();

        }
        else{
            Intent intent=new Intent(SignUp.this, DoctorMainActivity.class);
            intent.putExtra("number",num);
            startActivity(intent);
            finish();

        }
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
                            go_to_respective_activity();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        role = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}

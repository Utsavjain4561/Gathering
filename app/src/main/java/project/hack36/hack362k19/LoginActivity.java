package project.hack36.hack362k19;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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


public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button login, guestLogin;
    private TextView signUp;
    private Spinner spinner;
    private TextInputEditText inputPhone, inputPassword;
    private Button forgot_password;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef, databaseRef2;
    private String phone, password, tmp1, tmp2, tmp3, role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            SessionManager sm = new SessionManager(getApplicationContext());
            HashMap<String, String> details = sm.getUserDetails();
            tmp1 = details.get("id");
            tmp2 = details.get("role");
            tmp3 = details.get("place");
            if (!TextUtils.isEmpty(tmp1) && !TextUtils.isEmpty(tmp2)) {
                Toast.makeText(this, "User " + tmp1 + " logged in", Toast.LENGTH_SHORT).show();

                if (tmp2.equals("admin")) {
                    goto_Admin();
                } else if(tmp2.equals("cleaner")){
                    goto_Cleaner();
                }
                else{
                    goto_Doctor();
                }
            }
        }

        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_press);
        guestLogin = findViewById(R.id.guest_login_press);
        signUp = findViewById(R.id.goSign);
        inputPhone = findViewById(R.id.phone);
        inputPassword = findViewById(R.id.password);
        forgot_password = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progressBar);
        spinner = findViewById(R.id.spinner1);
        auth = FirebaseAuth.getInstance();

        // spinner list adapter population code

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
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
                databaseRef = FirebaseDatabase.getInstance().getReference("gathering");
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String primKey = dataSnapshot.getKey();
                        databaseRef2 = FirebaseDatabase.getInstance().getReference(primKey);
                        databaseRef2.child(role).child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    AuthDetails authDetails = dataSnapshot.getValue(AuthDetails.class);
                                    if(authDetails.getPassword().equals(password)){

                                        Toast.makeText(getApplicationContext(), "Logged in!!", Toast.LENGTH_SHORT).show();
                                        go_to_respective_activity();

                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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

    void go_to_respective_activity(){
        if(role.equals("cleaner")){
            Intent intent=new Intent(LoginActivity.this, CleanerMainActivity.class);
            startActivity(intent);
            finish();

        }
        else if(role.equals("admin")){
            Intent intent=new Intent(LoginActivity.this, AdminMainActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            Intent intent=new Intent(LoginActivity.this, DoctorMainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        role = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    void goto_Admin() {
        Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        startActivity(intent);
        finish();
    }

    void goto_Cleaner() {
        Intent intent = new Intent(LoginActivity.this, CleanerMainActivity.class);
        startActivity(intent);
        finish();
    }

    void goto_Doctor(){
        Intent intent = new Intent(LoginActivity.this, DoctorMainActivity.class);
        startActivity(intent);
        finish();
    }

    String RadioButtonSelect(int selectId) {
        RadioButton radioButton = findViewById(selectId);
        return radioButton.getText().toString();
    }
}
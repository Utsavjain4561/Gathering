package com.example.aryan.hack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartupActivity extends AppCompatActivity {
    private Button userButton;
    private Button driverButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        userButton = (Button) findViewById(R.id.user);
        driverButton = (Button) findViewById(R.id.driver);

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driverIntent = new Intent(StartupActivity.this,DriverActivity.class);
                startActivity(driverIntent);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userIntent = new Intent(StartupActivity.this,MainActivity.class);
                startActivity(userIntent);
            }
        });
    }
}

package com.example.snakespiciesdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class selectRegistrationActivity extends AppCompatActivity {
    private Button victimButton;
    private Button doctorButton;
    private TextView SignIn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_registration);
        SignIn=findViewById(R.id.SignIn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(selectRegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        victimButton=findViewById(R.id.victimButton);
        doctorButton=findViewById(R.id.doctorButton);

        victimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(selectRegistrationActivity.this,victimActivity.class);
                startActivity(intent);
            }
        });

        doctorButton=findViewById(R.id.doctorButton);
        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(selectRegistrationActivity.this,DoctorRegistrationActivity.class);
               startActivity(intent);
            }
        });
    }

}
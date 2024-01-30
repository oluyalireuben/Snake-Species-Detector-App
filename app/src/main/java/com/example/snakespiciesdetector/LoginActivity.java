package com.example.snakespiciesdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

import java.util.Objects;



public class LoginActivity extends AppCompatActivity {


    private TextInputEditText Password, registerEmail;
    private Button LoginReg;
    private TextView backButton;

    private ProgressDialog loader;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backButton = findViewById(R.id.backButton);
        LoginReg = findViewById(R.id.LoginReg);
        Password = findViewById(R.id.Password);
        registerEmail = findViewById(R.id.registerEmail);
        loader = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        LoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Objects.requireNonNull(registerEmail.getText()).toString().trim();
                final String password = Objects.requireNonNull(Password.getText()).toString().trim();

                if (TextUtils.isEmpty(email)) {
                    registerEmail.setError("Email is required!");
                }
                if (TextUtils.isEmpty(password)) {
                    Password.setError("Password is required!");
                } else {
                    loader.setMessage("Registering you...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String error = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(LoginActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            } else {
                                String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("userLogin").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("email", email);
                                userInfo.put("password", password);
                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Data Set Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        // loader.dismiss();
                                        finish();
                                    }
                                });
                            }

                            Intent intent = new Intent(LoginActivity.this, imageClassificationActivity.class);
                            startActivity(intent);
                            backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LoginActivity.this, selectRegistrationActivity.class);
                                    startActivity(intent);
                                    loader.dismiss();
                                    finish();
                                }

                            });
                        }
                    });

                }
            }
        });
    }
}




package com.example.snakespiciesdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorRegistrationActivity extends AppCompatActivity {

    private TextInputEditText logo, LoginPageQuestion, loginEmail, loginPassword, registrationPhoneNumber, registrationIdNumber, registrationFullName, title;
    private Button regPage;
    private CircleImageView profile_image;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;
    private TextView backButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration);
        // regPageQuestion = findViewById(R.id.RegPageQuestion);
        regPage = findViewById(R.id.regPage);
        loginPassword = findViewById(R.id.loginPassword);
        registrationPhoneNumber = findViewById(R.id.registrationPhoneNumber);
        registrationIdNumber = findViewById(R.id.registrationIdNumber);
        registrationFullName = findViewById(R.id.registrationFullName);
        profile_image = findViewById(R.id.profile_image);
        loginEmail = findViewById(R.id.loginEmail);
        backButton = findViewById(R.id.backButton);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


        regPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = loginEmail.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                final String fullName = registrationFullName.getText().toString().trim();
                final String number = registrationIdNumber.getText().toString().trim();
                final String phoneNumber = registrationPhoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(fullName)) {
                    registrationFullName.setError("full name is required");
                    return;
                }
                if (TextUtils.isEmpty(number)) {
                    registrationIdNumber.setError("Id number is required");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    registrationPhoneNumber.setError("Phone number is required");
                    return;
                }
                if (resultUri == null) {
                    Toast.makeText(DoctorRegistrationActivity.this, "profile image is required!", Toast.LENGTH_SHORT).show();
                } else {
                    loader.setMessage("Registration in progress...");
                    loader.show();
                    loader.setCanceledOnTouchOutside(false);


                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                String error = task.getException().toString();
                                Toast.makeText(DoctorRegistrationActivity.this, "error occurred " + error, Toast.LENGTH_SHORT).show();
                            } else {
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId);
                                HashMap userInfo = new HashMap();
                                userInfo.put("name", fullName);
                                userInfo.put("email", email);
                                userInfo.put("idNumber", number);
                                userInfo.put("phoneNumber", phoneNumber);
                                userInfo.put("password", password);
                                userInfo.put("type", "patient");

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(DoctorRegistrationActivity.this, "Details set successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(DoctorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                        loader.dismiss();
                                    }
                                });
                                if (resultUri != null) {
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile pictures").child(currentUserId);

                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //tries to upload image
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();

                                    UploadTask uploadTask = filepath.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            finish();
                                            return;
                                        }
                                    });
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (taskSnapshot.getMetadata() != null) {
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    //saves the picture as url
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map newImageMap = new HashMap();
                                                        newImageMap.put("profilePictureUrl", imageUrl);

                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(DoctorRegistrationActivity.this, "registration successful", Toast.LENGTH_SHORT).show();
                                                                } else
                                                                    Toast.makeText(DoctorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                                finish();
                                            }

                                        }
                                    });
                                    Intent intent = new Intent(DoctorRegistrationActivity.this, DoctorReviewActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }

                            }
                        }
                    });
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DoctorRegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);

        }
    }
}
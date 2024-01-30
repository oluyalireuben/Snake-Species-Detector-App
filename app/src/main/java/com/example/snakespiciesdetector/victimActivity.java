package com.example.snakespiciesdetector;

import static com.example.snakespiciesdetector.R.id.regPage;

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


public class victimActivity extends AppCompatActivity {
//    private TextView backButton;
//    private CircleImageView profile_image;
//    private TextInputEditText IdNumber,PhoneNumber,Password,registerEmail,registerFullName;
//    private Button DonorReg;
//    private Uri resultUri;
//    private ProgressDialog loader=new ProgressDialog(this);
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference userDatabaseRef;
//
//
private TextView regPageQuestion;
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
        setContentView(R.layout.activity_victim);
            regPageQuestion = findViewById(R.id.RegPageQuestion);
            regPage = findViewById(R.id.regPage);
            loginPassword = findViewById(R.id.loginPassword);
            registrationPhoneNumber = findViewById(R.id.registrationPhoneNumber);
            registrationIdNumber = findViewById(R.id.registrationIdNumber);
            registrationFullName = findViewById(R.id.registrationFullName);
            profile_image = findViewById(R.id.profile_image);
            loginEmail = findViewById(R.id.loginEmail);
            backButton=   findViewById(R.id.backButton);
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
                                    Toast.makeText(victimActivity.this, "profile image is required!", Toast.LENGTH_SHORT).show();
                            } else {
                                    loader.setMessage("Registration in progress...");
                                    loader.show();
                                    loader.setCanceledOnTouchOutside(false);


                                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (!task.isSuccessful()) {
                                                            String error = task.getException().toString();
                                                            Toast.makeText(victimActivity.this, "error occurred " + error, Toast.LENGTH_SHORT).show();
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
                                                                                    Toast.makeText(victimActivity.this, "Details set successfully", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                    Toast.makeText(victimActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
                                                                                                                                    Toast.makeText(victimActivity.this, "registration successful", Toast.LENGTH_SHORT).show();
                                                                                                                            } else
                                                                                                                                    Toast.makeText(victimActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                            });

                                                                                                    }
                                                                                            });
                                                                                            finish();
                                                                                    }

                                                                            }
                                                                    });
                                                                    Intent intent = new Intent(victimActivity.this, imageClassificationActivity.class);
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
                Intent intent=new Intent(victimActivity.this,LoginActivity.class);
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

//        regPageQuestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(patientsRegistrationActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
        }
}

//        backButton=   findViewById(R.id.backButton);
//        IdNumber= findViewById(R.id.IdNumber);
//        PhoneNumber=  findViewById(R.id.PhoneNumber);
//        Password=   findViewById(R.id.Password);
//        registerEmail=   findViewById(R.id.registerEmail);
//        DonorReg=   findViewById(R.id.DonorReg);
//        registerFullName=  findViewById(R.id.registerFullName);
//        firebaseAuth=FirebaseAuth.getInstance();
//        profile_image=findViewById(R.id.profile_image);
//
//
//        DonorReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String email= Objects.requireNonNull(registerEmail.getText()).toString().trim();
//                final String password= Objects.requireNonNull(Password.getText()).toString().trim();
//                final  String fullName= Objects.requireNonNull(registerFullName.getText()).toString().trim();
//                final String idNumber= Objects.requireNonNull(IdNumber.getText()).toString().trim();
//                final String phoneNumber= Objects.requireNonNull(PhoneNumber.getText()).toString().trim();
//
//                if(TextUtils.isEmpty(email)){
//                    registerEmail.setError("Email is required!");
//                    return;
//                }
//                if(TextUtils.isEmpty(password)){
//                    Password.setError("Password is required!");
//                    return;
//                }
//                if(TextUtils.isEmpty(fullName)){
//                    registerFullName.setError("Your full name is required!");
//                    return;
//                }
//                if(TextUtils.isEmpty(idNumber)){
//                    IdNumber.setError("ID number is required!");
//                    return;
//                }
//                if(TextUtils.isEmpty(phoneNumber)){
//                    PhoneNumber.setError("phoneNumber is required!");
//
//                }else{
//                    loader.setMessage("Registering you...");
//                    loader.setCanceledOnTouchOutside(false);
//                    loader.show();
//                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (!task.isSuccessful()) {
//                                String error = Objects.requireNonNull(task.getException()).toString();
//                                Toast.makeText(victimActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
//                            } else {
//                                String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
//                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
//                                HashMap userInfo = new HashMap();
//                                userInfo.put("Id", currentUserId);
//                                userInfo.put("email", email);
//                                userInfo.put("Name", fullName);
//                                userInfo.put("IdNumber", idNumber);
//                                userInfo.put("phoneNumber", phoneNumber);
//                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
//                                    @Override
//                                    public void onComplete(@NonNull Task task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(victimActivity.this, "Data Set Successfully", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(victimActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
//                                        }
//                                        // loader.dismiss();
//                                        finish();
//                                    }
//
//                                });
////                                if (resultUri != null) {
//                                    it uploads the image to google cloud
//                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile Images").child(currentUserId);
//                                    Bitmap bitmap = null;
//                                    try {
//                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    //tries to upload the data to the cloud
//                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                    Objects.requireNonNull(bitmap).compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
//                                    byte[] data = byteArrayOutputStream.toByteArray();
//                                    UploadTask uploadTask = filePath.putBytes(data);
//                                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(victimActivity.this, "Failed to upload the Image", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                            if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
//                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
//                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                    @Override
//                                                    public void onSuccess(Uri uri) {
//                                                        //we've gotten the url now its a matter of uploading it to the database
//                                                        String imageUrl = uri.toString();
//                                                        Map newImageUrl = new HashMap();
//                                                        newImageUrl.put("profileImageUrl", imageUrl);
//
//                                                        //upload it to the database
//                                                        userDatabaseRef.updateChildren(newImageUrl).addOnCompleteListener(new OnCompleteListener() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task task) {
//                                                                if (task.isSuccessful()) {
//                                                                    Toast.makeText(victimActivity.this, "Image Url uploaded successfully", Toast.LENGTH_SHORT).show();
//                                                                } else {
//                                                                    Toast.makeText(victimActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            }
//                                                        });
//                                                        finish();
//                                                    }
//                                                });
//                                                Intent intent = new Intent(victimActivity.this, imageClassificationActivity.class);
//                                                startActivity(intent);
//                                                loader.dismiss();
//                                                finish();
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                    });
//
//                }
//            }
//        });
//
//        profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_PICK);
//                intent. setType("image/** ");
//                startActivityForResult(intent,1);
//            }
//        });
//
//
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(victimActivity.this,LoginActivity.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//    //displays the image that the user has picked from the gallery
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==1&& resultCode==RESULT_OK&& data!=null){
//            resultUri=data.getData();
//            profile_image.setImageURI(resultUri);
//        }
//    }
//}


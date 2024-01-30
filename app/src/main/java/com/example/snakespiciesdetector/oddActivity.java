package com.example.snakespiciesdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class oddActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "snake species name";
    public static final String TAG = "MainActivity";
    private Button SEND;
    private TextInputEditText species;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odd);
        species = findViewById(R.id.species);
        SEND = findViewById(R.id.SEND);
        loader = new ProgressDialog(this);

        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proceed = species.getText().toString().trim();
                if (TextUtils.isEmpty(proceed)) {
                    SEND.setError("species name is required");
                } else {
                    loader.setMessage("Sending...");
                    loader.show();
                    loader.setCanceledOnTouchOutside(false);
                }
                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Species");
                HashMap userInfo = new HashMap();
                userInfo.put(KEY_TITLE, proceed);
                userInfo.put("Patient located at ", "Kabarnet,Baringo");

                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(oddActivity.this, "Details sent successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(oddActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(oddActivity.this, imageClassificationActivity.class);
                startActivity(intent);
                finish();
                loader.dismiss();

            }
        });
    }
}









//    private FirebaseFirestore db= FirebaseFirestore.getInstance();
//    private DocumentReference details=db.collection("snake species").document("Baringo,kabarnet");
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_odd);
//        species=findViewById(R.id.species);
//        SEND=findViewById(R.id.SEND);
//
//        SEND.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String proceed=species.getText().toString().trim();
//                Map<String,Object> data=new HashMap<>();
//                data.put(KEY_TITLE,proceed);
//                details.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(oddActivity.this, "Sent Successfully", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e.toString());
//                    }
//                });
//
//            }
//        });
//
//    }
//}
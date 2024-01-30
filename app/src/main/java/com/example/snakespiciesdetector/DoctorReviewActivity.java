package com.example.snakespiciesdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorReviewActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "title";
    public static final String KEY_NAME = "name";
    public static final String TAG = "DoctorReviewActivity";
    private Button doctorButton;
    private TextView yeahTitle;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference("Species");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_review);
        yeahTitle = findViewById(R.id.yeahTitle);
        doctorButton = findViewById(R.id.doctorButton);


        doctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String title = snapshot.getValue().toString();
                            yeahTitle.setText(title);
                        }else {
                            Toast.makeText(DoctorReviewActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.toString());
                    }
                });
            }
        });
    }
}


//        doctorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                details.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot.exists()){
//                        String title=documentSnapshot.getString(KEY_TITLE);
//                        yeahTitle.setText(title);
//                        }else{
//                            Toast.makeText(DoctorReviewActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: "+ e.toString());
//
//                    }
//                });
//
//
//            }
//        });
//    }
//}
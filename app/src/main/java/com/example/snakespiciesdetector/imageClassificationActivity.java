package com.example.snakespiciesdetector;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class imageClassificationActivity extends AppCompatActivity {


        private static final int RESULT_LOAD_IMAGE = 123;
        public static final int IMAGE_CAPTURE_CODE = 654;
        private static final int PERMISSION_CODE = 321;
        private ImageView frame, innerImage;
        private TextView resultTv;
        private Uri image_uri;
        private Button proceed;

        private int mInputSize = 224;
        private String mModelPath = "model_unquant.tflite";
        private String mLabelPath = "labels.txt";
        private Classifier classifier;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_image_classification);
            frame = findViewById(R.id.imageView);
            innerImage = findViewById(R.id.imageView2);
            resultTv = findViewById(R.id.textView);
            proceed=findViewById(R.id.proceed);

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(imageClassificationActivity.this,oddActivity.class);
                    startActivity(intent);
                }
            });
            //TODO chose image from gallery
            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }
            });

            //TODO capture image using camera
            frame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED) {
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, PERMISSION_CODE);
                        } else {
                            openCamera();
                        }
                    } else {
                        openCamera();
                    }
                    return false;
                }
            });

            //TODO initialize the Classifier class object. This class will load the model and using its method we will pass input to the model and get the output
            try {
                classifier = new Classifier(getAssets(), mModelPath, mLabelPath, mInputSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TODO ask for permission of camera upon first launch of application
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }

        //TODO opens camera so that user can capture image
        private void openCamera() {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                image_uri = data.getData();
                innerImage.setImageURI(image_uri);
                doInference();
            }

            if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
                innerImage.setImageURI(image_uri);
                doInference();
            }
        }

        //TODO pass image to the model and shows the results on screen
        public void doInference() {
            Bitmap bitmap = uriToBitmap(image_uri);
            List<Classifier.Recognition> result = classifier.recognizeImage(bitmap);
            resultTv.setText("");
            for (int i = 0; i < result.size(); i++) {
                resultTv.append(result.get(i).title + " " + result.get(i).confidence + "\n");
            }
        }

        //TODO takes URI of the image and returns bitmap
        private Bitmap uriToBitmap(Uri selectedFileUri) {
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(selectedFileUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                parcelFileDescriptor.close();
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();

        }
    }

package com.example.snakespiciesdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationScreenActivity extends AppCompatActivity {
Animation topanimation,botttomanimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);

        ImageView logo = (ImageView) findViewById(R.id.logo);
        TextView slogan = (TextView) findViewById(R.id.slogan);
        TextView title = (TextView) findViewById(R.id.title);

        topanimation= AnimationUtils.loadAnimation(this,R.anim.topanimation);
        botttomanimation=AnimationUtils.loadAnimation(this,R.anim.botttomanimation);

        logo.setAnimation(topanimation);
        title.setAnimation(botttomanimation);
        slogan.setAnimation(botttomanimation);

        int animation_screen=4300;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(AnimationScreenActivity.this,selectRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        },animation_screen);

    }
}
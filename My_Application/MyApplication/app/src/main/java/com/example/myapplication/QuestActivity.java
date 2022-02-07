package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class QuestActivity extends AppCompatActivity {

    private ImageButton goBigOrGoHome;
    private ImageButton goToProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        goBigOrGoHome = findViewById(R.id.home_button_quest);
        goToProfile = findViewById(R.id.profile_button_quest);

        goBigOrGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
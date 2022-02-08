package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton profileButton;
    private ImageButton questButton;
    private Button startQuizButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        profileButton = findViewById(R.id.profile_button);
        startQuizButton = findViewById(R.id.turnOnQuiz);
        questButton = findViewById(R.id.quest_button);

        questButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        startQuizButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_button:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.turnOnQuiz:
                Intent intent_two = new Intent(this, QuizSettings.class);
                startActivity(intent_two);
                break;
            case R.id.quest_button:
                Intent intent_Three = new Intent(this, QuestActivity.class);
                startActivity(intent_Three);
                break;
        }
    }
}
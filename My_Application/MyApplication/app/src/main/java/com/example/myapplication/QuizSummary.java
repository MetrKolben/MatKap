package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuizSummary extends AppCompatActivity implements View.OnClickListener {

    TextView percentage;
    TextView ratingText;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        percentage = findViewById(R.id.percentage);
        ratingText = findViewById(R.id.ratingText);
        backButton = findViewById(R.id.goBackToMainScreen);
        System.out.println("Jsi tady fajn \n\n\n\n\n #########################################");
        backButton.setOnClickListener(this);

        int rating = (int) getIntent().getSerializableExtra("percent");
        int numOfQuestions = (int) getIntent().getSerializableExtra("numberofquestions");
        percentage.setText("" + rating);
        setResultMessage(rating, ratingText, numOfQuestions);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    void setResultMessage(int rate, TextView tv, int howmanyquestions) {
        int i = rate / 10;
        if (i > howmanyquestions * 0.7) {
            tv.setText(getString(R.string.goodResult));
        } else if(i <= howmanyquestions * 0.7 && i >= howmanyquestions * 0.5) {
            tv.setText(getString(R.string.averageResult));
        } else tv.setText(getString(R.string.badResult));
    }
}
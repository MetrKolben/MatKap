package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.database.Sql;

public class MainActivity extends AppCompatActivity {

    Button goToSignInBtn;
    Button test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///

//        try {
//            Connect.openOrCreateGeneralDatabase(this);

//        System.out.println(Sql.getQuestionList(this));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ///

        goToSignInBtn = findViewById(R.id.goToSignInButton);
        goToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        test = findViewById(R.id.testbutton);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizSettings.class);
                startActivity(intent);
                Sql.QuestionList questionList = Sql.getQuestionList(MainActivity.this, new String[]{"", ""});
//        System.out.println(questionList.questionList.size());
//                System.out.println(questionList.getNQuestions(12).get(0).text);
//                System.out.println(Sql.FilterType.COUNTRY.items);
            }
        });



    }
}
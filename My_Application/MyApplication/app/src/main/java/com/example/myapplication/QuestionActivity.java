package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myapplication.database.Sql;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    TextView questionText;
    RadioGroup answers;
    RadioButton answerA;
    RadioButton answerB;
    RadioButton answerC;
    RadioButton answerD;

    public static final int СКОЛЬКО_ВОПРОСОВ = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);


        questionText = findViewById(R.id.questionId);
        answers = findViewById(R.id.radioGroupAnswers);
        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);


        List<String> listOfMovements = (List<String>) getIntent().getSerializableExtra("passDataList");

        Sql.QuestionList questionList = Sql.getQuestionList(this, Sql.Filter.formatFilters(listOfMovements));

        int goligichest = questionList.getPossibleQuestionsCount();



        /*
        10 náhodnejch otázek ze vsech moznejch
         */
        List<Sql.Question> questions = questionList.getNQuestions(goligichest);

        System.out.println(questions.toString());
//        for (Sql.Question question : questions) {
//            System.out.println(question.text + " \n           "
//                    + "[" + question.getA().text + ", " + question.getA().isRight + "]\n           "
//                    + "[" + question.getB().text + ", " + question.getB().isRight + "]\n           "
//                    + "[" + question.getC().text + ", " + question.getC().isRight + "]\n           "
//                    + "[" + question.getD().text + ", " + question.getD().isRight + "]");
//        }
    }
}
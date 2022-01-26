package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.Sql;


import java.util.List;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView questionTextView;
    TextView numOfCurrentQuestion;
    TextView howManyQuestionsInTest;
    TextView pointsView;


    Button confirmButton;

    int indexOfQuestion = 0;
    RadioGroup answers;
    RadioButton answerA;
    RadioButton answerB;
    RadioButton answerC;
    RadioButton answerD;



    int points = 0;
    public static final int NUMBER_OF_TESTED_QUESTIONS = 10;

    List<Sql.Question> questions;
List<Sql.Question> test;
    Sql.QuestionList questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Sql.setContext(this);

        List<String> listOfMovements = (List<String>) getIntent().getSerializableExtra("passDataList");
        questionList = Sql.getQuestionList(this, Sql.Filter.formatFilters(listOfMovements));


        questionTextView = findViewById(R.id.questionId);
        answers = findViewById(R.id.radioGroupAnswers);
        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);
        confirmButton = findViewById(R.id.submitAnswerButton);
        howManyQuestionsInTest = findViewById(R.id.howManyQuestions);
        numOfCurrentQuestion = findViewById(R.id.numberOfQuestion);
        pointsView = findViewById(R.id.points);


        howManyQuestionsInTest.setText("" + numberOfQuestions());


        confirmButton.setOnClickListener(this);


        int goligichest = questionList.getPossibleQuestionsCount();

        questions = questionList.getNQuestions(goligichest);
        test = questionList.getNQuestions(numberOfQuestions());
        setQuestionAndAnswers(indexOfQuestion);
    }


    public void setQuestionAndAnswers(int i) {
        if (questions.isEmpty()){
            //TODO tady si to oprav. Když je to prázdný, tak
            Intent intent = new Intent(this, QuizSettings.class);
            startActivity(intent);
            Toast.makeText(this, "Nastala chyba, zvol více směrů", Toast.LENGTH_SHORT).show();
        }
        String questionText = questions.get(i).text;
        String answerA_text = questions.get(i).getA().text;
        String answerB_text = questions.get(i).getB().text;
        String answerC_text = questions.get(i).getC().text;
        String answerD_text = questions.get(i).getD().text;

        boolean answerA_isRight = questions.get(i).getA().isRight;
        boolean answerB_isRight = questions.get(i).getB().isRight;
        boolean answerC_isRight = questions.get(i).getC().isRight;
        boolean answerD_isRight = questions.get(i).getD().isRight;

        questionTextView.setText(questionText);

        answerA.setText(answerA_text);
        answerA.setTag(answerA_isRight);

        answerB.setText(answerB_text);
        answerB.setTag(answerB_isRight);

        answerC.setText(answerC_text);
        answerC.setTag(answerC_isRight);

        answerD.setText(answerD_text);
        answerD.setTag(answerD_isRight);


    }

    //
//    int i = 0;
//    public void cycleOfQuizz(int numOfQuestion) {
//
//        while (i < numOfQuestion) {
//            setQuestionAndAnswers(i);
//            if (answers.getCheckedRadioButtonId() != -1) {
//                if(checkAnswer()) {
//                    points += 10;
//                }
//            }
//        }
//
//
//    }
//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitAnswerButton:
                System.out.println(numberOfQuestions());
                System.out.println(indexOfQuestion);
                if(indexOfQuestion <= numberOfQuestions() - 2) {
                    if ((answers.getCheckedRadioButtonId() != -1)) {
                        if (checkAnswer()) {
                            points += 10;
                            Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(this, "Špatně", Toast.LENGTH_SHORT).show();
                        }
                        indexOfQuestion++;
                        setQuestionAndAnswers(indexOfQuestion);
                        answers.clearCheck();
                        numOfCurrentQuestion.setText("" + (indexOfQuestion + 1));
                        pointsView.setText(points + "b");
                    }


                }else{
                    if (checkAnswer()){
                        points += 10;
                        Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                    }
                    goToSummaryActivity();
                }

                break;
            }

        }



    boolean checkAnswer() {
        boolean isRight = (boolean) findViewById(answers.getCheckedRadioButtonId()).getTag();
        return isRight;
    }

    int numberOfQuestions(){
        return Math.min(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount());
    }

    void goToSummaryActivity() {
        Intent intent = new Intent(QuestionActivity.this, QuizSummary.class);
        intent.putExtra("percent", points);
        intent.putExtra("numberofquestions", numberOfQuestions());
        points = 0;
        indexOfQuestion = 0;
        startActivity(intent);
    }

    //TODO  Oprava bugů. Zkusit najít nějakej přechod mezi otázkami
}
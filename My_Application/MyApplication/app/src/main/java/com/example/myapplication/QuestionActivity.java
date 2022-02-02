package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.firebase.Sql;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView questionTextView;
    TextView numOfCurrentQuestion;
    TextView howManyQuestionsInTest;
    TextView pointsView;
    TextView confirmButton;

    RadioGroup answers;
    RadioButton answerA;
    RadioButton answerB;
    RadioButton answerC;
    RadioButton answerD;

    int indexOfQuestion = 0;
    int points = 0;
    public static final int NUMBER_OF_TESTED_QUESTIONS = 10;

    public static List<String> movementList = new ArrayList<>();
    public static List<Boolean> answerList = new ArrayList<>();

    public static List<String> mostCommonMistakes = new ArrayList<>();

    boolean setResults = false;

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

        setResults = (boolean) getIntent().getSerializableExtra("getResults");

        questionTextView = findViewById(R.id.questionId);
        answers = findViewById(R.id.radioGroupAnswers);
        answerA = findViewById(R.id.answerA);
        answerB = findViewById(R.id.answerB);
        answerC = findViewById(R.id.answerC);
        answerD = findViewById(R.id.answerD);
        confirmButton = findViewById(R.id.buttonNext);
        howManyQuestionsInTest = findViewById(R.id.howManyQuestions);
        numOfCurrentQuestion = findViewById(R.id.numberOfQuestion);
        pointsView = findViewById(R.id.points);
        movementList = new ArrayList<>();
        answerList = new ArrayList<>();
        mostCommonMistakes = new ArrayList<>();


        if(setResults){
            pointsView.setVisibility(View.VISIBLE);
        } else pointsView.setVisibility(View.INVISIBLE);


        howManyQuestionsInTest.setText("" + numberOfQuestions(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount()));

        confirmButton.setOnClickListener(this);


        int goligichest = questionList.getPossibleQuestionsCount();
        questions = questionList.getNQuestions(goligichest);
        test = questionList.getNQuestions(numberOfQuestions(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount()));
        setQuestionAndAnswers(indexOfQuestion);
    }


    public void setQuestionAndAnswers(int i) {
        if (questions.isEmpty()) {
            Intent intent = new Intent(this, QuizSettings.class);
            startActivity(intent);
            Toast.makeText(this, "Nastala chyba, zvol více směrů", Toast.LENGTH_SHORT).show();
            return;
        }
        String questionText = questions.get(i).text;
        String answerA_text = questions.get(i).getA().text;
        String answerB_text = questions.get(i).getB().text;
        String answerC_text = questions.get(i).getC().text;
        String answerD_text = questions.get(i).getD().text;
        String movement = questions.get(i).movement;

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

        movementList.add(movement);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:

                RadioButton rb = findViewById(answers.getCheckedRadioButtonId());
                needToLearnMovements();

                if (indexOfQuestion <= numberOfQuestions(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount()) - 2) {
                    if ((answers.getCheckedRadioButtonId() != -1)) {
                        if (checkAnswer()) {
                            points += 10;
                            setButtonRight(rb);
                            answerList.add(true);
                            // Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                        } else {
                            setButtonWrong(rb);
                            setButtonRight(findRadioRight(answers));

                            answerList.add(false);
                            //   Toast.makeText(this, "Špatně", Toast.LENGTH_SHORT).show();
                        }
                        if (setResults) {
                            confirmButton.setClickable(false);
                            answers.clearCheck();

                            final Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    pointsView.setText(points + "b");
                                    indexOfQuestion++;
                                    cleanButtons(answers);
                                    numOfCurrentQuestion.setText("" + (indexOfQuestion + 1));
                                    setQuestionAndAnswers(indexOfQuestion);
                                }
                            };
                            Handler h = new Handler();
                            h.postDelayed(r, 1500);
                            confirmButton.setClickable(true);
                        } else {
                            confirmButton.setClickable(false);
                            pointsView.setText(points + "b");
                            indexOfQuestion++;
                            answers.clearCheck();
                            cleanButtons(answers);
                            numOfCurrentQuestion.setText("" + (indexOfQuestion + 1));
                            setQuestionAndAnswers(indexOfQuestion);
                            confirmButton.setClickable(true);
                        }
                    }
                } else {
                    if (checkAnswer()) {
                        points += 10;
                        //Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                        setButtonRight(rb);
                        // Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                    } else {
                        setButtonWrong(rb);
                        setButtonRight(findRadioRight(answers));
                        //   Toast.makeText(this, "Špatně", Toast.LENGTH_SHORT).show();
                    }

                    if (setResults) {
                        confirmButton.setClickable(false);
                        final Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                pointsView.setText(points + "b");
                                indexOfQuestion++;
                                answers.clearCheck();
                                cleanButtons(answers);
                                numOfCurrentQuestion.setText("" + (indexOfQuestion + 1));
                                setQuestionAndAnswers(indexOfQuestion);
                            }
                        };
                        Handler h = new Handler();
                        h.postDelayed(r, 1500);
                        confirmButton.setClickable(true);
                    } else {
                        confirmButton.setClickable(false);
                        //pointsView.setText(points + "b");
                        //indexOfQuestion++;
                        answers.clearCheck();
                        cleanButtons(answers);
                        // numOfCurrentQuestion.setText("" + (indexOfQuestion + 1));
                        setQuestionAndAnswers(indexOfQuestion);
                        confirmButton.setClickable(true);
                    }
                    goToSummaryActivity();

                }
                break;
        }


    }


    /**
     * @return value stored in the <i>Tag</i> of the <i>RadioButton</i>
     */
    public boolean checkAnswer() {
        boolean isRight = (boolean) findViewById(answers.getCheckedRadioButtonId()).getTag();
        return isRight;
    }

    /**
     * @return the lower number of
     */
    public int numberOfQuestions(int a, int b) {
        return Math.min(a, b);
    }

    public void goToSummaryActivity() {
        Intent intent = new Intent(QuestionActivity.this, QuizSummary.class);
        intent.putExtra("percent", points);
        intent.putExtra("numberofquestions", numberOfQuestions(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount()));
        points = 0;
        indexOfQuestion = 0;
        startActivity(intent);
    }


    /**
     * creates list of movements with 3 or more mistakes
     */
    public void needToLearnMovements() {

       HashMap<String, Integer> mistakes = new HashMap<>();
        for (int i = 0; i < answerList.size(); i++) {
            if (!answerList.get(i)){
                if (mistakes.get(movementList.get(i)) == null){
                    mistakes.put(movementList.get(i), 1);
                } else {
                    mistakes.put(movementList.get(i), mistakes.get(movementList.get(i)) + 1);
                }
            }
        }

        if (!mistakes.isEmpty()) {
            for (Map.Entry<String, Integer> entry : mistakes.entrySet()) {
                if (entry.getValue() >= 3) {
                    if (!mostCommonMistakes.contains(entry.getKey())) {
                        mostCommonMistakes.add(entry.getKey());
                    }
                }
            }
        }

//        System.out.println(Arrays.asList(mistakes));
//        System.out.println("nejcastejsi chyby: " + Arrays.toString(mostCommonMistakes.toArray()));
    }

    public void setButtonWrong(RadioButton rb) {
        if (!setResults) {
            return;
        } else rb.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_background_wrong));
    }

    public void setButtonRight(RadioButton rb) {
        if (!setResults) {
            return;
        } else rb.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_background_right));
    }

    public RadioButton findRadioRight(RadioGroup radioGroup) {
        RadioButton rb = null;
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            boolean b = (boolean) radioGroup.getChildAt(i).getTag();
            if (b) {
                rb = (RadioButton) radioGroup.getChildAt(i);
                break;
            }
        }
        return rb;
    }

    public void cleanButtons(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setBackground(null);

        }
    }
}


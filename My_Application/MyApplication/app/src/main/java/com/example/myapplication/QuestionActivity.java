package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.firebase.Firestore;
import com.example.myapplication.firebase.Sql;
import com.google.common.collect.Iterables;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static List<AnsweredQuestion> answeredQuestions = new ArrayList<>();

    private TextView questionTextView;
    private TextView numOfCurrentQuestion;
    private TextView howManyQuestionsInTest;
    private TextView pointsView;
    private TextView confirmButton;

    private RadioGroup answers;
    private RadioButton answerA;
    private RadioButton answerB;
    private RadioButton answerC;
    private RadioButton answerD;

    private Sql.Question currentQuestion = null;

    int indexOfQuestion = 0;
    int points = 0;
    public static final int NUMBER_OF_TESTED_QUESTIONS = 10;

    public static List<String> movementList = new ArrayList<>();
    public static List<Boolean> answerList = new ArrayList<>();

    public static List<String> mostCommonMistakes = new ArrayList<>();

    boolean setResults = false;

    private List<Sql.Question> questions;
    private List<Sql.Question> test;
    private Sql.QuestionList questionList;

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
        answeredQuestions.add(new AnsweredQuestion(questions.get(i)));
        String questionText = questions.get(i).text;
        String answerA_text = questions.get(i).getA().text;
        String answerB_text = questions.get(i).getB().text;
        String answerC_text = questions.get(i).getC().text;
        String answerD_text = questions.get(i).getD().text;
        String movement = questions.get(i).movement;
        currentQuestion = questions.get(i);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNext:

                RadioButton rb = findViewById(answers.getCheckedRadioButtonId());
                needToLearnMovements();

                if (indexOfQuestion <= numberOfQuestions(NUMBER_OF_TESTED_QUESTIONS, questionList.getPossibleQuestionsCount()) - 2) {
                    if ((answers.getCheckedRadioButtonId() != -1)) {
                        if (checkAnswer()) {
                            AnsweredQuestion.setWrongForLast(-1);
                            Firestore.user.questEventHandler(currentQuestion);
                            points += 10;
                            setButtonRight(rb);
                            answerList.add(true);
//                            answers.indexOfChild(answers.findViewById(answers.getCheckedRadioButtonId()))
                            // Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                        } else {
                            AnsweredQuestion.setWrongForLast(answers.indexOfChild(answers.findViewById(answers.getCheckedRadioButtonId())));
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
                                    confirmButton.setClickable(true);
                                }
                            };
//                            confirmButton.setClickable(true);
                            Handler h = new Handler();
                            h.postDelayed(r, 1500);
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
                        AnsweredQuestion.setWrongForLast(-1);
                        Firestore.user.questEventHandler(currentQuestion);
                        points += 10;
                        //Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                        setButtonRight(rb);
                        // Toast.makeText(this, "Správně", Toast.LENGTH_SHORT).show();
                    } else {
                        AnsweredQuestion.setWrongForLast(answers.indexOfChild(answers.findViewById(answers.getCheckedRadioButtonId())));
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToSummaryActivity();
                        }
                    }, 1000);


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
        intent.putExtra("mistakes", mostCommonMistakes.toArray(new String[0]));
        intent.putExtra("answeredQuestions", (Serializable) answeredQuestions);
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

    static class AnsweredQuestion extends Sql.Question implements Serializable{
        private int indexOfWrong;

        AnsweredQuestion(Sql.Question question) {
            super(question);
        }

        private static void setWrongForLast(int indexOfWrong) {
            Iterables.getLast(answeredQuestions).indexOfWrong = indexOfWrong;
        }

        public String getQuestionText() {
            return text;
        }

        public String getWrongAnswer() {
            switch (indexOfWrong) {
                case 0:
                    return getA().text;
                case 1:
                    return getB().text;
                case 2:
                    return getC().text;
                case 3:
                    return getD().text;
                default:
                    return null;
            }
        }

        public String getRightAnswer() {
            if (getA().isRight) return getA().text;
            if (getB().isRight) return getB().text;
            if (getC().isRight) return getC().text;
            return getD().text;
        }

        @Override
        public String toString() {
            return "AnsweredQuestion{\n" +
                    "   " + getQuestionText() + "\n" +
                    "   " + getWrongAnswer() + "\n" +
                    "   " + getRightAnswer() + "}";
        }
    }
}


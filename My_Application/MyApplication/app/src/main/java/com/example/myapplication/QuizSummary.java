package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.RV_answers.AnswersAdapter;
import com.example.myapplication.firebase.Firestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizSummary extends AppCompatActivity implements View.OnClickListener {

    private TextView percentage;
    private TextView ratingText;
    private TextView mostMistakesText;
    private Button backButton;
    private List<QuestionActivity.AnsweredQuestion> answeredQuestions;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Firestore.updateUser();
        setContentView(R.layout.activity_quiz_summary);
        percentage = findViewById(R.id.percentage);
        ratingText = findViewById(R.id.ratingText);
        mostMistakesText = findViewById(R.id.mostMistakesText);
        backButton = findViewById(R.id.goBackToMainScreen);
        recyclerView = findViewById(R.id.answers_rv);

        backButton.setOnClickListener(this);

        int rating = (int) getIntent().getSerializableExtra("percent");
        int numOfQuestions = (int) getIntent().getSerializableExtra("numberofquestions");
        percentage.setText(""+rating(rating, numOfQuestions));
        String[] mistakes = getIntent().getStringArrayExtra("mistakes");
        answeredQuestions = (ArrayList<QuestionActivity.AnsweredQuestion>) getIntent().getSerializableExtra("answeredQuestions");
        System.out.println(answeredQuestions.toString());
        if (mistakes.length > 0) {
            String str = mistakes[0];
            for (int i = 1; i < mistakes.length; i++) {
                str += i < mistakes.length-1 ? ", " : " a " + mistakes[i];
            }
            mostMistakesText.setText("Měl by sis procvičit směr" + ((mistakes.length > 1) ? "y" : "") + " " + str + ".");//TODO fix, někdy píše null
        }
        setResultMessage(rating, ratingText, numOfQuestions);

        initData();
        setRecyclerView();

    }

    private void initData() {

    }

    private void setRecyclerView() {
        AnswersAdapter answersAdapter = new AnswersAdapter(answeredQuestions);
        recyclerView.setAdapter(answersAdapter);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * <p>Implemented method from View.OnClickListener. When BackButton is clicked, user is delivered to the home page.</p>
     * @param v given View
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    /**
     * <p><b>Function</b> used to calculate the percentage of right answered questions.</p>
     * @param rate <i>number of points gained during the test</i>
     * @param numOfQ <i>number of questions contained in test</i>
     * @return Rounded value of division of <b><i>parametres</i></b> multiplied by 10.
     */
    public int rating(int rate, int numOfQ) {
        float r = (float) rate;
        float n = (float) numOfQ;
        float f =  (r / n) * 10;
        return Math.round(f);
    }

    /**
     * <p><b>Method</b> setting text in the final message. The strings are stored in the <b><i>XML</i></b> file. (located in folder <i>values</i> in the <b><i>Resources</i></b> folder)</p>
     * @param rate <i>number of points gained during the test</i>
     * @param tv <i><b>TextView</b> which is being modified</i>
     * @param howmanyquestions <i>number of questions contained in test</i>
     */
    public void setResultMessage(int rate, TextView tv, int howmanyquestions) {
        int i = rate / 10;
        if (i > howmanyquestions * 0.7) {
            tv.setText(getString(R.string.goodResult));
        } else if(i <= howmanyquestions * 0.7 && i >= howmanyquestions * 0.5) {
            tv.setText(getString(R.string.averageResult));
        } else tv.setText(getString(R.string.badResult));
    }

    @Override
    public void onBackPressed() {

    }
}
package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.firebase.Firestore;

public class QuestActivity extends AppCompatActivity {

    private ImageButton goBigOrGoHome;
    private ImageButton goToProfile;

    private static TextView questText1;
    private static TextView xpQuest1;
    private static ProgressBar xpProgress1;

    private static TextView questText2;
    private static TextView xpQuest2;
    private static ProgressBar xpProgress2;

    private static TextView questText3;
    private static TextView xpQuest3;
    private static ProgressBar xpProgress3;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest1(String text, String xp, int value, int max) {
        System.out.println(value + " 1");
        if (questText1 != null) {
            questText1.setText(text);
            xpQuest1.setText(xp);
            xpProgress1.setMax(max);
            xpProgress1.setProgress(value, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest2(String text, String xp, int value, int max) {
        System.out.println(value + " 2");
        if (questText2 != null) {
            questText2.setText(text);
            xpQuest2.setText(xp);
            xpProgress2.setMax(max);
            xpProgress2.setProgress(value, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest3(String text, String xp, int value, int max) {
        System.out.println(value + " 3");
        if (questText3 != null) {
            questText3.setText(text);
            xpQuest3.setText(xp);
            xpProgress3.setMax(max);
            xpProgress3.setProgress(value, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuests(String text1, String xp1, int value1, int max1,
                                 String text2, String xp2, int value2, int max2,
                                 String text3, String xp3, int value3, int max3) {
        setQuest1(text1, xp1+" xp", value1, max1);
        setQuest2(text2, xp2+" xp", value2, max2);
        setQuest3(text3, xp3+" xp", value3, max3);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("bruh");
        setContentView(R.layout.activity_quest);
        goBigOrGoHome = findViewById(R.id.home_button_quest);
        goToProfile = findViewById(R.id.profile_button_quest);

        questText1 = findViewById(R.id.questText1);
        xpQuest1 = findViewById(R.id.xpQuest1);
        xpProgress1 = findViewById(R.id.xpProgress1);
        questText2 = findViewById(R.id.questText2);
        xpQuest2 = findViewById(R.id.xpQuest2);
        xpProgress2 = findViewById(R.id.xpProgress2);
        questText3 = findViewById(R.id.questText3);
        xpQuest3 = findViewById(R.id.xpQuest3);
        xpProgress3 = findViewById(R.id.xpProgress3);

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
        if (Firestore.user != null) {
            Firestore.Quest[] quests = Firestore.user.quests;
            int xp = Firestore.Quest.EXPERIENCE;
            int max = Firestore.Quest.MAX;
            setQuests(quests[0].getQuestType().text,
                    ""+xp,
                    (int)quests[0].getPercentage()*max,
                    max,
                    quests[1].getQuestType().text,
                    ""+xp,
                    (int)quests[1].getPercentage()*max,
                    max,
                    quests[2].getQuestType().text,
                    ""+xp,
                    (int)quests[2].getPercentage()*max,
                    max);
        }
    }
}
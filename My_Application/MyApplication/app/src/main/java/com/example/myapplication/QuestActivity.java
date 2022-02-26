package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.firebase.Firestore;

import java.util.Arrays;

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

    private static LinearLayout quest1;
    private static LinearLayout quest2;
    private static LinearLayout quest3;

    private static ImageView quest1_done;
    private static ImageView quest2_done;
    private static ImageView quest3_done;



    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest1(String text, String xp, int value, int max) {
        if (questText1 != null) {
            questText1.setText(text);
            xpQuest1.setText(xp);
            xpProgress1.setMax(max);
            xpProgress1.setProgress(value, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest2(String text, String xp, int value, int max) {
        if (questText2 != null) {
            questText2.setText(text);
            xpQuest2.setText(xp);
            xpProgress2.setMax(max);
            xpProgress2.setProgress(value, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setQuest3(String text, String xp, int value, int max) {
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
        quest1 = findViewById(R.id.quest1);
        quest2 = findViewById(R.id.quest2);
        quest3 = findViewById(R.id.quest3);
        quest1_done = findViewById(R.id.quest1_complete);
        quest2_done = findViewById(R.id.quest2_complete);
        quest3_done = findViewById(R.id.quest3_complete);


        setForeground();
        System.out.println(Firestore.user.quests[1].isComplete() + "            ########################");
        System.out.println(Firestore.user.quests[1].getPercentage() + "         @@@@@@@@@@@@@@@@@@@@@@@@");
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
                Intent intent = new Intent(QuestActivity.this, CheatSheet.class);
                startActivity(intent);
            }
        });
        if (Firestore.user != null) {
            Firestore.fillQuestInfo();
//            Firestore.Quest[] quests = Firestore.user.quests;
////            int xp = Firestore.Quest.EXPERIENCE;
//            int max = Firestore.Quest.MAX;
//
//            setQuests(quests[0].getQuestType().text,
//                    ""+quests[0].getEXPERIENCE(),
//                    (int)(quests[0].getPercentage()*max),
//                    max,
//                    quests[1].getQuestType().text,
//                    ""+quests[1].getEXPERIENCE(),
//                    (int)(quests[1].getPercentage()*max),
//                    max,
//                    quests[2].getQuestType().text,
//                    ""+quests[2].getEXPERIENCE(),
//                    (int)(quests[2].getPercentage()*max),
//                    max);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setForeground() {
        ImageView[] isDone = {quest1_done, quest2_done, quest3_done};
        Firestore.Quest[] quests = Firestore.user.quests;
        for(int i = 0; i < quests.length; i++) {
            if(quests[i].isComplete()) {
                isDone[i].setVisibility(View.VISIBLE);
            }


        }
    }
}
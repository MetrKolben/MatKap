package com.example.myapplication.firebase;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.QuestActivity;
import com.example.myapplication.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *<p>
 *     This class provides connection between the application and Firestore from <a href="https://console.firebase.google.com/">Firebase</a>
 *</p>
 */
public class Firestore {
    public static final int XP_PER_LEVEL = 50;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser firebaseUser = null;
    public static User user = null;
    private static String document_name = null;

    /**
     * Initializes current user for the whole app and downloads his stats
     * @param firebaseUser object of the user at Firebase
     * @param isNew should be true when the user is creating an account
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setFirebaseUser(FirebaseUser firebaseUser, boolean isNew) {
        Firestore.firebaseUser = firebaseUser;
        document_name = ""+(firebaseUser.getEmail().hashCode());
        if (isNew) {
            addFirebaseUser();
            return;
        }
        db.collection("users")
                .document(document_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> user = task.getResult().getData();
                        if (user == null) {
                            addFirebaseUser();
                            return;
                        }
                        int i = 0;
                        Quest[] quests = new Quest[]{new Quest(), new Quest(), new Quest()};
                        int lvl = 0;
                        int xp = 0;
                        int pic_id = 0;
                        QuestType[] types = getTodaysQuests();
                        boolean shouldBeRestarted = false;
//                        Arrays.fill(shouldBeRestarted, false);
                        for (Map.Entry<String, Object> entry : user.entrySet()) {
                            String row = entry.getKey();
                            switch(row) {
                                case "lvl":
                                    lvl = ((Long)entry.getValue()).intValue();
                                    break;
                                case "pic_id":
                                    pic_id = ((Long)entry.getValue()).intValue();
                                    break;
                                case "u1_type":
                                    shouldBeRestarted = shouldBeRestarted || !(QuestType.toQuestType((String)entry.getValue()) == types[0]);
                                    quests[0].setQuestionType(types[0]);
                                    break;
                                case "u1_stat":
                                    quests[0].setPercentage((Double)entry.getValue());
                                    break;
                                case "u2_type":
                                    shouldBeRestarted = shouldBeRestarted || !(QuestType.toQuestType((String)entry.getValue()) == types[1]);
                                    quests[1].setQuestionType(types[1]);
                                    break;
                                case "u2_stat":
                                    quests[1].setPercentage((Double)entry.getValue());
                                    break;
                                case "u3_type":
                                    shouldBeRestarted = shouldBeRestarted || !(QuestType.toQuestType((String)entry.getValue()) == types[2]);
                                    quests[2].setQuestionType(types[2]);
                                    break;
                                case "u3_stat":
                                    quests[2].setPercentage((Double)entry.getValue());
                                    break;
                                case "xp":
                                    xp = ((Long)entry.getValue()).intValue();
                                    break;
                            }
                        }
                        if (shouldBeRestarted) {
                            quests[0].setPercentage(0);
                            quests[1].setPercentage(0);
                            quests[2].setPercentage(0);
                        }
                        //Setting Profile info in ProfileActivity
                        Firestore.user = new User(quests, lvl, xp, pic_id);
                        updateUser();
//                        long l = Utils.TIMER.stop();
                        fillProfileInfo();
//                        ProfileActivity.fillInfo(
//                                Storage.getProfilePicturePath(Firestore.user.lvl),
//                                getEmail(),
//                                getName(),
//                                ""+Firestore.user.lvl,
//                                Firestore.user.xp,
//                                Firestore.user.lvl);
                        //Setting Quest info in QuestActivity

                        fillQuestInfo();
                        LoginActivity.firebaseLoaded();
//                        Quest[] quests1 = Firestore.user.quests;
//                        int XP = Firestore.Quest.EXPERIENCE;
//                        int max = Firestore.Quest.MAX;
//                        QuestActivity.setQuests(quests1[0].getQuestType().text,
//                                ""+XP,
//                                (int)quests1[0].getPercentage()*max,
//                                max,
//                                quests1[0].getQuestType().text,
//                                ""+XP,
//                                (int)quests1[0].getPercentage()*max,
//                                max,
//                                quests1[0].getQuestType().text,
//                                ""+XP,
//                                (int)quests1[0].getPercentage()*max,
//                                max);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fillProfileInfo() {
        ProfileActivity.fillInfo(
                Storage.getProfilePicturePath(Firestore.user.lvl),
                getEmail(),
                getName(),
                ""+Firestore.user.lvl,
                Firestore.user.xp,
                Firestore.user.lvl);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fillQuestInfo() {
        Quest[] quests = Firestore.user.quests;
        int XP = Firestore.Quest.EXPERIENCE;
        int max = Firestore.Quest.MAX;
        QuestActivity.setQuests(quests[0].getQuestType().text,
                ""+XP,
                (int)(quests[0].getPercentage()*max),
                max,
                quests[1].getQuestType().text,
                ""+XP,
                (int)(quests[1].getPercentage()*max),
                max,
                quests[2].getQuestType().text,
                ""+XP,
                (int)(quests[2].getPercentage()*max),
                max);
    }

    /**
     * @return Array of today's quests
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static QuestType[] getTodaysQuests() {
        int[] indexes = Utils.getNNumbersFromDate(QuestType.values().length, 3);
        QuestType[] res = new QuestType[3];
        QuestType[] types = QuestType.values();
        res[0] = types[indexes[0]];
        res[1] = types[indexes[1]];
        res[2] = types[indexes[2]];
        return res;
    }

    /**
     * Is called when creating new user in order to create and associate a new document in Firestore
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addFirebaseUser() {
        QuestType[] numbers = getTodaysQuests();
        Map<String, Object> user = new HashMap<>();
        user.put("u1_type", numbers[0]);
        user.put("u1_stat", 0.0);
        user.put("u2_type", numbers[1]);
        user.put("u2_stat", 0.0);
        user.put("u3_type", numbers[2]);
        user.put("u3_stat", 0.0);
        user.put("lvl", 1);
        user.put("xp", 0);
        user.put("pic_id", 1);

        db.collection("users")
                .document(document_name)
                .set(user);
        setFirebaseUser(firebaseUser, false);
    }

    /**
     * Uploads updated user data to Firestore
     */
    public static void updateUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("u1_type", Firestore.user.quests[0].questType);
        user.put("u1_stat", Firestore.user.quests[0].percentage);
        user.put("u2_type", Firestore.user.quests[1].questType);
        user.put("u2_stat", Firestore.user.quests[1].percentage);
        user.put("u3_type", Firestore.user.quests[2].questType);
        user.put("u3_stat", Firestore.user.quests[2].percentage);
        user.put("lvl", Firestore.user.lvl);
        user.put("xp", Firestore.user.xp);
        user.put("pic_id", Firestore.user.pic_id);

        db.collection("users")
                .document(document_name)
                .set(user);
    }

    public static void deleteUser() {
        if(document_name != null) {
            db.collection("users")
                    .document(document_name)
                    .delete();
        }
    }

    public static String getEmail() {
        return firebaseUser.getEmail();
    }

    public static String getName() {
        return firebaseUser.getDisplayName();
    }

    /**
     * Represents the data from the documents from Firestore database programmatically
     */
    public static class User{
        public Quest[] quests;

        private int lvl;
        private int xp;
        private int pic_id;

        private User(Quest[] quests, int lvl, int xp, int pic_id) {
            this.quests = quests;
            this.lvl = lvl;
            this.xp = xp;
            this.pic_id = pic_id;
        }

        public int getLvl() {
            return lvl;
        }

        private void setLvl(int lvl) {
            this.lvl = lvl;
        }

        public int getXp() {
            return xp;
        }

        private void setXp(int xp) {
            this.xp = xp;
        }

        private int getPic_id() {
            return pic_id;
        }

        private void setPic_id(int pic_id) {
            this.pic_id = pic_id;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void questEventHandler(Sql.Question question) {
            for (Quest q : quests) {
                if (q.questType.toString().equals(question.questionType.toString()) && !q.isComplete) {
                    q.stepForward();
                    fillQuestInfo();
                }
            }
        }

        @Override
        public String toString() {
            return ">>>User<<<\n" +
                    "User{\n" +
                    "   Quests{\n" +
                    "       quest1[type=" + quests[0].questType + "; stat=" + quests[0].percentage*100 + "%]\n" +
                    "       quest2[type=" + quests[1].questType + "; stat=" + quests[1].percentage*100 + "%]\n" +
                    "       quest3[type=" + quests[2].questType + "; stat=" + quests[2].percentage*100 + "%]\n" +
                    "   }\n" +
                    "   lvl=" + lvl + "\n" +
                    "   xp=" + xp + "\n" +
                    "   pic_id=" + pic_id + "\n" +
                    "}";
        }
    }
    public static class Quest {
        private QuestType questType;
        private double percentage;
        private boolean isComplete = false;
        public static final int EXPERIENCE = 25;
        public static final int MAX = 20;

        public double getPercentage() {
            return percentage;
        }

        private void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public QuestType getQuestType() {
            return questType;
        }

        private void setQuestionType(QuestType questType) {
            this.questType = questType;
        }

        private void stepForward() {
            if (isComplete) return;
            percentage = (percentage*20.0 + 1)/20.0;
            if (percentage == 1.0) isComplete = true;
        }

        @Override
        public String toString() {
            return "Quest{" +
                    "questType=" + questType +
                    ", percentage=" + percentage +
                    ", isComplete=" + isComplete +
                    '}';
        }
    }

    public enum QuestType {
        AUTHOR_BOOK("Přiřaď správně 20 knih k autorům"),
        BOOK_AUTHOR("Přiřaď správně 20 autorů ke knihám"),
        AUTHOR_MOVEMENT("Přiřaď správně 20 krát směr ke kterému se hlásí autor"),
        BOOK_MOVEMENT("Určete správně 20 krát směr, pod který spadá daná kniha"),
        BOOK_DRUH("Určete správně druh u 20 knih"),
        BOOK_GENRE("Žánrově zařaďte správně 20 knih"),
        MOVEMENT_CENTURY("Správně umísti 20 směrů na číselné ose"),
        MOVEMENT_SIGN("20 krát vyber hlavní znaky daného směru");

        public final String text;

        QuestType(String text) {
            this.text = text;
        }

        public static QuestType toQuestType(String str) {
            for (QuestType q : QuestType.values()) {
                if (str.equals(q.toString())) return q;
            }
            return null;
        }
    }

}

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

import java.util.HashMap;
import java.util.Map;

/**
 *<p>
 *     This class provides connection between the application and Firestore from <a href="https://console.firebase.google.com/">Firebase</a>
 *</p>
 */
public class Firestore {
    public static final int XP_PER_LVL_MULTIPLIER = 50;
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
                        QuestType[] types = getTodaysQuests();
                        boolean shouldBeRestarted = false;
                        int[] xps = getTodaysQuestRewards();
                        double[] maxes = getTodaysQuestMaxes();
                        for (Map.Entry<String, Object> entry : user.entrySet()) {
                            String row = entry.getKey();
                            switch(row) {
                                case "lvl":
                                    lvl = ((Long)entry.getValue()).intValue();
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
                        quests[0].setEXPERIENCE(xps[0]);
                        quests[0].requiredActionsCount = maxes[0];
                        quests[1].setEXPERIENCE(xps[1]);
                        quests[1].requiredActionsCount = maxes[1];
                        quests[2].setEXPERIENCE(xps[2]);
                        quests[2].requiredActionsCount = maxes[2];
                        for (Quest quest : quests) {
                            if (quest.percentage >= 1.0) {
                                quest.isComplete = true;
                            }
                        }

                        //Setting Profile info in ProfileActivity

                        Firestore.user = new User(quests, lvl, xp/*, pic_id*/);
                        updateUser();
                        fillProfileInfo();

                        //Setting Quest info in QuestActivity

                        fillQuestInfo();
                        LoginActivity.firebaseLoaded();

                        //Downloading profile pictures

                        Storage.downloadPics();
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
        double max[] = {user.quests[0].requiredActionsCount,
                user.quests[1].requiredActionsCount,
                user.quests[2].requiredActionsCount};
        QuestActivity.setQuests(quests[0].getQuestType().getText((int)max[0]),
                ""+quests[0].getEXPERIENCE(),
                (int)(quests[0].getPercentage()*max[0]),
                (int)max[0],
                quests[1].getQuestType().getText((int)max[1]),
                ""+quests[1].getEXPERIENCE(),
                (int)(quests[1].getPercentage()*max[1]),
                (int)max[1],
                quests[2].getQuestType().getText((int)max[2]),
                ""+quests[2].getEXPERIENCE(),
                (int)(quests[2].getPercentage()*max[2]),
                (int)max[2]);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int[] getTodaysQuestRewards() {
        int[] xps = Utils.getNNumbersFromDate(Quest.MAX_XP-Quest.MIN_XP+1, 3);
        for (int i = 0; i<xps.length; i++) {
            xps[i]+=Quest.MIN_XP;
        }
        return xps;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static double[] getTodaysQuestMaxes() {
        int[] maxes = Utils.getNNumbersFromDate(Quest.MAX_ACT-Quest.MIN_ACT+1, 3);
        double[] dMaxes = new double[3];
        for (int i = 0; i < maxes.length; i++) {
            dMaxes[i]=maxes[i]+Quest.MIN_ACT;
        }
        return dMaxes;
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

        private User(Quest[] quests, int lvl, int xp) {
            this.quests = quests;
            this.lvl = lvl;
            this.xp = xp;
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

        private void addXp(int xpToBeAdded) {
            int maxXp = lvl*XP_PER_LVL_MULTIPLIER;
            if (xpToBeAdded + xp < maxXp) {
                setXp(xp + xpToBeAdded);
                return;
            }
            setLvl(lvl+1);
            setXp(xp+xpToBeAdded-maxXp);
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
                    "}";
        }
    }
    public static class Quest {
        private QuestType questType;
        private double percentage;
        private boolean isComplete = false;

        private int EXPERIENCE = 0;
        public static final int MAX_XP = 30, MIN_XP = 15;
        public static final int MAX_ACT = 10, MIN_ACT = 5;
        private double requiredActionsCount = 10;/**10 is default, just to avoid unnecessary bugs*/

        public boolean isComplete() {
            return isComplete;
        }

        public int getEXPERIENCE() {
            return EXPERIENCE;
        }

        public void setEXPERIENCE(int EXPERIENCE) {
            this.EXPERIENCE = EXPERIENCE;
        }

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
            percentage = (percentage*requiredActionsCount + 1)/requiredActionsCount;
            if (percentage >= 1.0) isComplete = true;
            return;
        }

        public void collect() {
            if (!isComplete) return;
            percentage = 1.1;
            user.addXp(EXPERIENCE);
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
        AUTHOR_BOOK("Přiřaď správně <amount> knih k autorům"),
        BOOK_AUTHOR("Přiřaď správně <amount> autorů ke knihám"),
        AUTHOR_MOVEMENT("Přiřaď správně <amount> krát směr ke kterému se hlásí autor"),
        BOOK_MOVEMENT("Určete správně <amount> krát směr, pod který spadá daná kniha"),
        BOOK_DRUH("Určete správně druh u <amount> knih"),
        BOOK_GENRE("Žánrově zařaďte správně <amount> knih"),
        MOVEMENT_CENTURY("Správně umísti <amount> směrů na číselné ose"),
        MOVEMENT_SIGN("<amount> krát vyber hlavní znaky daného směru");

        private final String text;

        QuestType(String text) {
            this.text = text;
        }

        private String getText(int max) {
            return text.replaceAll("<amount>", ""+max);
        }

        public static QuestType toQuestType(String str) {
            for (QuestType q : QuestType.values()) {
                if (str.equals(q.toString())) return q;
            }
            return null;
        }
    }

}

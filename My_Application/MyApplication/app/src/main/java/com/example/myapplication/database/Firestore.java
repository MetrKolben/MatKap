package com.example.myapplication.database;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *<p>
 *     This class provides connection between the application and Firestore from <a href="https://console.firebase.google.com/">Firebase</a>
 *</p>
 */
public class Firestore {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser firebaseUser = null;
    private static User user = null;
    private static String document_name = null;

    /**
     * Initializes current user for the whole app and downloads his stats
     * @param firebaseUser object of the user at Firebase
     * @param isNew should be true when the user is creating an account
     */
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
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> user = task.getResult().getData();
                        int i = 0;
                        Quest[] quests = new Quest[3];
                        int lvl = 0;
                        int xp = 0;
                        int pic_id = 0;
                        int[] numbers = getNRandomNumbers(3, 21);
                        for (Map.Entry<String, Object> entry : user.entrySet()) {
                            String row = entry.getKey();
                            switch(row) {
                                case "lvl":
                                    lvl = (int)entry.getValue();
                                    break;
                                case "pic_id":
                                    pic_id = (int)entry.getValue();
                                    break;
                                case "u1_id":
                                    quests[0] = new Quest(numbers[0], 0);
                                    break;
                                case "u1_stat":
                                    quests[0].percentage = (int)entry.getValue();
                                    break;
                                case "u2_id":
                                    quests[1] = new Quest(numbers[1], 0);
                                    break;
                                case "u2_stat":
                                    quests[1].percentage = (int)entry.getValue();
                                    break;
                                case "u3_id":
                                    quests[2] = new Quest(numbers[2], 0);
                                    break;
                                case "u3_stat":
                                    quests[2].percentage = (int)entry.getValue();
                                    break;
                                case "xp":
                                    xp = (int)entry.getValue();
                                    break;
                            }
                        }
                        Firestore.user = new User(quests, lvl, xp, pic_id);
                    }
                });
    }

    /**
     * Is called when creating new user in order to create and associate a new document in Firestore
     */
    public static void addFirebaseUser() {
        int[] numbers = getNRandomNumbers(3, 21);
        Map<String, Object> user = new HashMap<>();
        user.put("u1_id", numbers[0]);
        user.put("u1_stat", "0");
        user.put("u2_id", numbers[1]);
        user.put("u2_stat", "0");
        user.put("u3_id", numbers[2]);
        user.put("u3_stat", "0");
        user.put("lvl", 1);
        user.put("xp", 0);
        user.put("pic_id", 1);

        db.collection("users")
                .document(document_name)
                .set(user);
    }

    /**
     * Uploads updated user data to Firestore
     */
    public static void updateUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("u1_id", Firestore.user.quests[0].id);
        user.put("u1_stat", Firestore.user.quests[0].percentage);
        user.put("u2_id", Firestore.user.quests[1].id);
        user.put("u2_stat", Firestore.user.quests[1].percentage);
        user.put("u3_id", Firestore.user.quests[2].id);
        user.put("u3_stat", Firestore.user.quests[2].percentage);
        user.put("lvl", Firestore.user.lvl);
        user.put("xp", Firestore.user.xp);
        user.put("pic_id", Firestore.user.pic_id);

        db.collection("users")
                .document(document_name)
                .set(user);
    }

    /**
     * Represents the data from the documents from Firestore database programmatically
     */
    private static class User{
        public Quest[] quests;

        private int lvl;
        private int xp;
        private int pic_id;

        public User(Quest[] quests, int lvl, int xp, int pic_id) {
            this.quests = quests;
            this.lvl = lvl;
            this.xp = xp;
            this.pic_id = pic_id;
        }

        public int getLvl() {
            return lvl;
        }

        public void setLvl(int lvl) {
            this.lvl = lvl;
        }

        public int getXp() {
            return xp;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public int getPic_id() {
            return pic_id;
        }

        public void setPic_id(int pic_id) {
            this.pic_id = pic_id;
        }
    }
    private static class Quest {
        public final int id;
        private double percentage;

        public Quest(int id, double percentage) {
            this.id = id;
            this.percentage = percentage;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }

    public static int[] getNRandomNumbers(int n, int bound) {
        Random r = new Random();
        int[] numbers = new int[n];
        Arrays.fill(numbers, Integer.MAX_VALUE);
        for (int i = 0; i < n; i++) {
            boolean lock = true;
            while (lock) {
                int number = r.nextInt(bound);
                if (!contains(numbers, number)) {
                    lock = false;
                    numbers[i] = number;
                }
            }
        }
        return numbers;
    }

    private static boolean contains(int[] numbers, int number) {
        for (int n : numbers) {
            if (n == number) return true;
        }
        return false;
    }
}

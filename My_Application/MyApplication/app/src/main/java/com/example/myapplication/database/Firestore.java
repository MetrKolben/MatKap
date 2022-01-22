package com.example.myapplication.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Firestore {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser firebaseUser = null;
    private static User user = null;
    private static String document_name = null;

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
//                            if (i<=5) {
//                                if (i%2==0) {
//                                    quests[i/2] = new Quest((int)entry.getValue(), 0);
//                                } else {
//                                    quests[(i-1)/2].setPercentage((double)entry.getValue());
//                                }
//                            } else {
//                                switch (i){
//                                    case 6:
//                                        lvl = (int)entry.getValue();
//                                        break;
//                                    case 7:
//                                        xp = (int)entry.getValue();
//                                        break;
//                                    case 8:
//                                        pic_id = (int)entry.getValue();
//                                        break;
//                                }
//                            }
//
//                            i++;
                        }
                        Firestore.user = new User(quests, lvl, xp, pic_id);
                    }
                });
//        int i = 0;
//        Quest[] quests = new Quest[3];
//        int lvl = 0;
//        int xp = 0;
//        int pic_id = 0;
//        for (Map.Entry<String, Object> entry : userMap.entrySet()) {
//            if (i<=5) {
//                if (i%2==0) {
//                    quests[i/2] = new Quest((int)entry.getValue(), 0);
//                } else {
//                    quests[(i-1)/2].setPercentage((double)entry.getValue());
//                }
//            } else {
//                switch (i){
//                    case 6:
//                        lvl = (int)entry.getValue();
//                        break;
//                    case 7:
//                        xp = (int)entry.getValue();
//                        break;
//                    case 8:
//                        pic_id = (int)entry.getValue();
//                        break;
//                }
//            }
//
//            i++;
//        }
//        Firestore.user = new User(quests, lvl, xp, pic_id);
//                .getResult()
//                .getData();
    }

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
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
// Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
    }

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

    private static int[] getNRandomNumbers(int n, int bound) {
        Random r = new Random();
        int[] numbers = new int[n];
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

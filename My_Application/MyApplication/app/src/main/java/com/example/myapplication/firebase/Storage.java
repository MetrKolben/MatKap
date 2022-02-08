package com.example.myapplication.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Storage {
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String filesDirectory = "/data/user/0/com.example.myapplication/files/";
    private static final String firebaseDirectory = "gs://matkap-19ed9.appspot.com/";

    public static String getProfilePicturePath(int lvl) {
        ProfilePicture current = ProfilePicture.ONE;
        for (ProfilePicture picture : ProfilePicture.values()) {
            if (picture.lvlValue <= lvl) {
                current = picture;

            } else return getImagePath(current);
        }
        return getImagePath(current);
    }

    private static String getImagePath(ProfilePicture picture) {
        StorageReference gsReference = storage.getReferenceFromUrl(firebaseDirectory + picture.fileName);
        File file = new File(filesDirectory + picture.fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        gsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        return file.getPath();
    }

    public enum ProfilePicture{
        ONE("1.png", 1),
        FIVE("5.png", 5),
        TEN("10.png", 10),
        TWENTY("20.png", 20),
        THIRTY("30.png", 30),
        FORTY("40.png", 40),
        FIFTY("50.png", 50),
        HUNDRED("100.png", 100);
        public final String fileName;
        public final int lvlValue;

        ProfilePicture(String fileName, int lvlValue) {
            this.fileName = fileName;
            this.lvlValue = lvlValue;
        }
    }
}

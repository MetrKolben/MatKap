package com.example.myapplication.firebase;

import android.os.Environment;
import android.widget.ImageView;

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

    public static String getImagePath(ProfilePicture picture) {
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
                System.out.println("Seš dobrej :)");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Dneska ne, promiň :(");
            }
        });
        return file.getPath();
    }

    public enum ProfilePicture{
        TEST("negrrr.png");
        public final String fileName;

        ProfilePicture(String fileName) {
            this.fileName = fileName;
        }
    }
}

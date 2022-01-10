package com.example.myapplication.database;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void main(AppCompatActivity activity) {
//        System.out.println(Environment.getRootDirectory().getAbsolutePath());
//        System.out.println(Environment.getExternalStorageDirectory().exists());
//        File direc = new File(Environment.getRootDirectory().getAbsolutePath() + File.separator + "MatKap");
//        System.out.println(direc.exists());

//        System.out.println(Environment.getRootDirectory().canRead());
        SQLiteDatabase database;
        database = activity.openOrCreateDatabase("database.db", activity.MODE_PRIVATE, null);
        File databaseFile = new File(database.getPath());
        InputStream input = activity.getResources().openRawResource(R.raw.database);
        try {
            copyDatabaseToStorage(input, databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        database = SQLiteDatabase.openOrCreateDatabase()
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        URL url = classLoader.getResource("database.db");
//        try {
//            File file = new File(url.toURI());
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

//        FileOutputStream fileOutputStream;
//        File file = new File();
//        System.out.println(Environment.getDataDirectory().toString());
//        File root = new File(Environment.getDataDirectory());


//        System.out.println(new File("C:/Users/Honza/Documents/GitHub/LEEN/My_Application/MyApplication/app/src/main/java/com/example/myapplication/database/database.db").exists() + "      /////////////////////////");


//        File file = new File("src/main/java/com/example/myapplication/database/database.db");
//        System.out.println(file.exists());
//        database = SQLiteDatabase.openOrCreateDatabase();
//        try {
//            DriverManager.getConnection("");
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copyDatabaseToStorage(InputStream input, File destination) throws IOException {

//        Environment.
        try (
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(destination))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = input.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }

//        assertThat(destination).exists();
//        assertThat(Files.readAllLines(original.toPath())
//                .equals(Files.readAllLines(destination.toPath())));
    }
}

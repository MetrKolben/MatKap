package com.example.myapplication.database;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Sql {
    private static SQLiteDatabase generalDatabase = null;
    private static AppCompatActivity context = null;

//    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void openOrCreateGeneralDatabase(AppCompatActivity con) throws IOException {
        context = con;

        generalDatabase = context.openOrCreateDatabase("generalDatabase.db", context.MODE_PRIVATE, null);
        File databaseFile = new File(generalDatabase.getPath());
        if (!isDatabaseCorrect()) {
            InputStream input = context.getResources().openRawResource(R.raw.database);
            copyDatabaseToStorage(input, databaseFile);
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void copyDatabaseToStorage(InputStream input, File destination) throws IOException {

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(destination))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = input.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }
    }

    private static boolean isDatabaseCorrect() throws IOException {
        if (generalDatabase == null) return false;
        File fileToCheck = new File(generalDatabase.getPath());
        if (!fileToCheck.exists()) return false;
        InputStream toBeCompared = new FileInputStream(fileToCheck);
        InputStream template = context.getResources().openRawResource(R.raw.database);
        try {
            while (true) {
                int fr = toBeCompared.read();
                int tr = template.read();

                if (fr != tr)
                    return false;

                if (fr == -1)
                    return true;
            }

        } finally {
            if (toBeCompared != null)
                toBeCompared.close();
            if (template != null)
                template.close();
        }
    }

    public static Set<String[]> getResultSet(AppCompatActivity con) {
        if (generalDatabase != null) {
            List<String[]> resultSet = new ArrayList<>();
            Cursor c = generalDatabase.rawQuery("SELECT * FROM author", null);
            if (c.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = c.getString(c.getColumnIndex("name"));
                    @SuppressLint("Range") String birth = c.getString(c.getColumnIndex("birth"));
                    resultSet.add(new String[] {name, birth});
                } while(c.moveToNext());
            }
            for (String[] row : resultSet) {
                System.out.println(Arrays.toString(row));
            }
//            System.out.println(resultSet);
        } else {
            try {
                openOrCreateGeneralDatabase(con);
                test(con);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;////////////////////////////////
    }

    public static void test(AppCompatActivity con) {
        if (generalDatabase != null) {
            List<String[]> resultSet = new ArrayList<>();
            Cursor c = generalDatabase.rawQuery("SELECT * FROM author", null);
            if (c.moveToFirst()) {
                do {

                    @SuppressLint("Range") String name = c.getString(c.getColumnIndex("name"));
                    @SuppressLint("Range") String birth = c.getString(c.getColumnIndex("birth"));
                    resultSet.add(new String[] {name, birth});
                } while(c.moveToNext());
            }
            for (String[] row : resultSet) {
                System.out.println(Arrays.toString(row));
            }
//            System.out.println(resultSet);
        } else {
            try {
                openOrCreateGeneralDatabase(con);
                test(con);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum Filter {
        MOVEMENT("movement"),
        CENTURY(""),
        COUNTRY("country");
//        GENRE("genre", column),
//        DRUH("druh", column);
        public final String table;

        Filter(String table) {
            this.table = table;
        }
    }

    public enum Country{
        CZECH("Česko");
        public final String dbValue;

        Country(String dbValue) {
            this.dbValue = dbValue;
        }
    }

    public enum Movement{
        ROMANTISMUS("Romantismus");
        public final String dbValue;

        Movement(String dbValue) {
            this.dbValue = dbValue;
        }
    }

    public class Question{

    }


}

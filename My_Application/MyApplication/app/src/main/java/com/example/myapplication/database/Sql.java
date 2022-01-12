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
import java.util.Collections;
import java.util.List;

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

    public static QuestionList getQuestionList(AppCompatActivity con /*, +filters*/) {
        if (generalDatabase != null) {
            Cursor author = generalDatabase.rawQuery("SELECT author.id AS author_id, author.name AS author_name, movement.name AS movement_name, author.birth AS birth, author.death AS author_death, country.name AS country_name, sex " +
                    "FROM author, movement, country " +
                    "WHERE author.movement_id = movement.id " +
                    "AND author.country_id = country.id"/* + filters*/, null);
            Cursor book = generalDatabase.rawQuery("SELECT book.id AS book_id, book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "WHERE book.author_id = author.id " +
                    "AND book.genre_id = genre.id " +
                    "AND book.druh_id = druh.id " +
                    "AND book.movement_id = movement.id"/* + filters*/, null);
            if (author.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = author.getString(author.getColumnIndex("name"));
                    @SuppressLint("Range") String birth = author.getString(author.getColumnIndex("birth"));
                } while(author.moveToNext());
            }
        } else {
            try {
                openOrCreateGeneralDatabase(con);
                getQuestionList(con);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;////////////////////////////////
    }



//    public static Set<String[]> getResultSet(AppCompatActivity con) {
//        if (generalDatabase != null) {
//            List<String[]> resultSet = new ArrayList<>();
//            Cursor c = generalDatabase.rawQuery("SELECT * FROM author", null);
//            if (c.moveToFirst()) {
//                do {
//                    @SuppressLint("Range") String name = c.getString(c.getColumnIndex("name"));
//                    @SuppressLint("Range") String birth = c.getString(c.getColumnIndex("birth"));
//                    resultSet.add(new String[] {name, birth});
//                } while(c.moveToNext());
//            }
//            for (String[] row : resultSet) {
//                System.out.println(Arrays.toString(row));
//            }
////            System.out.println(resultSet);
//        } else {
//            try {
//                openOrCreateGeneralDatabase(con);
//                getResultSet(con);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;////////////////////////////////
//    }

//    public static void test(AppCompatActivity con) {
//        if (generalDatabase != null) {
//            List<String[]> resultSet = new ArrayList<>();
//            Cursor c = generalDatabase.rawQuery("SELECT * FROM author", null);
//            if (c.moveToFirst()) {
//                do {
//
//                    @SuppressLint("Range") String name = c.getString(c.getColumnIndex("name"));
//                    @SuppressLint("Range") String birth = c.getString(c.getColumnIndex("birth"));
//                    resultSet.add(new String[] {name, birth});
//                } while(c.moveToNext());
//            }
//            for (String[] row : resultSet) {
//                System.out.println(Arrays.toString(row));
//            }
////            System.out.println(resultSet);
//        } else {
//            try {
//                openOrCreateGeneralDatabase(con);
//                test(con);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public enum Filter {
        MOVEMENT("movement"),
        CENTURY(""),
        COUNTRY("country");
        public final String table;

        Filter(String table) {
            this.table = table;
        }

        @Override
        public String toString() {
            return table;
        }
    }

    public enum Country{
        CZECH("Česko");
        public final String dbValue;

        Country(String dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public String toString() {
            return dbValue;
        }
    }

    public enum Movement{
        ROMANTISMUS("Romantismus");
        public final String dbValue;

        Movement(String dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public String toString() {
            return dbValue;
        }
    }

    public static class Question{
        public final String text;
        private final Answer[] answers;
        public final QuestionType questionType;

        private Question(QuestionType questionType, String text, Answer... answers) {
            this.questionType = questionType;
            this.answers = answers;
            this.text = text;
        }

        @SuppressLint("Range")
        public static Question createQuestion(Cursor cursor, QuestionType questionType, int row) {
            switch(questionType) {
                //TODO Celý předělat, lol
                case AUTHOR_BOOK:
                    return createABQuestion(cursor, cursor.getString(cursor.getColumnIndex("book")), cursor.getString(cursor.getColumnIndex("author")));
                case BOOK_AUTHOR:
                    return createBAQuestion(cursor, cursor.getString(cursor.getColumnIndex("author")), cursor.getString(cursor.getColumnIndex("book")));
                case BOOK_GENRE:
                    return createBGQuestion(cursor, cursor.getString(cursor.getColumnIndex("genre")), cursor.getString(cursor.getColumnIndex("book")));
                case BOOK_DRUH:
                    return new Question(QuestionType.BOOK_DRUH, QuestionType.completeBDText(cursor.getString(cursor.getColumnIndex("book"))), new Answer("Lyricko-epický", cursor.getString(cursor.getColumnIndex("druh")).equals("Lyricko-epický")),
                                                                new Answer("Lyrika", cursor.getString(cursor.getColumnIndex("druh")).equals("Lyrika")),
                                                                new Answer("Epika", cursor.getString(cursor.getColumnIndex("druh")).equals("Epika")));
            }
            return null;
        }

        private static Question createABQuestion(Cursor cursor, String firstAnswer, String toBeAssigned) {
            return null;
        }

        private static Question createBAQuestion(Cursor cursor, String firstAnswer, String toBeAssigned) {
            return null;
        }

        private static Question createBGQuestion(Cursor cursor, String firstAnswer, String toBeAssigned) {
            return null;
        }

        public Answer getA() {
            return answers[0];
        }

        public Answer getB() {
            return answers[1];
        }

        public Answer getC() {
            return answers[2];
        }

        public Answer getD() {
            return answers[3];
        }
    }

    public static class QuestionList{
        private List<Question> questionList = new ArrayList<>();
        public int getPossibleQuestionsCount() {
            return questionList.size();
        }

        public List<Question> getNQuestions(int n) {
            List<Question> copy = new ArrayList<>();
            Collections.copy(copy, questionList);
            Collections.shuffle(copy);
            if (n <= questionList.size() && n > 0) {
                return copy;
            }
            return copy.subList(0, n);
        }
    }

    public static class Answer{
        final String text;
        final boolean isRight;

        public Answer(String text, boolean isRight) {
            this.text = text;
            this.isRight = isRight;
        }
    }

    public enum QuestionType {
        AUTHOR_BOOK("Kterou knihu napsal<sex> <author>?", "author", "book"),
        BOOK_AUTHOR("Který autor napsal \"<book>?\"", "book", "author"),
//        AUTHOR_MOVEMENT,
//        BOOK_MOVEMENT,
        BOOK_DRUH("Ke kterému druhu se řadí \"<book>\"", "book", "druh"),
        BOOK_GENRE("Do jakého žánru se řadí \"<book>\"", "book", "genre");
        private final String questionText, questionColumn, answerColumn;

        QuestionType(String questionText, String questionColumn, String answerColumn) {
            this.questionText = questionText;
            this.questionColumn = questionColumn;
            this.answerColumn = answerColumn;
        }

        public static String completeABText(String sex, String author) {
            return AUTHOR_BOOK.questionText.replaceAll("<sex>", sex).replaceAll("<author>", author);
        }

        public static String completeBAText(String book) {
            return BOOK_AUTHOR.questionText.replaceAll("<book>", book);
        }

        public static String completeBDText(String book) {
            return BOOK_DRUH.questionText.replaceAll("<book>", book);
        }

        public static String completeBGText(String book) {
            return BOOK_GENRE.questionText.replaceAll("<book>", book);
        }
    }
}

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
import java.util.Random;

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
        QuestionList questionList = new QuestionList();
        if (generalDatabase != null) {

            if(true /*if not containing movement filter*/){
                Cursor author = generalDatabase.rawQuery("SELECT author.id AS author_id, author.name AS author_name, movement.name AS movement_name, author.birth AS birth, author.death AS author_death, country.name AS country_name, sex " +
                        "FROM author, movement, country " +
                        "WHERE author.movement_id = movement.id " +
                        "AND author.country_id = country.id"/* + filters*/, null);
                System.out.println(author.getCount());
                if (author.moveToFirst()) {
                    do {
//                        questionList.add(Question.createQuestion(author, QuestionType.AUTHOR_MOVEMENT));
                        Question aMQuestion = Question.createQuestion(author, QuestionType.AUTHOR_MOVEMENT);

                        if (aMQuestion != null) {
                            questionList.add(aMQuestion);
                        }
//                    questionList.add(Question.createQuestion(author, questionType));
//                    questionList.add(Question.createQuestion(author, questionType));
//                    for (QuestionType questionType : QuestionType.values()) {
//                        questionList.add(Question.createQuestion(author, questionType));
//                    }
                    } while (author.moveToNext());
                }
                author.close();
            }


            Cursor book = generalDatabase.rawQuery("SELECT book.id AS book_id, book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "FROM book, author, genre, druh, movement " +
                    "WHERE book.author_id = author.id " +
                    "AND book.genre_id = genre.id " +
                    "AND book.druh_id = druh.id " +
                    "AND book.movement_id = movement.id"/* + filters*/, null);
            if (book.moveToFirst()) {
                do {
                    Question bDQuestion = Question.createQuestion(book, QuestionType.BOOK_DRUH);
                    Question bAQuestion = Question.createQuestion(book, QuestionType.BOOK_AUTHOR);
                    Question bGQuestion = Question.createQuestion(book, QuestionType.BOOK_GENRE);
                    Question bMQuestion = Question.createQuestion(book, QuestionType.BOOK_MOVEMENT);
                    Question aBQuestion = Question.createQuestion(book, QuestionType.AUTHOR_BOOK);

                    if (aBQuestion != null) {
                        questionList.add(aBQuestion);
                    }
                    if (bDQuestion != null){
                        questionList.add(bDQuestion);
                    }
                    if (bAQuestion != null){
                        questionList.add(bAQuestion);
                    }
                    if (bGQuestion != null){
                        questionList.add(bGQuestion);
                    }
                    if (bMQuestion != null){
                        questionList.add(bMQuestion);
                    }

//                    questionList.add();
//                    questionList.add(Question.createQuestion(book, QuestionType.BOOK_AUTHOR));
//                    questionList.add(Question.createQuestion(book, QuestionType.AUTHOR_BOOK));
//                    questionList.add(Question.createQuestion(book, QuestionType.BOOK_GENRE));
//                    questionList.add(Question.createQuestion(book, QuestionType.BOOK_MOVEMENT));
                } while(book.moveToNext());
            }
            book.close();

        } else {
            try {
                openOrCreateGeneralDatabase(con);
                getQuestionList(con);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return questionList;
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
        private static Random random= new Random();
        public final String text;
        private final Answer[] answers;
        public final QuestionType questionType;

        private Question(QuestionType questionType, String text, Answer... answers) {
            this.questionType = questionType;
            this.answers = answers;
            this.text = text;
        }

        @SuppressLint("Range")
        public static Question createQuestion(Cursor cursor, QuestionType questionType) {
            int position = cursor.getPosition();
            switch(questionType) {
                //TODO Celý předělat, lol
//                case BOOK_MOVEMENT:
//                    return createBMQuestion()
//                case AUTHOR_MOVEMENT:
//                    return createAMQuestion();
//                case AUTHOR_BOOK:
//                    return createABQuestion(cursor, cursor.getString(cursor.getColumnIndex("book")), cursor.getString(cursor.getColumnIndex("author")));
//                case BOOK_AUTHOR:
//                    return createBAQuestion(cursor, cursor.getString(cursor.getColumnIndex("author")), cursor.getString(cursor.getColumnIndex("book")));
//                case BOOK_GENRE:
//                    return createBGQuestion(cursor, cursor.getString(cursor.getColumnIndex("genre")), cursor.getString(cursor.getColumnIndex("book")));
                case BOOK_DRUH:
                    return new Question(QuestionType.BOOK_DRUH, QuestionType.completeBDText(cursor.getString(cursor.getColumnIndex("book"))), new Answer("Lyricko-epický", cursor.getString(cursor.getColumnIndex("druh")).equals("Lyricko-epický")),
                                                                new Answer("Lyrika", cursor.getString(cursor.getColumnIndex("druh")).equals("Lyrika")),
                                                                new Answer("Epika", cursor.getString(cursor.getColumnIndex("druh")).equals("Epika")));
                default:
                    String qCol = questionType.questionColumn;
                    String aCol = questionType.answerColumn;
                    String questionText = "";
                    switch(questionType) {
                        case BOOK_MOVEMENT:
                            questionText = QuestionType.completeBMText(cursor.getString(cursor.getColumnIndex(qCol)));
                        case AUTHOR_MOVEMENT:
                            questionText = QuestionType.completeAMText(cursor.getString(cursor.getColumnIndex(qCol)));
                        case AUTHOR_BOOK:
                            questionText = QuestionType.completeABText(cursor.getString(cursor.getColumnIndex(qCol)), (cursor.getString(cursor.getColumnIndex("sex")).equals("male") ? "" : "a"));
                        case BOOK_AUTHOR:
                            questionText = QuestionType.completeBAText(cursor.getString(cursor.getColumnIndex(qCol)));
                        case BOOK_GENRE:
                            questionText = QuestionType.completeBGText(cursor.getString(cursor.getColumnIndex(qCol)));
                    }
                    List<Answer> answers = new ArrayList<>();
                    System.out.println(questionType);
                    answers.add(new Answer(cursor.getString(cursor.getColumnIndex(aCol)), true));
                    for (int i = 0; i < 3; i++) {
                        boolean lock = true;
                        int checkIfPossible = 0;
                        while (lock) {
                            cursor.move(random.nextInt(cursor.getCount()));
                            if (!containsAnswer(answers, cursor.getString(cursor.getColumnIndex(aCol)))) {
                                answers.add(new Answer(cursor.getString(cursor.getColumnIndex(aCol)), false));
                                lock = false;
                            }
                            if (checkIfPossible > cursor.getCount()) return null;
                            checkIfPossible++;
                        }
                    }
                    Collections.shuffle(answers);
                    cursor.move(position);
                    return new Question(questionType, questionText, (Answer[])answers.toArray());
                    //TODO answer list, shuffle
            }
        }

        private static boolean containsAnswer(List<Answer> answers, String answer) {
            for (Answer answer1: answers) {
                if (answer1.text.equals(answer)) return true;
            }
            return false;
        }

        private static Question createABQuestion(Cursor cursor, int position) {
            return null;
        }

        private static Question createBAQuestion(Cursor cursor, int position) {
            return null;
        }

        private static Question createBGQuestion(Cursor cursor, int position) {
            return null;
        }

        private static Question createAMQuestion(Cursor cursor, int position) {
            return null;
        }

        private static Question createBMQuestion(Cursor cursor, int position) {
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
        private List<Question> questionList;
        public int getPossibleQuestionsCount() {
            return questionList.size();
        }

        public void add(Question question) {
            questionList.add(question);
        }

        public QuestionList() {
            this.questionList = new ArrayList<>();
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
        AUTHOR_BOOK("Kterou knihu napsal<sex> <author>?", "author_name", "book_name"),
        BOOK_AUTHOR("Který autor napsal \"<book>?\"?", "book_name", "author_name"),
        AUTHOR_MOVEMENT("Ke kterému směru se hlásí \"<author>?\"?", "author_name", "movement_name"),
        BOOK_MOVEMENT("Z jakého směru je \"<book>?\"?", "book_name", "movement_name"),
        BOOK_DRUH("Ke kterému druhu se řadí \"<book>\"?", "book_name", "druh_name"),
        BOOK_GENRE("Do jakého žánru se řadí \"<book>\"?", "book_name", "genre_name");
        private final String questionText, questionColumn, answerColumn;

        QuestionType(String questionText, String questionColumn, String answerColumn) {
            this.questionText = questionText;
            this.questionColumn = questionColumn;
            this.answerColumn = answerColumn;
        }

        public static String completeAMText(String author) {
            return AUTHOR_BOOK.questionText.replaceAll("<author>", author);
        }
        public static String completeBMText(String book) {
            return AUTHOR_BOOK.questionText.replaceAll("<book>", book);
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

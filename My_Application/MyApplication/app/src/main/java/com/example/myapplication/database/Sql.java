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
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <p>
 *     Connects the application with an SQLite type database that was created together with installing the application
 * </p>
 * <p>
 *     Contains methods that generate questions based on filters that the user chooses before starting the quiz
 * </p>
 * <p>
 *     Creates following own subclasses:
 *     <ul>
 *         <li>{@link com.example.myapplication.database.Sql.Answer}</li>
 *         <li>{@link FilterType}</li>
 *         <li>{@link com.example.myapplication.database.Sql.Question}</li>
 *         <li>{@link com.example.myapplication.database.Sql.QuestionList}</li>
 *         <li>{@link com.example.myapplication.database.Sql.QuestionType}</li>
 *     </ul>
 * </p>
 */
public class Sql {
    private static SQLiteDatabase generalDatabase = null;
    private static AppCompatActivity context = null;

    /**
     * Makes sure there is a valid copy of database to work with
     * @param con is there because {@link #openOrCreateGeneralDatabase(AppCompatActivity)} requires context
     * @throws IOException if there is a problem with reading database or copying it to users storage
     */
    private static void openOrCreateGeneralDatabase(AppCompatActivity con) throws IOException {
        context = con;

        generalDatabase = context.openOrCreateDatabase("generalDatabase.db", context.MODE_PRIVATE, null);
        File databaseFile = new File(generalDatabase.getPath());
        if (!isDatabaseCorrect()) {
            InputStream input = context.getResources().openRawResource(R.raw.database);
            copyDatabaseToStorage(input, databaseFile);
        }
    }

    /**
     * Copies the SQLite database to the device in order to make it possible to use SQL statements
     * @param input InputStream object that reads data from database located in <b>\res\raw\database.db</b>
     * @param destination File created in users storage
     * @throws IOException if there is a problem with reading database or copying it to users storage
     */
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

    /**
     * Used to either update the database or repaired a damaged copy in the device storage
     * @return returns true if both copies of the database contain the same data
     * @throws IOException if any of the two copies is not allowed to be read
     */
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

    /**
     * <p>
     *     First creates the author table which contains all the necessary information to create
     * <ul>
     *     <li>Author-Movement question</li>
     * </ul>
     *     Then creates the book table in order to create
     * <ul>
     *     <li>Author-Book question</li>
     *     <li>Book-Author question</li>
     *     <li>Book-Movement question</li>
     *     <li>Book-Druh question</li>
     *     <li>Book-Genre question</li>
     * </ul>
     * </p>
     * <p>
     *     Then it iterates through both tables and creates either 5 questions for each row when iterating through <b>book</b> or a single question when iterating through <b>author</b>
     * </p>
     * <p>
     *     Also recursively calls itself when <b>generalDatabase</b> is <b>null</b> to initialize the database first
     * </p>
     * @param con used to be passed over to <b>{@link #openOrCreateGeneralDatabase(AppCompatActivity)} </b>
     * @return a <b>{@link com.example.myapplication.database.Sql.QuestionList}</b> object
     */
    @SuppressLint("Range")
    public static QuestionList getQuestionList(AppCompatActivity con, String[] filters) {
        QuestionList questionList = new QuestionList();
        if (generalDatabase != null) {


            Cursor author = generalDatabase.rawQuery("SELECT author.id AS author_id, author.name AS author_name, movement.name AS movement_name, author.birth AS birth, author.death AS author_death, country.name AS country_name, sex " +
                        "FROM author, movement, country " +
                        "WHERE author.movement_id = movement.id " +
                        "AND author.country_id = country.id"+ filters[0], null);
            Cursor book = generalDatabase.rawQuery("SELECT book.id AS book_id, book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "FROM book, author, genre, druh, movement " +
                    "WHERE book.author_id = author.id " +
                    "AND book.genre_id = genre.id " +
                    "AND book.druh_id = druh.id " +
                    "AND book.movement_id = movement.id" + filters[1], null);

            author.moveToFirst();
            book.moveToFirst();

            Question.setAuthorAndBook(author, book);


            if(true /*if not containing movement filter*/){
                if (author.moveToFirst()) {
                    //FIXME
                    do {
                        Question aMQuestion = Question.createQuestion(author, QuestionType.AUTHOR_MOVEMENT);

                        if (aMQuestion != null) {
                            questionList.add(aMQuestion);
                        }
                        } while (author.moveToNext());
                    }
                }

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
                } while(book.moveToNext());
            }
            author.close();
            book.close();

        } else {
            try {
                openOrCreateGeneralDatabase(con);
                return getQuestionList(con, filters);
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
//            }
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
//            }
//        } else {
//            try {
//                openOrCreateGeneralDatabase(con);
//                test(con);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * <p>
     *     Used to simplify distinguishing between filters.
     *
     * </p>
     * <p>
     *     Century refers to a column in the <b>author</b> table and has to be calculated later to work
     * </p>
     */
    public enum FilterType {
        MOVEMENT("movement"),
        CENTURY(""),
        COUNTRY("country_name");
        public final String table;
        public final List<String> items;

        FilterType(String table) {
            this.table = table;
            this.items = fillItems(table);
        }


        //TODO javadoc
        @SuppressLint("Range")
        public static List<String> fillItems(String column) {
            List<String> items = new ArrayList<>();
            if (generalDatabase == null) {
                try {
                    openOrCreateGeneralDatabase(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Cursor cursor = generalDatabase.rawQuery(column.equals("movement") ? "SELECT name FROM movement" : "SELECT name FROM country", null);
            if (cursor.moveToFirst()) {
                do {
                    String item = cursor.getString(cursor.getColumnIndex("name"));
                    if (!items.contains(item)) {
                        items.add(item);
                    }
                } while(cursor.moveToNext());
            }
            cursor.close();
            return items;
        }

        //TODO filter formatting

        /**
         * @return the name of the table it refers to
         */
        @Override
        public String toString() {
            return table;
        }

    }

    public static class Filter{
        private static final String beginning = " AND(", separator = " OR ", end = ")";
        public String[] formatFilters(Object countries, Object movements, Object centuries) {
            String[] filters = new String[2];

            return filters;
        }
    }

    public static class Question{
        private static Random random= new Random();
        public final String text;
        private final Answer[] answers;
        public final QuestionType questionType;
        private static Cursor book, author;

        private Question(QuestionType questionType, String text, Answer... answers) {
            this.questionType = questionType;
            this.answers = answers;
            this.text = text;
        }

        /**
         * Sets the following variables (used only for Book-Author question)
         */
        public static void setAuthorAndBook(Cursor author, Cursor book) {
            Question.author = author;
            Question.book = book;
        }

        /**
         * @param cursor Either the <b>author</b> or the <b>book</b> table
         * @param questionType Determines the type of the question which then alters the question text and in the case of Book-Druh question changes the amount of answers
         * @return <b>{@link com.example.myapplication.database.Sql.Question}</b> object of a determined <b>{@link com.example.myapplication.database.Sql.QuestionType}</b>
         */
        @SuppressLint("Range")
        public static Question createQuestion(Cursor cursor, QuestionType questionType) {
            int position = cursor.getPosition();
            switch(questionType) {
                case BOOK_DRUH:
                    return new Question(QuestionType.BOOK_DRUH, QuestionType.completeBDText(cursor.getString(cursor.getColumnIndex(questionType.questionColumn))), new Answer("Lyricko-epický", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Lyricko-epický")),
                                                                new Answer("Lyrika", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Lyrika")),
                                                                new Answer("Epika", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Epika")));
                default:
                    String qCol = questionType.questionColumn;
                    String aCol = questionType.answerColumn;
                    String questionText = "";
                    switch(questionType) {
                        case BOOK_MOVEMENT:
                            questionText = QuestionType.completeBMText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case AUTHOR_MOVEMENT:
                            questionText = QuestionType.completeAMText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case AUTHOR_BOOK:
                            int save = author.getPosition();
                            author.move(book.getPosition()-1);
                            questionText = QuestionType.completeABText((author.getString(author.getColumnIndex("sex")).equals("male") ? "" : "a"),
                                    cursor.getString(cursor.getColumnIndex(qCol)));
                            author.move(save);
                            break;
                        case BOOK_AUTHOR:
                            questionText = QuestionType.completeBAText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case BOOK_GENRE:
                            questionText = QuestionType.completeBGText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                    }
                    List<Answer> answers = new ArrayList<>();
                    answers.add(new Answer(cursor.getString(cursor.getColumnIndex(aCol)), true));
                    //TODO improve the answer generating algorithm
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
            }
        }

        /**
         * <p>Used in {@link #createQuestion(Cursor, QuestionType)} in order to generate random wrong answers and checking if each answer is unique</p>
         * @param answers list of current answers to the <b>Question</b> object that is being generated
         * @param answer a potential answer that is yet to be checked if it is unique
         * @return false if the answer is unique
         */
        private static boolean containsAnswer(List<Answer> answers, String answer) {
            for (Answer answer1: answers) {
                if (answer1.text.equals(answer)) return true;
            }
            return false;
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

    /**
     * Represents a list of questions that is not directly accessible
     */
    public static class QuestionList{
        //FIXME should be private
        public List<Question> questionList;
        public int getPossibleQuestionsCount() {
            return questionList.size();
        }

        public void add(Question question) {
            questionList.add(question);
        }

        public QuestionList() {
            this.questionList = new ArrayList<>();
        }

        /**
         * Used to prevent any changes done to the original list
         * @param n the quantity of the questions that should be returned
         * @return n long list of <b>Question</b> objects
         */
        public List<Question> getNQuestions(int n) {
            List<Question> copy = new ArrayList<>(questionList);
            Collections.shuffle(copy);
            if ((n <= questionList.size() && n > 0) || n >= questionList.size()) {
                return copy;
            }
            return copy.subList(0, n);
        }
    }

    /**
     * A simple object containing the <b>text</b> and <b>truthfulness</b> of the answer
     */
    public static class Answer{
        final String text;
        final boolean isRight;

        public Answer(String text, boolean isRight) {
            this.text = text;
            this.isRight = isRight;
        }
    }

    /**
     * <p>
     *     Represents question types each containing the names of the columns where the question detail and answer should be searched.
     *     Also contains the question <b>template<b/>.
     * </p>
     */
    public enum QuestionType {
        AUTHOR_BOOK("Kterou knihu napsal<sex> <author>?", "author_name", "book_name"),
        BOOK_AUTHOR("Který autor napsal \"<book>\"?", "book_name", "author_name"),
        AUTHOR_MOVEMENT("Ke kterému směru se hlásí <author>?", "author_name", "movement_name"),
        BOOK_MOVEMENT("Z jakého směru je \"<book>\"?", "book_name", "movement_name"),
        BOOK_DRUH("Ke kterému druhu se řadí \"<book>\"?", "book_name", "druh_name"),
        BOOK_GENRE("Do jakého žánru se řadí \"<book>\"?", "book_name", "genre_name");
        private final String questionText, questionColumn, answerColumn;

        QuestionType(String questionText, String questionColumn, String answerColumn) {
            this.questionText = questionText;
            this.questionColumn = questionColumn;
            this.answerColumn = answerColumn;
        }

        public static String completeAMText(String author) {
            return AUTHOR_MOVEMENT.questionText.replaceAll("<author>", author);
        }
        public static String completeBMText(String book) {
            return BOOK_MOVEMENT.questionText.replaceAll("<book>", book);
        }

        public static String completeABText(String sex, String author) {
            return AUTHOR_BOOK.questionText.replace("<author>", author).replace("<sex>", sex);
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

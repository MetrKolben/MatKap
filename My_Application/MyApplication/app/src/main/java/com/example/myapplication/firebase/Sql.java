package com.example.myapplication.firebase;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 *         <li>{@link com.example.myapplication.firebase.Sql.Answer}</li>
 *         <li>{@link FilterType}</li>
 *         <li>{@link com.example.myapplication.firebase.Sql.Question}</li>
 *         <li>{@link com.example.myapplication.firebase.Sql.QuestionList}</li>
 *         <li>{@link com.example.myapplication.firebase.Sql.QuestionType}</li>
 *     </ul>
 * </p>
 */
public class Sql{
    private static SQLiteDatabase generalDatabase = null;

    private static AppCompatActivity context = null;

    public static void setContext(AppCompatActivity context) {
        Sql.context = context;
    }

    /**
     * Makes sure there is a valid copy of database to work with
     * @param con is there because {@link #openOrCreateGeneralDatabase(AppCompatActivity)} requires context
     * @throws IOException if there is a problem with reading database or copying it to users storage
     */
    private static void openOrCreateGeneralDatabase(AppCompatActivity con) throws IOException {
        context = con;

        generalDatabase = context.openOrCreateDatabase("generalDatabase.db"
                , context.MODE_PRIVATE
                , null);
        File databaseFile = new File(generalDatabase.getPath());
        if (!isDatabaseCorrect()) {
            InputStream input = context.getResources().openRawResource(R.raw.database);
            copyDatabaseToStorage(input, databaseFile);
        }
    }

    public static class Book{
        String name, author, genre, druh, movement;
        String year;

        public Book(String name, String author, String genre, String druh, String movement, String year) {
            this.name = name;
            this.author = author;
            this.genre = genre;
            this.druh = druh;
            this.movement = movement;
            this.year = year;
        }

        public static List<Book> getBookList() {
            if (generalDatabase == null) {
                try {
                    openOrCreateGeneralDatabase(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Book> books = new ArrayList<>();

            Cursor book = generalDatabase.rawQuery("SELECT book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "FROM book LEFT OUTER JOIN author ON book.author_id = author.id " +
                    "LEFT OUTER JOIN genre ON book.genre_id = genre.id " +
                    "LEFT OUTER JOIN druh ON book.druh_id = druh.id " +
                    "LEFT OUTER JOIN movement ON book.movement_id = movement.id " +
                    "ORDER BY book_name", null);

            if (book.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = book.getString(book.getColumnIndex("book_name"));
                    @SuppressLint("Range") String author = book.getString(book.getColumnIndex("author_name"));
                    @SuppressLint("Range") String genre = book.getString(book.getColumnIndex("genre_name"));
                    @SuppressLint("Range") String druh = book.getString(book.getColumnIndex("druh_name"));
                    @SuppressLint("Range") String movement = book.getString(book.getColumnIndex("movement_name"));
                    @SuppressLint("Range") String year = book.getString(book.getColumnIndex("year"));
                    books.add(new Book(name,
                            author,
                            genre,
                            druh,
                            movement,
                            year));
                } while(book.moveToNext());
            }
            book.close();
            return books;
        }

        public String getName() {
            return name;
        }

        public String getAuthor() {
            return author;
        }

        public String getGenre() {
            return genre;
        }

        public String getDruh() {
            return druh;
        }

        public String getMovement() {
            return movement;
        }

        public String getYear() {
            return year;
        }
    }

    public static class Movement{
        String name, sign, century;

        public Movement(String name, String sign, String century, List<String> authors) {
            this.name = name;
            this.sign = sign;
            this.century = century;
            this.authors = authors;
        }

        List<String> authors;

        public static List<Movement> getMovementList() {
            if (generalDatabase == null) {
                try {
                    openOrCreateGeneralDatabase(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Movement> movements = new ArrayList<>();

            Cursor movement = generalDatabase.rawQuery("SELECT movement.name AS movement_name, movement.sign AS sign, movement.century AS century " +
                    "FROM movement ORDER BY movement_name", null);

            Cursor author = generalDatabase.rawQuery("SELECT author.name AS author_name, movement.name AS movement_name " +
                    "FROM author LEFT OUTER JOIN movement ON author.movement_id = movement.id", null);

            if (movement.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = movement.getString(movement.getColumnIndex("movement_name"));
                    @SuppressLint("Range") String sign = movement.getString(movement.getColumnIndex("sign"));
                    @SuppressLint("Range") String century = movement.getString(movement.getColumnIndex("century"));
                    List<String> authors = getAuthors(author, movement);

                    movements.add(new Movement(name,
                            sign,
                            century,
                            authors));
                } while(movement.moveToNext());
            }
            movement.close();
            return movements;
        }

        @SuppressLint("Range")
        static List<String> getAuthors(Cursor author, Cursor movement) {
            List<String> authors = new ArrayList<>();

            @SuppressLint("Range") String movement_id = movement.getString(movement.getColumnIndex("movement_name"));

            if (author.moveToFirst()) {
                do {
                    String author_movement_name = author.getString(author.getColumnIndex("movement_name"));
                    if (author_movement_name != null && author_movement_name.equals(movement_id)) {
                        authors.add(author.getString(author.getColumnIndex("author_name")));
                    }
                } while(author.moveToNext());
            }
            return authors;
        }

        public String getName() {
            return name;
        }

        public String getSign() {
            return sign;
        }

        public String getCentury() {
            return century;
        }

        public List<String> getAuthors() {
            return authors;
        }
    }

    public static class Author{
        String name;

        public Author(String name, String movement, String country, List<String> books) {
            this.name = name;
            this.movement = movement;
            this.country = country;
            this.books = books;
        }

        String movement;
        String country;
        List<String> books;


        public static List<Author> getAuthorList() {
            if (generalDatabase == null) {
                try {
                    openOrCreateGeneralDatabase(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Author> authors = new ArrayList<>();

            Cursor author = generalDatabase.rawQuery("SELECT author.name AS author_name, movement.name AS movement_name, sex, author.country AS country " +
                    "FROM author LEFT OUTER JOIN movement " +
                    "ON author.movement_id = movement.id ORDER BY author_name", null);

            Cursor book = generalDatabase.rawQuery("SELECT book.name AS book_name, author.name AS author_name " +
                    "FROM book LEFT OUTER JOIN author ON book.author_id = author.id ORDER BY author_name", null);

            if (author.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = author.getString(author.getColumnIndex("author_name"));
                    @SuppressLint("Range") String movement = author.getString(author.getColumnIndex("movement_name"));
                    @SuppressLint("Range") String country = author.getString(author.getColumnIndex("country"));
                    List<String> books = getBooks(book, author);

                    authors.add(new Author(name,
                            movement,
                            country,
                            books));
                } while(author.moveToNext());
            }
            author.close();
            return authors;
        }

        @SuppressLint("Range")
        static List<String> getBooks(Cursor book, Cursor author) {
            List<String> books = new ArrayList<>();

            @SuppressLint("Range") String author_name = author.getString(author.getColumnIndex("author_name"));

            if (book.moveToFirst()) {
                do {
                    String book_author_name = book.getString(book.getColumnIndex("author_name"));
                    if (book_author_name.equals(author_name)) {
                        books.add(book.getString(book.getColumnIndex("book_name")));
                    }
                } while(book.moveToNext());
            }
            return books;
        }

        public String getName() {
            return name;
        }

        public String getMovement() {
            return movement;
        }

        public String getCountry() {
            return country;
        }

        public List<String> getBooks() {
            return books;
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
        return Utils.inputStreamEquals(toBeCompared, template);
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
     * @return a <b>{@link com.example.myapplication.firebase.Sql.QuestionList}</b> object
     */
    @SuppressLint("Range")
    public static QuestionList getQuestionList(AppCompatActivity con, String filter) {
        QuestionList questionList = new QuestionList();
        if (generalDatabase != null) {
            Cursor author = generalDatabase.rawQuery("SELECT author.id AS author_id, author.name AS author_name, movement.name AS movement_name, sex " +
                    "FROM author LEFT OUTER JOIN movement " +
                    "ON author.movement_id = movement.id " +
                    filter, null);

            Cursor book = generalDatabase.rawQuery("SELECT book.id AS book_id, book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "FROM book LEFT OUTER JOIN author ON book.author_id = author.id " +
                    "LEFT OUTER JOIN genre ON book.genre_id = genre.id " +
                    "LEFT OUTER JOIN druh ON book.druh_id = druh.id " +
                    "LEFT OUTER JOIN movement ON book.movement_id = movement.id" + filter, null);

            Cursor movement = generalDatabase.rawQuery("SELECT movement.id AS movement_id, movement.name AS movement_name, movement.sign AS sign, movement.century AS century " +
                    "FROM movement " + filter, null);


            Question.setAuthorAndBook(author, book);

            if (movement.moveToFirst()) {
                do {
                    Question mSQuestion = Question.createQuestion(movement, QuestionType.MOVEMENT_SIGN);
                    Question mCQuestion = Question.createQuestion(movement, QuestionType.MOVEMENT_CENTURY);

                    if (mCQuestion != null) questionList.add(mCQuestion);
                    if (mSQuestion != null) questionList.add(mSQuestion);
                } while (movement.moveToNext());
            }

            if (author.moveToFirst()) {
                do {
                    Question aMQuestion = Question.createQuestion(author, QuestionType.AUTHOR_MOVEMENT);

                    if (aMQuestion != null) questionList.add(aMQuestion);
                } while (author.moveToNext());
            }

            if (book.moveToFirst()) {
                do {
                    Question bDQuestion = Question.createQuestion(book, QuestionType.BOOK_DRUH);
                    Question bAQuestion = Question.createQuestion(book, QuestionType.BOOK_AUTHOR);
                    Question bGQuestion = Question.createQuestion(book, QuestionType.BOOK_GENRE);

                    Question bMQuestion = Question.createQuestion(book, QuestionType.BOOK_MOVEMENT);
                    Question aBQuestion = Question.createQuestion(book, QuestionType.AUTHOR_BOOK);

                    if (bMQuestion != null) questionList.add(bMQuestion);
                    if (aBQuestion != null) questionList.add(aBQuestion);
                    if (bDQuestion != null) questionList.add(bDQuestion);
                    if (bAQuestion != null) questionList.add(bAQuestion);
                    if (bGQuestion != null) questionList.add(bGQuestion);
                } while(book.moveToNext());
            }
            author.close();
            book.close();
            movement.close();

        } else {
            try {
                openOrCreateGeneralDatabase(con);
                return getQuestionList(con, filter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(questionList.questionList.size());
        return questionList;
    }

    /**
     * <p>
     *     Used to simplify distinguishing between filters.
     *
     * </p>
     * <p>
     *     Century refers to a column in the <b>author</b> table and has to be calculated later to work
     * </p>
     * <p>
     *     Originally meant to include multiple filter types
     * </p>
     */
    public enum FilterType {
        MOVEMENT("movement");
        public final String table;
        public final List<String> items;

        FilterType(String table) {
            this.table = table;
            this.items = getItems();
        }

        /**
         * Reads from movement table from the database
         * @return list of all movements
         */
        @SuppressLint("Range")
        public List<String> getItems() {
            List<String> items = new ArrayList<>();
            if (generalDatabase == null) {
                try {
                    openOrCreateGeneralDatabase(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Cursor cursor = generalDatabase.rawQuery("SELECT movement.name FROM movement", null);
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

        /**
         * @return the name of the table it refers to
         */
        @Override
        public String toString() {
            return table;
        }

    }

    public static class Filter{
        private static final String beginning = " WHERE (", equal = "movement_name = ", separator = " OR ", end = ")";
        public static String formatFilters(List<String> movements) {
            if (movements.isEmpty()) return "";
            String filter = beginning + " " + equal + "'" + movements.get(0) + "'";
            loop:
            for (String movement : movements) {
                if (movement.equals(movements.get(0))) continue loop;
                filter += separator + equal + "'" + movement + "'";
            }
            filter += end;

            return filter;
        }
    }

    public static class Question implements Serializable{
        public final String text;
        private final Answer[] answers;
        public final QuestionType questionType;
        private static Cursor book, author;
        public final String movement;

        private Question(QuestionType questionType, String movement, String text, Answer... answers) {
            this.questionType = questionType;
            this.answers = answers;
            this.text = text;
            this.movement = movement;
        }

        public Question(Question question) {
            this(question.questionType, question.movement, question.text, question.answers);
        }

        /**
         * Sets the following variables (used only for Book-Author question)
         */
        public static void setAuthorAndBook(Cursor author, Cursor book) {
            Question.author = author;
            Question.book = book;
        }

        @Override
        public String toString() {
            return text + " \n           "
                    + "[" + getA().text + ", " + getA().isRight + "]\n           "
                    + "[" + getB().text + ", " + getB().isRight + "]\n           "
                    + "[" + getC().text + ", " + getC().isRight + "]\n           "
                    + "[" + getD().text + ", " + getD().isRight + "]\n";
        }

        /**
         * @param cursor Either the <b>author</b> or the <b>book</b> table
         * @param questionType Determines the type of the question which then alters the question text and in the case of Book-Druh question changes the amount of answers
         * @return <b>{@link com.example.myapplication.firebase.Sql.Question}</b> object of a determined <b>{@link com.example.myapplication.firebase.Sql.QuestionType}</b>
         */
        @SuppressLint("Range")
        private static Question createQuestion(Cursor cursor, QuestionType questionType) {
            int position = cursor.getPosition();
            if (position == cursor.getCount()) return null;
            if (cursor.getString(cursor.getColumnIndex(questionType.questionColumn)) == null || cursor.getString(cursor.getColumnIndex(questionType.answerColumn)) == null) return null;
            String movement = cursor.getString(cursor.getColumnIndex("movement_name"));
            switch(questionType) {
                case BOOK_DRUH:
                    List<Answer> answers1 = new ArrayList<>();
                    answers1.add(new Answer("Lyricko-epický", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Lyricko-epický")));
                    answers1.add(new Answer("Lyrika", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Lyrika")));
                    answers1.add(new Answer("Epika", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Epika")));
                    answers1.add(new Answer("Drama", cursor.getString(cursor.getColumnIndex(questionType.answerColumn)).equals("Drama")));
                    Collections.shuffle(answers1);
                    return new Question(QuestionType.BOOK_DRUH, movement, QuestionType.completeBDText(cursor.getString(cursor.getColumnIndex(questionType.questionColumn))), answers1.toArray(new Answer[0]));
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
                            author.moveToPosition(getAuthorPositionFromBook(book, author));
                            if (author.getPosition() >= author.getCount()) {
                                author.moveToPosition(save);
                                return null;
                            }
                            questionText = QuestionType.completeABText((author.getString(author.getColumnIndex("sex")).equals("male") ? "" : "a"),
                                    cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case BOOK_AUTHOR:
                            questionText = QuestionType.completeBAText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case BOOK_GENRE:
                            questionText = QuestionType.completeBGText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case MOVEMENT_CENTURY:
                            questionText = QuestionType.completeMCText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                        case MOVEMENT_SIGN:
                            questionText = QuestionType.completeMSText(cursor.getString(cursor.getColumnIndex(qCol)));
                            break;
                    }
                    List<Answer> answers = new ArrayList<>();
                    List<String> questionStrings = new ArrayList<>();
                    answers.add(new Answer(cursor.getString(cursor.getColumnIndex(aCol)), true));
                    questionStrings.add(cursor.getString(cursor.getColumnIndex(qCol)));
                    int[] indexes = Utils.getNRandomNumbers(cursor.getCount(), cursor.getCount());
                    loop:
                    for (int i = 0; i < 3; i++) {
                        for (int j : indexes) {
                            cursor.moveToPosition(j);
                            if ((isValidAnswer(answers, cursor.getString(cursor.getColumnIndex(aCol)))) && (!containsQuestionString(questionStrings, cursor.getString(cursor.getColumnIndex(qCol))))) {
                                questionStrings.add(cursor.getString(cursor.getColumnIndex(qCol)));
                                answers.add(new Answer(cursor.getString(cursor.getColumnIndex(aCol)), false));
                                continue loop;
                            }
                        }
                        cursor.moveToPosition(position);
                        return null;
                    }

                    Collections.shuffle(answers);
                    cursor.moveToPosition(position);
                    return new Question(questionType, movement, questionText, answers.toArray(new Answer[0]));
            }


        }

        /**
         * <p>Used in {@link #createQuestion(Cursor, QuestionType)} in order to generate random wrong answers and checking if each answer is unique</p>
         * @param answers list of current answers to the <b>Question</b> object that is being generated
         * @param answer a potential answer that is yet to be checked if it is unique
         * @return false if the answer is unique
         */
        private static boolean isValidAnswer(List<Answer> answers, String answer) {
            if (answer == null) return false;
            for (Answer answer1: answers) {
                if (answer1.text == null) return false;
                if (answer1.text.equals(answer)) return false;
            }
            return true;
        }

        private static boolean containsQuestionString(List<String> questionStrings, String questionString) {
            for (String questionString1: questionStrings) {
                if (questionString == null) return true;
                if (questionString.equals(questionString1)) return true;
            }
            return false;
        }

        private static int getAuthorPositionFromBook(@NonNull Cursor book, @NonNull Cursor author) {
            @SuppressLint("Range") String author_name = book.getString(book.getColumnIndex("author_name"));
            int initialPosition = author.getPosition();
            author.moveToFirst();
            do {
                @SuppressLint("Range") String name = author.getString(author.getColumnIndex("author_name"));
                if (name.equals(author_name)) {
                    int authorPosition = author.getPosition();
                    author.moveToPosition(initialPosition);
                    return authorPosition;
                }
            } while(author.moveToNext());
            author.moveToPosition(initialPosition);
            return initialPosition;
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

        @Override
        public String toString() {
            System.out.println(questionList.size());
            String s = "";
            for (Question q : questionList) {
                s+=q.toString();
                s+="\n//////////////////\n";
            }
            return s;
        }
    }

    /**
     * A simple object containing the <b>text</b> and <b>truthfulness</b> of the answer
     */
    public static class Answer implements Serializable{
        public final String text;
        public final boolean isRight;

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
        BOOK_MOVEMENT("Z jakého směru je kniha \"<book>\"?", "book_name", "movement_name"),
        BOOK_DRUH("Ke kterému druhu se řadí \"<book>\"?", "book_name", "druh_name"),
        BOOK_GENRE("Do jakého žánru se řadí \"<book>\"?", "book_name", "genre_name"),
        MOVEMENT_CENTURY("Z jakého období je směr <movement>?", "movement_name", "century"),
        MOVEMENT_SIGN("Jaké jsou typické znaky směru <movement>?", "movement_name", "sign");
        private final String questionText, questionColumn, answerColumn;

        QuestionType(String questionText, String questionColumn, String answerColumn) {
            this.questionText = questionText;
            this.questionColumn = questionColumn;
            this.answerColumn = answerColumn;
        }

        @NonNull
        private static String completeAMText(String author) {
            return AUTHOR_MOVEMENT.questionText.replaceAll("<author>", author);
        }
        @NonNull
        private static String completeBMText(String book) {
            return BOOK_MOVEMENT.questionText.replaceAll("<book>", book);
        }

        @NonNull
        private static String completeABText(String sex, String author) {
            return AUTHOR_BOOK.questionText.replace("<author>", author).replace("<sex>", sex);
        }

        @NonNull
        private static String completeBAText(String book) {
            return BOOK_AUTHOR.questionText.replaceAll("<book>", book);
        }

        @NonNull
        private static String completeBDText(String book) {
            return BOOK_DRUH.questionText.replaceAll("<book>", book);
        }

        @NonNull
        private static String completeBGText(String book) {
            return BOOK_GENRE.questionText.replaceAll("<book>", book);
        }

        @NonNull
        private static String completeMCText(String movement) {
            return MOVEMENT_CENTURY.questionText.replaceAll("<movement>", movement);
        }

        @NonNull
        private static String completeMSText(String movement) {
            return MOVEMENT_SIGN.questionText.replaceAll("<movement>", movement);
        }
    }
}

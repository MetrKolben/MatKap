package com.example.myapplication.cheat_sheet;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestClass {
    static class Book{
        String name, author, genre, druh, movement;
        int year;

        public Book(String name, String author, String genre, String druh, String movement, int year) {
            this.name = name;
            this.author = author;
            this.genre = genre;
            this.druh = druh;
            this.movement = movement;
            this.year = year;
        }

        static List<Book> getBookList() {
            // Místo tohohle bude ten try catch block
            SQLiteDatabase generalDatabase = null;
            // Místo tohohle bude ten try catch block

            List<Book> books = new ArrayList<>();

            Cursor book = generalDatabase.rawQuery("SELECT book.name AS book_name, author.name AS author_name, genre.name AS genre_name, druh.name AS druh_name, movement.name AS movement_name, year " +
                    "FROM book LEFT OUTER JOIN author ON book.author_id = author.id " +
                    "LEFT OUTER JOIN genre ON book.genre_id = genre.id " +
                    "LEFT OUTER JOIN druh ON book.druh_id = druh.id " +
                    "LEFT OUTER JOIN movement ON book.movement_id = movement.id", null);

            if (book.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = book.getString(book.getColumnIndex("book_name"));
                    @SuppressLint("Range") String author = book.getString(book.getColumnIndex("author_name"));
                    @SuppressLint("Range") String genre = book.getString(book.getColumnIndex("genre_name"));
                    @SuppressLint("Range") String druh = book.getString(book.getColumnIndex("druh_name"));
                    @SuppressLint("Range") String movement = book.getString(book.getColumnIndex("movement_name"));
                    @SuppressLint("Range") int year = book.getInt(book.getColumnIndex("year"));
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
    }

    static class Movement{
        String name, sign, century;

        public Movement(String name, String sign, String century) {
            this.name = name;
            this.sign = sign;
            this.century = century;
        }

        static List<Movement> getMovementList() {
            // Místo tohohle bude ten try catch block
            SQLiteDatabase generalDatabase = null;
            // Místo tohohle bude ten try catch block

            List<Movement> movements = new ArrayList<>();

            Cursor movement = generalDatabase.rawQuery("SELECT movement.name AS movement_name, movement.sign AS sign, movement.century AS century " +
                    "FROM movement ", null);

            if (movement.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = movement.getString(movement.getColumnIndex("movement_name"));
                    @SuppressLint("Range") String sign = movement.getString(movement.getColumnIndex("sign"));
                    @SuppressLint("Range") String century = movement.getString(movement.getColumnIndex("century"));
                    movements.add(new Movement(name,
                            sign,
                            century));
                } while(movement.moveToNext());
            }
            movement.close();
            return movements;
        }
    }

    static class Author{
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


        static List<Author> getMovementList() {
            // Místo tohohle bude ten try catch block
            SQLiteDatabase generalDatabase = null;
            // Místo tohohle bude ten try catch block

            List<Author> authors = new ArrayList<>();

            Cursor author = generalDatabase.rawQuery("SELECT author.name AS author_name, movement.name AS movement_name, sex, author.country AS country " +
                    "FROM author LEFT OUTER JOIN movement " +
                    "ON author.movement_id = movement.id", null);

            Cursor book = generalDatabase.rawQuery("SELECT book.name AS book_name, author.name AS author_name " +
                    "FROM book LEFT OUTER JOIN author ON book.author_id = author.id", null);

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
    }
}

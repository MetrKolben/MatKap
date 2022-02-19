package com.example.myapplication.cheat_sheet;

import com.example.myapplication.firebase.Sql;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private static final List<Book> values = new ArrayList<>();

    private String name;

    private String author;
    private String movement;
    private String druh;
    private String genre;
    private String publishYear;
    private boolean expandable;


    public Book(String name,
                String author,
                String movement,
                String druh,
                String genre,
                String publishYear) {

        this.name = name;
        this.author = author;
        this.movement = movement;
        this.druh = druh;
        this.genre = genre;
        this.publishYear = publishYear;
        this.expandable = false;
    }

    public static List<Book> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public String getDruh() {
        return druh;
    }

    public void setDruh(String druh) {
        this.druh = druh;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", movement='" + movement + '\'' +
                ", druh='" + druh + '\'' +
                ", genre='" + genre + '\'' +
                ", publishYear=" + publishYear +
                '}';
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}

package cz.matkap.cheat_sheet;

import java.util.List;

public class Author {

    private String name;
    private List<String> books;
    private String movement;
    private String country;


    private boolean expandable;

    public Author(String name, List<String> books, String movement, String country) {
        this.name = name;
        this.books = books;
        this.movement = movement;
        this.country = country;
        this.expandable = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBooks() {
        return books;
    }

    public String getMovement() {
        return movement;
    }


    public String getCountry() {
        return country;
    }


    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", books=" + books +
                ", movement='" + movement + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}

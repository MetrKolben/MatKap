package com.example.myapplication.cheat_sheet;

import java.util.ArrayList;
import java.util.List;

public class Movement {

    private static final List<Movement> values = new ArrayList<>();

    private String name;
    private String sign;
    private String century;

    private boolean expandable;

    private List<String> authors;

    public Movement(String name, String sign, String century, List<String> authors) {
        this.name = name;
        this.sign = sign;
        this.century = century;
        this.expandable = false;
        this.authors = authors;
    }

    public static List<Movement> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Movement{" +
                "name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                ", century='" + century + '\'' +
                '}';
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }
}

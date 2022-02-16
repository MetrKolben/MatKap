package com.example.myapplication.cheat_sheet;

import com.example.myapplication.firebase.Sql;

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
        sign = Sql.Movement.getMovementSign(name);
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCentury() {
        century = Sql.Movement.getMovementCentury(name);
        return century;
    }

    public void setCentury(String century) {
        this.century = century;
    }

    public List<String> getAuthors() {
        authors = Sql.Movement.getMovementAuthors(name);
        return authors;
    }

//    public void setAuthors(List<String> authors) {
//        this.authors = authors;
//    }

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

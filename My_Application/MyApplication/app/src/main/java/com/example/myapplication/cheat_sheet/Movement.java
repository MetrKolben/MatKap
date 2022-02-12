package com.example.myapplication.cheat_sheet;

public class Movement {

    private String name;
    private String sign;
    private String century;
    private boolean expandable;

    public Movement(String name, String sign, String century) {
        this.name = name;
        this.sign = sign;
        this.century = century;
        this.expandable = false;
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

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCentury() {
        return century;
    }

    public void setCentury(String century) {
        this.century = century;
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

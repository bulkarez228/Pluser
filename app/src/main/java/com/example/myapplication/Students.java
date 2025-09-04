package com.example.myapplication;

public class Students implements Comparable<Students>
{
    int score;
    int curColor;
    String name;


    public Students() {
    }

    public Students(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Students student) {
        return Integer.compare(getScore(), student.getScore());
    }

    public int getCurColor() {
        return curColor;
    }

    public void setCurColor(int curColor) {
        this.curColor = curColor;
    }
}

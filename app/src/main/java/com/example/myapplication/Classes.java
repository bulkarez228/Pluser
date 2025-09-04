package com.example.myapplication;

import java.util.ArrayList;

public class Classes {
    String name;
    boolean visible = true;
    ArrayList<Students> studentsList = new ArrayList<>();
    int typeOfSort;

    public Classes(){   }

    public Classes(String class_) {
        this.name = class_;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ArrayList<Students> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(ArrayList<Students> parallelsList) {
        this.studentsList = parallelsList;
    }

    public int getTypeOfSort() {
        return typeOfSort;
    }

    public void setTypeOfSort(int typeOfSort) {
        this.typeOfSort = typeOfSort;
    }
}

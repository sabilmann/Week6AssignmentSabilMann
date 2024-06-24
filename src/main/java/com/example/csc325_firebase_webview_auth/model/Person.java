package com.example.csc325_firebase_webview_auth.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Person {

    private final SimpleStringProperty name;
    private final SimpleStringProperty major;
    private final SimpleIntegerProperty age;

    public Person(String name, String major, int age) {
        this.name = new SimpleStringProperty(name);
        this.major = new SimpleStringProperty(major);
        this.age = new SimpleIntegerProperty(age);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getMajor() {
        return major.get();
    }

    public SimpleStringProperty majorProperty() {
        return major;
    }

    public void setMajor(String major) {
        this.major.set(major);
    }

    public int getAge() {
        return age.get();
    }

    public SimpleIntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }
}


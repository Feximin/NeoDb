package com.feximin.neodbsample;

/**
 * Created by Neo on 16/1/6.
 */
public class Teacher {
    private int age;
    private String name;
    private String title;
    private String school;
    private int studentCount;

    public Teacher(){}
    public Teacher(int age, String name, String title, String school, int studentCount) {
        this.age = age;
        this.name = name;
        this.title = title;
        this.school = school;
        this.studentCount = studentCount;
    }
}

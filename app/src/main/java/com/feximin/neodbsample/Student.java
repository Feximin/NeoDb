package com.feximin.neodbsample;

import com.feximin.neodb.annotation.MultiUser;
import com.feximin.neodb.model.Model;

/**
 * Created by Neo on 16/1/6.
 */
@MultiUser
public class Student implements Model {
    private int age;
    private String name;
    private String title;
    private String teacher;
    public Student(){}

    public Student(int age, String name, String title, String teacher) {
        this.age = age;
        this.name = name;
        this.title = title;
        this.teacher = teacher;
    }
}

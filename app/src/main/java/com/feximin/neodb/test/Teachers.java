package com.feximin.neodb.test;

import com.feximin.neodb.model.Model;

/**
 * Created by Neo on 16/1/6.
 */
public class Teachers implements Model {
    private int age;
    private String name;
    private String title;
    private String school;
    private int studentCount;

    public Teachers(){}
    public Teachers(int age, String name, String title, String school, int studentCount) {
        this.age = age;
        this.name = name;
        this.title = title;
        this.school = school;
        this.studentCount = studentCount;
    }
}

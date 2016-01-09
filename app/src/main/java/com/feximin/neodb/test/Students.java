package com.feximin.neodb.test;

import com.feximin.neodb.model.Model;

/**
 * Created by Neo on 16/1/6.
 */
public class Students extends Model {
    private int age;
    private String name;
    private String title;
    private String teacher;
    public Students(){}

    public Students(int age, String name, String title, String teacher) {
        this.age = age;
        this.name = name;
        this.title = title;
        this.teacher = teacher;
    }
}

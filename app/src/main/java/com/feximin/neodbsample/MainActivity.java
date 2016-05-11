package com.feximin.neodbsample;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.feximin.neodb.core.DBQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt = (TextView) findViewById(R.id.txt);

        List<Student> students = new ArrayList<>();
        List<Teacher> teachers = new ArrayList<>();
        for(int i = 0; i<500; i++){
            teachers.add(new Teacher(12*i+i, "name_"+i, "title_"+i, "school_"+i, i * 10));
            students.add(new Student(12*i, "name_"+i, "title_"+i, "teacher_"+i));
        }

        new DBQuery<>(Student.class).insert(students);
        new DBQuery<>(Teacher.class).insert(teachers);
        new DBQuery<>(Student.class).insertMy(students);

        List<Student> list = new DBQuery<>(Student.class).endSelect();
        int size = list.size();
        txt.append("第一次添加之后Student的size是--》" + size );
        txt.append("\n");

        list = new DBQuery<>(Student.class).where("name").eq("name_1").endSelect();
        txt.append("其中name为name_1的Student的size是--》" + list.size() );
        txt.append("\n");

        List<Teacher> list1 = new DBQuery<>(Teacher.class).endSelect();
        int size1 = list1.size();
        txt.append("Teachers表中的size是--》" + size1 );
        txt.append("\n");

        ContentValues values = new ContentValues();
        values.put("name", "test");
        new DBQuery<>(Student.class).where("name").eq("name_0").endUpdate(values);
        List<Student> list2 = new DBQuery<>(Student.class).where("name").eq("test").endSelect();


        txt.append("Student表中name为test的size是--》" + list2.size() );
        txt.append("\n");

        new DBQuery<>(Student.class).where("name").eq("name_1").endDelete();
        list = new DBQuery<>(Student.class).endSelect();
        txt.append("删除Student表中name为name_1之后的size是--》" + list.size() );
        txt.append("\n");


//        List<Student> list4 = new DBQuery<>(Student.class).endSelect();
//        size = list4.size();
//
//        for(int mm = 0; mm < 10; mm++){
//            final int re = mm;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    int i = 0;
//                    while (i < 500){
//                        int j = i % 100;
//                        new DBQuery<>(Student.class).insert(new Student(12*j, "name_"+j, "title_"+j, "teacher_"+j));
//                        try {
//                            Thread.sleep(2 * re);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        new DBQuery<>(Teacher.class).insert(new Teacher(12*j+j, "name_"+i, "title_"+i, "school_"+i, i * 10));
//                        i++;
//                        showCount();
//                    }
//
//                    i = 0;
//                    while (i < 500){
//                        int j = i % 100;
//                        new DBQuery<>(Student.class).where("age").eq(12*j + "").endDelete();
//                        try {
//                            Thread.sleep(8 * re + re / 2);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        new DBQuery<>(Teacher.class).where("age").eq(12*j+j + "").endDelete();
//                        i++;
//
//                        showCount();
//                    }
//                }
//            }).start();
//        }
    }

    private long last ;
    private synchronized void showCount(){
        if(System.currentTimeMillis() - last < 100) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int count = new DBQuery<>(Student.class).count();
                int count2 = new DBQuery<>(Teacher.class).count();
                txt.setText(count + " " + count2);
                last = System.currentTimeMillis();
            }
        });
    }
}

package com.feximin.neodb.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.feximin.neodb.R;
import com.feximin.neodb.core.DBConfig;
import com.feximin.neodb.core.DBQuery;

public class MainActivity extends AppCompatActivity {

    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt = (TextView) findViewById(R.id.txt);
        //初始化 所需的Model
        DBConfig config = DBConfig.getInstance();

        config.addModel(Students.class);
        config.addModel(Teachers.class);

        config.build(this, "test", 1);

//        List<Students> students = new ArrayList<>();
//        List<Teachers> teachers = new ArrayList<>();
//        for(int i = 0; i<100; i++){
//            teachers.add(new Teachers(12*i+i, "name_"+i, "title_"+i, "school_"+i, i * 10));
//            students.add(new Students(12*i, "name_"+i, "title_"+i, "teacher_"+i));
//        }
//
//        new DBQuery<>(Students.class).insert(students);
//        new DBQuery<>(Teachers.class).insert(teachers);
//
//        List<Students> list = new DBQuery<>(Students.class).endSelect();
//        int size = list.size();
//
//
//        List<Teachers> list1 = new DBQuery<>(Teachers.class).endSelect();
//        int size1 = list1.size();
//
//        ContentValues values = new ContentValues();
//        values.put("name", "test");
//        new DBQuery<>(Students.class).where("name").eq("name_0").endUpdate(values);
//        List<Students> list2 = new DBQuery<>(Students.class).where("name").eq("test").endSelect();
//        Toast.makeText(this, size + " " + size1 + "" + list2.size(), Toast.LENGTH_LONG).show();
//
//        new DBQuery<>(Students.class).where("name").eq("name_1").endDelete();
//        list = new DBQuery<>(Students.class).endSelect();
//
//
//        List<Students> list4 = new DBQuery<>(Students.class).endSelect();
//        size = list4.size();

        for(int mm = 0; mm < 10; mm++){
            final int re = mm;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (i < 500){
                        int j = i % 100;
                        new DBQuery<>(Students.class).insert(new Students(12*j, "name_"+j, "title_"+j, "teacher_"+j));
                        try {
                            Thread.sleep(2 * re);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new DBQuery<>(Teachers.class).insert(new Teachers(12*j+j, "name_"+i, "title_"+i, "school_"+i, i * 10));
                        i++;
                        showCount();
                    }

                    i = 0;
                    while (i < 500){
                        int j = i % 100;
                        new DBQuery<>(Students.class).where("age").eq(12*j + "").endDelete();
                        try {
                            Thread.sleep(8 * re + re / 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new DBQuery<>(Teachers.class).where("age").eq(12*j+j + "").endDelete();
                        i++;

                        showCount();
                    }
                }
            }).start();
        }
    }

    private long last ;
    private synchronized void showCount(){
        if(System.currentTimeMillis() - last < 100) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int count = new DBQuery<>(Students.class).count();
                int count2 = new DBQuery<>(Teachers.class).count();
                txt.setText(count + " " + count2);
                last = System.currentTimeMillis();
            }
        });
    }
}

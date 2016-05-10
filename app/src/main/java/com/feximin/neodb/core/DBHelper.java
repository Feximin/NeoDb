package com.feximin.neodb.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feximin.neodb.manager.TableManager;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.Model;
import com.feximin.neodb.model.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Neo on 16/1/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;
    //无参的构造器是必需的，singletonUtil要用

    private DBHelper(DBConfig config){
        super(config.getContext(), config.getDbName(), null, config.getDbVersion());
        create(config);
    }

    private static DBHelper INSTANCE;

    public static DBHelper getInstance(){
        if (INSTANCE == null){
            synchronized (DBHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new DBHelper(DBConfig.obtain());
                }
            }
        }
        return INSTANCE;
    }

    private void create(DBConfig config){
        List<TableInfo> existList = TableManager.getInstance().queryAllTable();       //已经存在的表

        List<TableInfo> latestList = new ArrayList<>();
        for(Class<? extends Model> clazz : config.getModelList()){
            latestList.add(new TableInfo(clazz));
        }

        //开始比较，已经存在的表查看是否需要更新，不存在的表则新建
        for(TableInfo table : latestList){
            TableInfo existTable = null;  //是否已经存在
            for(TableInfo en : existList){
                if(en.equals(table)){
                    existTable = en;
                    break;
                }
            }
            if(existTable == null){
                TableManager.getInstance().createTable(table);
            }else{
                for(FieldInfo field : table.fieldList){
                    boolean isFieldExist = false;
                    for(FieldInfo fi : existTable.fieldList){
                        if(fi.equals(field)){
                            isFieldExist = true;
                            break;
                        }
                    }
                    if(!isFieldExist) TableManager.getInstance().addField(field, table.name);
                }
            }
        }
    }


    public void execSQL(String sql){
        try {
            getDb().execSQL(sql);
        }finally {
            close();
        }
    }

    public Cursor execSelect(String sql){
        return execSelect(sql, null);
    }

    public Cursor execSelect(String sql, String[] args){
        try {
            return getDb().rawQuery(sql, args);
        }finally {
            close();
        }
    }
    public int execDelete(String tableName, String whereClause, String[] args){
        int affect = 0;
        try {
            affect = getDb().delete(tableName, whereClause, args);
        }finally {
            close();
        }
        return affect;
    }


    private AtomicInteger mDbRefCount = new AtomicInteger();
    public synchronized SQLiteDatabase getDb(){
        if(mDbRefCount.incrementAndGet() == 1){
            mDb = getWritableDatabase();
        }
        return mDb;
    }

    public synchronized void close(){
        if(mDbRefCount.decrementAndGet() <= 0){
            mDbRefCount.set(0);
            super.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

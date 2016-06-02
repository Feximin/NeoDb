package com.feximin.neodb.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.feximin.neodb.exceptions.DbException;
import com.feximin.neodb.exceptions.InsertException;
import com.feximin.neodb.manager.TableManager;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * 约定:	1.每个存储到数据库的有效字段都是以双下划线（“__”）开头结尾
 *   	2.private或者default修饰符修饰的成员变量，子类生成的表中不包含这些字段
 *   	3.每个表都需要有一个主键，如果没有使用Primary注解，将会默认生成一个主键
 *   	4.存到数据库里每个字段的开始和结尾会加上双下划线
 *   继承了这个接口的类都需要有一个无参构造
 * Created by Neo on 16/5/11.
 */
public class NeoDb {

    private final DBConfig mDBConfig;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;
    private AtomicInteger mDbRefCount = new AtomicInteger();

    public static void init(DBConfig config){
        if (config == null){
            throw new DbException("config can not be null !!");
        }else{
            synchronized (NeoDb.class){
                if (INSTANCE == null){
                    INSTANCE = new NeoDb(config);
                    INSTANCE.mDBHelper = new DBHelper(config);
                    INSTANCE.initTables();
                }
            }
        }
    }

    private NeoDb(DBConfig config){
        this.mDBConfig = config;
    }

    public DBConfig getConfig(){
        return mDBConfig;
    }

    private void initTables( ){
        List<TableInfo> existList = TableManager.queryAllTable();       //已经存在的表

        List<TableInfo> latestList = new ArrayList<>();
        for(Class<?> clazz : mDBConfig.getModelList()){
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
                TableManager.createTable(table);
            }else{
                for(FieldInfo field : table.fieldList){
                    boolean isFieldExist = false;
                    for(FieldInfo fi : existTable.fieldList){
                        if(fi.equals(field)){
                            isFieldExist = true;
                            break;
                        }
                    }
                    if(!isFieldExist) TableManager.addField(field, table.name);
                }
            }
        }
    }

    private static NeoDb INSTANCE;

    public static NeoDb getInstance(){
        if (INSTANCE == null){
            throw new DbException("please init before call getInstance method !!");
        }
        return INSTANCE;
    }



    public void execSQL(String sql){
        try {
            getDb().execSQL(sql);
        }finally {
            close();
        }
    }

    public long insert(String tableName, ContentValues values){
        long id = -1;
        try {
            id = getDb().insert(tableName, null, values);
        }catch (Exception e){
            e.printStackTrace();
            throw new InsertException();
        }finally {
            close();
        }
        return id;
    }

    public Cursor rawQuery(String sql, String[] args){
        return getDb().rawQuery(sql, args);
    }

    public Cursor rawQuery(String sql){
        return rawQuery(sql, null);
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

    public synchronized SQLiteDatabase getDb(){
        if(mDbRefCount.incrementAndGet() == 1){
            mDb = mDBHelper.getWritableDatabase();
        }
        return mDb;
    }

    public synchronized void close(){
        if(mDbRefCount.decrementAndGet() <= 0){
            mDbRefCount.set(0);
            mDBHelper.close();
        }
    }

    public static class DBHelper extends SQLiteOpenHelper {


        public DBHelper(DBConfig config){
            super(config.getContext(), config.getDbName(), null, config.getDbVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

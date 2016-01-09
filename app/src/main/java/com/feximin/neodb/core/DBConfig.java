package com.feximin.neodb.core;

import android.content.Context;

import com.feximin.neodb.model.Model;
import com.feximin.neodb.model.TableInfo;
import com.feximin.neodb.utils.SingletonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neo on 16/1/6.
 */
public class DBConfig {
    private Context context;
    private String dbName;
    private int dbVersion;
    private List<Class<? extends Model>> mValidModelList = new ArrayList<>();

    private DBConfig(){}

    public static DBConfig getInstance(){
        return SingletonUtil.getInstance(DBConfig.class);
    }

    public void build(Context context, String name, int version){
        this.context = context;
        this.dbName = name;
        this.dbVersion = version;
        com.feximin.neodb.core.DBHelper.getInstance().create(this);
    }
    public void build(Context context, int version){
        build(context, context.getPackageName(), version);
    }

    public void addModel(Class<? extends Model> clazz){
        mValidModelList.add(clazz);
    }

    public List<TableInfo> getTableList(){
        List<TableInfo> tableInfoList = new ArrayList<>();
        for(Class<? extends Model> clazz : mValidModelList){
            tableInfoList.add(new TableInfo(clazz));
        }
        return tableInfoList;
    }

    public Context getContext() {
        return context;
    }

    public String getDbName() {
        return dbName;
    }

    public int getDbVersion() {
        return dbVersion;
    }
}

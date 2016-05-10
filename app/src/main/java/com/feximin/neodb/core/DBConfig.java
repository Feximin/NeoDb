package com.feximin.neodb.core;

import android.content.Context;

import com.feximin.neodb.model.Model;
import com.feximin.neodb.utils.SingletonUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Neo on 16/1/6.
 */
public class DBConfig {
    private Context context;
    private String dbName;
    private int dbVersion;
    private UserIdFetcher userIdFetcher;
    private Set<Class<? extends Model>> validModelList = new HashSet<>();

    private DBConfig(){}

    public static DBConfig obtain(){
        return SingletonUtil.getInstance(DBConfig.class);
    }

    public DBConfig addModel(Class<? extends Model> clazz){
        if (validModelList == null) validModelList = new HashSet<>();
        validModelList.add(clazz);
        return this;
    }
    public DBConfig context(Context context){
        this.context = context.getApplicationContext();
        return this;
    }

    public DBConfig name(String name){
        this.dbName = name;
        return this;
    }

    public DBConfig version(int version){
        this.dbVersion = version;
        return this;
    }

    public DBConfig userIdFetcher(UserIdFetcher fetcher){
        this.userIdFetcher = fetcher;
        return this;
    }


    public Set<Class<? extends Model>> getModelList(){
        return this.validModelList;
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

    public UserIdFetcher getUserIdFetcher(){
        return this.userIdFetcher;
    }

}

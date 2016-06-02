package com.feximin.neodb.core;

import android.content.Context;

import com.feximin.neodb.exceptions.NoEmptyConstructorException;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Neo on 16/1/6.
 * 初始化 所需的Model，去Application中初始化，因为进程可能被kill掉
 */
public class DBConfig {
    private Context context;
    private String dbName;
    private int dbVersion;
    private UserIdFetcher userIdFetcher;
    private Set<Class<?>> validModelList = new HashSet<>();

    public DBConfig addModel(Class<?> clazz){
        if (validModelList == null) validModelList = new HashSet<>();
        try {
            Constructor constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new NoEmptyConstructorException(clazz.getName());
        }
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


    public Set<Class<?>> getModelList(){
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

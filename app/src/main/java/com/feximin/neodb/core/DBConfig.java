package com.feximin.neodb.core;

import android.content.Context;
import android.text.TextUtils;

import com.feximin.neodb.model.Model;
import com.feximin.neodb.model.TableInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Neo on 16/1/6.
 */
public class DBConfig {
    private Context context;
    private String dbName;
    private int dbVersion;
    private Set<Class<? extends Model>> mValidModelList = new HashSet<>();

    private DBConfig(){}

    private static DBConfig INSTANCE;

    public static DBConfig getInstance(){
        return INSTANCE;
    }

    public void addModel(Class<? extends Model> clazz){
        mValidModelList.add(clazz);
    }

    public Set<Class<? extends Model>> getModelList(){
        return this.mValidModelList;
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

    public static class Builder{
        Context context;
        String name;
        int version = 1;

        public Builder context(Context context){
            this.context = context;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder version(int version){
            this.version = version;
            return this;
        }

        public DBConfig build(){
            DBConfig config = new DBConfig();
            if (this.context == null) throw new IllegalArgumentException("context can not be null !!");
            config.context = this.context;
            if (TextUtils.isEmpty(this.name)) this.name = this.context.getPackageName();
            config.dbName = this.name;
            config.dbVersion = this.version;
            INSTANCE = config;
            return INSTANCE;
        }
    }
}

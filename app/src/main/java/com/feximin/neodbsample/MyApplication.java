package com.feximin.neodbsample;

import android.app.Application;

import com.feximin.neodb.core.DBConfig;
import com.feximin.neodb.core.UserIdFetcher;

/**
 * Created by Neo on 16/5/11.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 所需的Model，去Application中初始化，因为进程可能被kill掉
        DBConfig.obtain()
                .context(this)
                .name("test")
                .version(1)
                .userIdFetcher(new UserIdFetcher() {
                    @Override
                    public String fetchUserId() {
                        return "2";
                    }
                })
                .addModel(Student.class)
                .addModel(Teacher.class);
    }
}

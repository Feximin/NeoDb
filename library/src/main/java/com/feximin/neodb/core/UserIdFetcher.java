package com.feximin.neodb.core;

/**
 * Created by Neo on 16/5/10.
 * 在MulitUserMode的时候用来获取userId
 */
public interface UserIdFetcher {
    String fetchUserId();
}

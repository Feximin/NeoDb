package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/5/11.
 */
public class DbException extends RuntimeException {
    public DbException(String message){
        super(message);
    }
}

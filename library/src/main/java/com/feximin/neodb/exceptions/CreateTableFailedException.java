package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/9.
 */
public class CreateTableFailedException extends RuntimeException {
    public CreateTableFailedException(String tableName) {
        super("create table failed --> " + tableName);
    }
}

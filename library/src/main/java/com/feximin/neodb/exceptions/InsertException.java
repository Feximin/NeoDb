package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 */
public class InsertException extends RuntimeException {
    public InsertException() {
        super("data insert error");
    }
}

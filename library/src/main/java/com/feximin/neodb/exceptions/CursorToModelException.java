package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 */
public class CursorToModelException extends RuntimeException{
    public CursorToModelException() {
        super("transform cursor to model error");
    }
}

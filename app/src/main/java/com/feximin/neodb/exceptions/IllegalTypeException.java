package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/6.
 */
public class IllegalTypeException extends RuntimeException {
    public IllegalTypeException(Class<?> type){
        super("no this kind of type -->" + type.getName());
    }
}

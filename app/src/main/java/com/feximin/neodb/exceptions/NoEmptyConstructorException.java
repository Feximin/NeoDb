package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 */
public class NoEmptyConstructorException extends RuntimeException {
    public NoEmptyConstructorException(String clazzName){
        super("no empty constructor found --> " + clazzName);
    }
}

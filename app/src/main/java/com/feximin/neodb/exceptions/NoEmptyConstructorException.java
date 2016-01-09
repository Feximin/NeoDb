package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 */
public class NoEmptyConstructorException extends NoSuchMethodException {
    public NoEmptyConstructorException(){
        super("no empty constructor found");
    }
}

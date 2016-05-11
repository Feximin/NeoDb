package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/6.
 */
public class IllegalIdentifySuffixException extends RuntimeException {
    public IllegalIdentifySuffixException(){
        super("you should assign a identify suffix");
    }
}

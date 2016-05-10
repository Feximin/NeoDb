package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/5/10.
 */
public class NotMultiUserModeClassException extends RuntimeException {
    public NotMultiUserModeClassException(Class<?> clazzName){
        super("this is not a MultiUser mode class --> " + clazzName.getName());
    }
}

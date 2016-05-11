package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/9.
 */
public class MultiPrimaryKeyException extends RuntimeException {
    public MultiPrimaryKeyException(String fieldName) {
        super("multi MultiPrimaryKey Annotations --> " + fieldName);
    }
}

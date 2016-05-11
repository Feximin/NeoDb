package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/9.
 */
public class PrimaryKeyTypeException extends RuntimeException {
    public PrimaryKeyTypeException(String fieldName) {
        super("primary key type must be int or Integer !!" + fieldName);
    }
}

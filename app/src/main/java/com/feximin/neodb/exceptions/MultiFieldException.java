package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 * 多个相同的字段
 */
public class MultiFieldException extends RuntimeException {
    public MultiFieldException(String fieldName) {
        super("multi field exception  -- >" + fieldName);
    }
}

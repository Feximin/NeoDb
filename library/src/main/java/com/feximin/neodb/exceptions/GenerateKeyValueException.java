package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/7.
 */

public class GenerateKeyValueException extends RuntimeException {
    public GenerateKeyValueException() {
        super("generate kv pair error");
    }
}

package com.feximin.neodb.exceptions;

/**
 * Created by Neo on 16/1/8.
 */
public class IllegalFieldName extends RuntimeException {
    public IllegalFieldName() {
        super("you can not named a field 'id__' or 'cur_login_user_id__'");
    }
}

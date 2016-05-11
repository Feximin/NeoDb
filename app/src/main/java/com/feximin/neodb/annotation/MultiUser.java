package com.feximin.neodb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Neo on 16/5/10.
 * 表中的数据需要多用户系统的时候，每个表中多一个字段，表示当前用户的id
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiUser {
    String field() default "cur_login_user_id";
}

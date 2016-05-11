package com.feximin.neodb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Neo on 16/5/10.
 * 每个表都需要有一个主键，如果没有使用这个注解，则给表默认加一个主键
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {
}

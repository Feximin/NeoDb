package com.feximin.neodb.model;

/**
 * 约定:	1.每个存储到数据库的有效字段都是以双下划线（“__”）开头结尾
 *   	2.private或者default修饰符修饰的成员变量，子类生成的表中不包含这些字段
 *   	3.每个表都需要有一个主键，如果没有使用Primary注解，将会默认生成一个主键
 *   	4.存到数据库里每个字段的开始和结尾会加上双下划线
 *   是一个标示接口
 *   继承了这个接口的类都需要有一个无参构造
 * @author Neo
 * @time 2016年01月06日
 */
public interface Model {

}

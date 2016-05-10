package com.feximin.neodb.model;

/**
 * 约定:	1.以双下划线（“__”）结尾的成员变量是将不会添加到数据表中，
 *   	2.private或者default修饰符修饰的成员变量，子类生成的表中不包含这些字段
 *   	3.每个表默认已经添加了一个名为“p_k_id”的主键
 *   	4.存到数据库里每个字段的开始和结尾会加上双下划线
 *   是一个标示接口
 *   继承了这个接口的类都需要有一个无参构造
 * @author Neo
 * @time 2016年01月06日
 */
public interface Model {

}

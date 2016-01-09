package com.feximin.neodb.model;

/**
 * Created by Neo on 16/1/9.
 */
public abstract class FieldType {

    public final String dbMetaType;
    public final Object dbDefaultValue;
    public final Class<?> clazz;

    //如果defaultValue为null的话表示该列不需要设置默认值
    public FieldType(Class<?> clazz, String dbMetaType, Object dbDefaultValue) {
        this.clazz = clazz;
        this.dbMetaType = dbMetaType;
        this.dbDefaultValue = dbDefaultValue;
    }

    //向数据库存储的时候需要转化为数据库认识的类型		字符串
    public abstract String toDb(Object value);

    public abstract Object fromDb(String ori);

    public static String decorName(String name){
        return String.format("__%s__", name);
    }

    public static String cleanName(String name){
        return name.substring(2, name.length() - 2);
    }

    public String getDefaultSqlState(){
        if(dbDefaultValue == null){
            return "";
        }else{
            return String.format(" DEFAULT %s", dbDefaultValue);
        }
    }
}

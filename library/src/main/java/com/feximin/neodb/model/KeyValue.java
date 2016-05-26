package com.feximin.neodb.model;

import com.feximin.neodb.exceptions.GenerateKeyValueException;
import com.feximin.neodb.manager.FieldManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neo on 16/1/7.
 */
public class KeyValue {
    public String key;
    public String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static <T> List<KeyValue> getKVList(T t){
        Class<T> clazz = (Class<T>) t.getClass();
        List<FieldInfo> fieldInfoList = FieldManager.getFieldList(clazz);
        int size = fieldInfoList.size();
        List<KeyValue> kvList = new ArrayList<>(size);
        try {
            for(int i = 0; i<size; i++){
                FieldInfo info = fieldInfoList.get(i);
                String name = info.name;


                Field f = FieldManager.getField(clazz, name);

                if (f != null){
                    f.setAccessible(true);
                    Object value = f.get(t);

                    FieldType fieldType = info.fieldType;
                    if(value != null){
                        Class<?> valueClazz = fieldType.clazz;

                        String valueStr;
                        if(valueClazz == String.class
                                || valueClazz == Integer.class  || valueClazz == int.class
                                || valueClazz == Float.class    || valueClazz == float.class
                                || valueClazz == Double.class   || valueClazz == double.class
                                || valueClazz == Long.class     || valueClazz == long.class
                                || valueClazz == Short.class    || valueClazz == short.class
                                || valueClazz == Byte.class     || valueClazz == byte.class){

                            valueStr = String.valueOf(value);

                        }else if(valueClazz == Boolean.class || valueClazz == boolean.class){
                            boolean b = (boolean) value;
                            valueStr = String.valueOf(b?1:0);
                        }else{
                            valueStr = fieldType.toDb(value);
                        }
                        KeyValue kv = new KeyValue(name, valueStr);
                        kvList.add(kv);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw  new GenerateKeyValueException();
        }
        return kvList;
    }
}

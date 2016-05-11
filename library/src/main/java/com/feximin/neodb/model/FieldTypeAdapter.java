package com.feximin.neodb.model;

/**
 * Created by Neo on 16/1/9.
 */
public class FieldTypeAdapter extends FieldType {
    public FieldTypeAdapter(Class<?> clazz, String dbMetaType, Object dbDefaultValue) {
        super(clazz, dbMetaType, dbDefaultValue);
    }

    @Override
    public String toDb(Object value) {
        return null;
    }

    @Override
    public Object fromDb(String ori) {
        return null;
    }
}

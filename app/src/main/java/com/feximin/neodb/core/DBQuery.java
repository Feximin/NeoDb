package com.feximin.neodb.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feximin.neodb.exceptions.CursorToModelException;
import com.feximin.neodb.exceptions.InsertException;
import com.feximin.neodb.manager.FieldManager;
import com.feximin.neodb.manager.TableManager;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.FieldType;
import com.feximin.neodb.model.KeyValue;
import com.feximin.neodb.model.Model;
import com.feximin.neodb.utils.NeoUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Neo on 16/1/6.
 * 1. select使用的是rawQuery
 * 2. delete使用的是db的delete方法，需要whereClause
 *
 */
public class DBQuery<T extends Model> {

    protected Class<T> clazz;
    protected List<String> args;
    protected StringBuilder whereClause;
    protected String tableName;

    public DBQuery(Class<T> clazz){
        this.clazz = clazz;
        this.tableName = TableManager.getInstance().getTableName(clazz);
        this.args = new ArrayList<>();
        this.whereClause = new StringBuilder();
    }


    public DBQuery where(String field){
        this.whereClause.append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery gt(String condition){
        this.args.add(condition);
        this.whereClause.append(">? ");
        return this;
    }
    public DBQuery lt(String condition){
        this.args.add(condition);
        this.whereClause.append("<? ");
        return this;
    }
    public DBQuery eq(String condition){
        this.args.add(condition);
        this.whereClause.append("=? ");
        return this;
    }

    public DBQuery notEq(String condition){
        this.args.add(condition);
        this.whereClause.append("<>? ");
        return this;
    }

    public DBQuery and(String field){
        this.whereClause.append("AND ").append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery or(String field){
        this.whereClause.append("OR ").append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery lBracket(){
        this.whereClause.append("( ");
        return this;
    }
    public DBQuery rBracket(){
        this.whereClause.append(") ");
        return this;
    }
    public DBQuery orderBy(String condition){
        this.whereClause.append("ORDER BY ").append(condition).append(" ");
        return this;
    }
    public DBQuery limit(int from, int limit){
        this.whereClause.append("LIMIT ?,? ");
        this.args.add(String.valueOf(from));
        this.args.add(String.valueOf(limit));
        return this;
    }
    public DBQuery limit(int limit){
        limit(0, limit);
        return this;
    }
    public DBQuery desc(){
        this.whereClause.append("DESC ");
        return this;
    }
    public DBQuery asc(){
        this.whereClause.append("ASC ");
        return this;
    }
    public DBQuery my(){
        this.args.add(getMyUid());
        this.whereClause.append("AND ").append(FieldInfo.M_U_NAME).append("=? ");
        return this;
    }
    public String[] getArgs(){
        if(args.size() == 0) return null;
        String[] arg = args.toArray(new String[args.size()]);
        return arg;
    }

    public int count(){
        DBHelper helper = DBHelper.getInstance();
        StringBuilder state = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName).append(" ");
        String[] arg = getArgs();
        if(arg != null) state.append(whereClause);
        helper.execSelect(state.toString(), arg);
        Cursor cursor= helper.execSelect(state.toString(), arg);
        int count = 0;
        if(cursor != null && cursor.moveToFirst()){
            count= cursor.getInt(0);
            cursor.close();
        }
        helper.close();
        return count;
    }

    public String getMyUid(){
        return null;
    }

    public List<T> endSelect(String ...columns){

        StringBuilder startState;
        startState = new StringBuilder("SELECT ");
        if(columns == null || columns.length == 0){
            startState.append("*");
        }else{
            for(String co : columns){
                startState.append(FieldType.decorName(co)).append(",");
            }
            startState.deleteCharAt(startState.length() - 1);
        }

        String[] arg = getArgs();
        startState.append(" FROM ").append(tableName);
        if(arg != null){
            startState.append(" WHERE ").append(whereClause);
        }
        String sql = startState.toString();
        DBHelper helper = DBHelper.getInstance();
        List<T> result = cursorToModelList(helper.execSelect(sql, arg), clazz);
        helper.close();
        return result;
    }

    public int endUpdate(ContentValues values){
        if(values.size() == 0) return 0;
        values = decorContentValues(values);
        String[] arg = getArgs();
        DBHelper helper = DBHelper.getInstance();
        int affect = helper.getDb().update(tableName, values, whereClause.toString(), arg);
        helper.close();
        return affect;
    }

    public ContentValues decorContentValues(ContentValues values){
        ContentValues newValues = new ContentValues();
        for(Map.Entry<String, Object> item : values.valueSet()){
            String key = item.getKey();
            Object va = values.get(key);
            key = FieldType.decorName(key);
            if(va instanceof Integer){
                newValues.put(key, (Integer) va);
            }else if(va instanceof String){
                newValues.put(key, (String) va);
            }else if(va instanceof Long){
                newValues.put(key, (Long) va);
            }else if(va instanceof Double){
                newValues.put(key, (Double) va);
            }else if(va instanceof Float){
                newValues.put(key, (Float) va);
            }else if(va instanceof Short){
                newValues.put(key, (Short) va);
            }else if(va instanceof Byte){
                newValues.put(key, (Byte) va);
            }else if(va instanceof Boolean){
                newValues.put(key, (Boolean) va?1:0);
            }
        }
        return newValues;
    }


    public int endDelete(){
        String[] arg = getArgs();
        String where = whereClause.length() > 0?whereClause.toString():null;
        int affect = DBHelper.getInstance().execDelete(tableName, where, arg);
        return affect;
    }

    public void insert(T t){
        insertWithKVList(t, false);

    }


    public void insertMy(T t){
        insertWithKVList(t, true);
    }

    public void insert(List<T> list){
        insertList(list, false);
    }

    public void insertMy(List<T> list){
        insertList(list, true);
    }

    private void insertList(List<T> list, boolean my){
        if(NeoUtil.isEmpty(list)) return;
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getDb();
        db.beginTransaction();
        try {
            for(T t : list){
                if(my){
                    insertMy(t);
                }else{
                    insert(t);
                }
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            helper.close();
        }
    }


    private void insertWithKVList(T t, boolean my){
        List<KeyValue> kvList = KeyValue.getKVList(t, clazz);
        if(my) kvList.add(new KeyValue(FieldInfo.M_U_NAME, getMyUid()));
        StringBuilder state = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for(KeyValue kv : kvList){
            state.append(FieldType.decorName(kv.key)).append(",");
        }
        state.deleteCharAt(state.length() - 1);
        state.append(") VALUES ( ");
        for(KeyValue kv: kvList){
            state.append("'").append(kv.value).append("',");
        }
        state.deleteCharAt(state.length() - 1);
        state.append(")");

        DBHelper helper = DBHelper.getInstance();
        try {
            helper.getDb().execSQL(state.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InsertException();
        }finally {
            helper.close();
        }
    }


    public static class ColumnInfo{
        public int index;
        public FieldInfo fieldInfo;
    }

    public static <T extends Model> List<T> cursorToModelList(Cursor cursor, Class<T> clazz){
        if(cursor != null && cursor.moveToFirst()){
            List<FieldInfo> fields = FieldManager.getFieldList(clazz);
            try {
                Constructor constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                String[] columns = cursor.getColumnNames();

                int length;
                if(columns != null && (length = columns.length) > 0){
                    Map<String, ColumnInfo> typeClazzMap = new HashMap<>(length);

                    for(String name : columns){
                        ColumnInfo info = new ColumnInfo();
                        info.index = cursor.getColumnIndex(name);
                        name = FieldType.cleanName(name);
                        for(FieldInfo field : fields){
                            if(name.equals(field.name)){
                                info.fieldInfo = field;
                                typeClazzMap.put(name, info);
                                break;
                            }
                        }
                    }
                    List<T> list = new ArrayList<>(length);
                    do{
                        Object t = constructor.newInstance();
                        for(String co : typeClazzMap.keySet()){
                            if(FieldInfo.isReserveFieldName(co)) continue;
                            Field field = clazz.getDeclaredField(co);
                            field.setAccessible(true);
                            ColumnInfo info = typeClazzMap.get(co);
                            FieldType fieldType = info.fieldInfo.fieldType;
                            Class<?> typeClazz = fieldType.clazz;
                            int index = info.index;
                            if(typeClazz == String.class){
                                field.set(t, cursor.getString(info.index));
                            }else if(typeClazz == Integer.class || typeClazz == int.class){
                                field.setInt(t, cursor.getInt(index));
                            }else if(typeClazz == Float.class || typeClazz == float.class){
                                field.setFloat(t, cursor.getFloat(index));
                            }else if(typeClazz == Double.class || typeClazz == double.class){
                                field.setDouble(t, cursor.getDouble(index));
                            }else if(typeClazz == Long.class || typeClazz == long.class){
                                field.setLong(t, cursor.getLong(index));
                            }else if(typeClazz == Short.class || typeClazz == short.class){
                                field.setShort(t, cursor.getShort(index));
                            }else if(typeClazz == Byte.class || typeClazz == byte.class){
                                field.setByte(t, (byte) cursor.getInt(index));
                            }else if(typeClazz == Boolean.class || typeClazz == boolean.class){
                                field.setBoolean(t, cursor.getInt(index) == 1);
                            }else{
                                field.set(t, fieldType.fromDb(cursor.getString(info.index)));
                            }
                        }
                        list.add((T) t);
                    }while (cursor.moveToNext());
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new CursorToModelException();
            }finally {
                cursor.close();
            }
        }
        return null;
    }
}

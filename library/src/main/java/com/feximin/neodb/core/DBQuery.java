package com.feximin.neodb.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.feximin.neodb.exceptions.CursorToModelException;
import com.feximin.neodb.exceptions.NoEmptyConstructorException;
import com.feximin.neodb.manager.FieldManager;
import com.feximin.neodb.manager.TableManager;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.FieldType;
import com.feximin.neodb.model.KeyValue;
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
public class DBQuery<T> {

    protected Class<T> clazz;
    protected List<String> args;
    protected StringBuilder whereClause;
    protected String tableName;
    private DBConfig mDBConfig;
    private String orderBy;
    private String limit;
    private String having;
    private String groupBy;



    public static <T> DBQuery<T> obtain(Class<T> clazz){
        return new DBQuery<>(clazz);
    }

    public DBQuery<T> clear(){
        args.clear();
        whereClause.delete(0, whereClause.length());
        limit = null;
        orderBy = null;
        having = null;
        groupBy = null;
        return this;
    }

    private DBQuery(Class<T> clazz){
        this.clazz = clazz;
        this.tableName = TableManager.getTableName(clazz);
        this.args = new ArrayList<>();
        this.whereClause = new StringBuilder();
        this.mDBConfig = NeoDb.getInstance().getConfig();
    }

    public DBQuery<T> where(String field){
        this.whereClause.append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery<T> gt(String condition){
        this.args.add(condition);
        this.whereClause.append(">? ");
        return this;
    }
    public DBQuery<T> lt(String condition){
        this.args.add(condition);
        this.whereClause.append("<? ");
        return this;
    }
    public DBQuery<T> eq(String condition){
        this.args.add(condition);
        this.whereClause.append("=? ");
        return this;
    }

    public DBQuery<T> eq(Object condition){
        String cond = condition.toString();
        return eq(cond);
    }

    public DBQuery<T> eq(boolean b){
        String con = b?"1":"0";
        return eq(con);
    }

    public DBQuery<T> notEq(String condition){
        this.args.add(condition);
        this.whereClause.append("<>? ");
        return this;
    }

    public DBQuery<T> and(String field){
        this.whereClause.append("AND ").append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery<T> or(String field){
        this.whereClause.append("OR ").append(FieldType.decorName(field)).append(" ");
        return this;
    }

    public DBQuery<T> lBracket(){
        this.whereClause.append("( ");
        return this;
    }
    public DBQuery<T> rBracket(){
        this.whereClause.append(") ");
        return this;
    }
    public DBQuery<T> limit(int from, int limit){
//        this.whereClause.append(" LIMIT ?,? ");
//        this.args.add(String.valueOf(from));
//        this.args.add(String.valueOf(limit));
        this.limit = String.format(" %s,%s ", from, limit);
        return this;
    }
    public DBQuery<T> limit(int limit){
        limit(0, limit);
        return this;
    }
    public DBQuery<T> desc(String field){
//        this.whereClause.append("ORDER BY ").append(field).append("DESC ");
        this.orderBy = String.format("%s DESC", FieldType.decorName(field));
        return this;
    }
    public DBQuery<T> asc(String field){
//        this.whereClause.append("ORDER BY ").append(field).append("ASC ");
        this.orderBy = String.format("%s ASC", FieldType.decorName(field));
        return this;
    }
    public DBQuery<T> my(){
        FieldInfo multiUser = FieldManager.getMultiUserIdentifyFieldInfo(clazz, true);

        this.whereClause.append(args.size() > 0 ?"AND ":"").append(FieldType.decorName(multiUser.name)).append("=? ");

        this.args.add(mDBConfig.getUserIdFetcher().fetchUserId());
        return this;
    }

    public DBQuery<T> having(String have){
        return this;
    }

    public DBQuery<T> groupBy(String group){
        return this;
    }

    public String[] getArgs(){
        if(args.size() == 0) return null;
        String[] arg = args.toArray(new String[args.size()]);
        return arg;
    }

    public int count(){
        StringBuilder state = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName).append(" ");
        String[] arg = getArgs();
        if(arg != null){
            state.append(" WHERE ").append(whereClause);
        }
        NeoDb helper = NeoDb.getInstance();
        Cursor cursor= helper.rawQuery(state.toString(), arg);
        int count = 0;
        if(cursor != null){
            if (cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        helper.close();
        return count;
    }

    public List<T> endSelect(String ...columns){

//        StringBuilder sqlStatement = new StringBuilder("SELECT ");
//        if(columns == null || columns.length == 0){
//            columns = null;
//            sqlStatement.append("*");
//        }
//        else{
//            for(String co : columns){
//                sqlStatement.append(FieldType.decorName(co)).append(",");
//            }
//            sqlStatement.deleteCharAt(sqlStatement.length() - 1);
//        }

//        sqlStatement.append(" FROM ").append(tableName);

        String[] arg = getArgs();
//        if(arg != null){
//            whereClause.insert(0, " WHERE ");
//            sqlStatement.append(whereClause);
//        }
        NeoDb helper = NeoDb.getInstance();
        Cursor cursor = helper.getDb().query(tableName, columns, whereClause.toString(), arg, groupBy, having, orderBy, limit);
        List<T> result = cursorToModelList(cursor);
        helper.close();
        return result;
    }

    public T endSelectSingle(String... columns){
        List<T> list = endSelect(columns);
        if (list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }


    public int endUpdate(ContentValues values){
        if(values.size() == 0) return 0;
        values = decorContentValues(values);
        String[] arg = getArgs();
        NeoDb helper = NeoDb.getInstance();
        int affect = helper.getDb().update(tableName, values, whereClause.toString(), arg);
        helper.close();
        return affect;
    }

    public int endUpdate(T t){
        List<KeyValue> kvList = KeyValue.getKVList(t);
        ContentValues values = new ContentValues(kvList.size());
        for (KeyValue kv : kvList){
            values.put(kv.key, kv.value);
        }
        return endUpdate(values);
    }

    public ContentValues decorContentValues(ContentValues values){
        ContentValues newValues = new ContentValues();
        for(Map.Entry<String, Object> item : values.valueSet()){
            String key = item.getKey();
            Object va = item.getValue();
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
        int affect = NeoDb.getInstance().execDelete(tableName, where, arg);
        return affect;
    }

    public long insert(T t){
        return insertWithKVList(t, false);
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
        NeoDb helper = NeoDb.getInstance();
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


//    private void insertWithKVList(T t, boolean my){
//        List<KeyValue> kvList = KeyValue.getKVList(t);
//        if(my){
//            FieldInfo multiUser = FieldManager.getMultiUserIdentifyFieldInfo(clazz, true);
//            kvList.add(new KeyValue(multiUser.name, mDBConfig.getUserIdFetcher().fetchUserId()));
//        }
//        StringBuilder state = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
//        for(KeyValue kv : kvList){
//            state.append(FieldType.decorName(kv.key)).append(",");
//        }
//        state.deleteCharAt(state.length() - 1);
//        state.append(") VALUES ( ");
//        for(KeyValue kv: kvList){
//            state.append("'").append(kv.value).append("',");
//        }
//        state.deleteCharAt(state.length() - 1);
//        state.append(")");
//
//        try {
//            NeoDb.getInstance().execSQL(state.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new InsertException();
//        }
//    }

    private long insertWithKVList(T t, boolean my){
        List<KeyValue> kvList = KeyValue.getKVList(t);
        ContentValues values = new ContentValues();
        if(my){
            FieldInfo multiUser = FieldManager.getMultiUserIdentifyFieldInfo(clazz, true);
            kvList.add(new KeyValue(multiUser.name, mDBConfig.getUserIdFetcher().fetchUserId()));
        }
        for (KeyValue kv : kvList){
            values.put(FieldType.decorName(kv.key), kv.value);
        }

        return NeoDb.getInstance().insert(tableName, values);
    }


    public static class ColumnInfo{
        public int index;
        public FieldInfo fieldInfo;
    }

    public  List<T> cursorToModelList(Cursor cursor){
        if(cursor != null){
            if (cursor.moveToFirst()) {
                List<FieldInfo> fields = FieldManager.getFieldList(clazz);
                try {
                    Constructor constructor;
                    try {
                        constructor = clazz.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new NoEmptyConstructorException(clazz.getName());
                    }
                    constructor.setAccessible(true);
                    String[] columns = cursor.getColumnNames();

                    int length;
                    if (columns != null && (length = columns.length) > 0) {
                        Map<String, ColumnInfo> typeColumnMap = new HashMap<>(length);

                        for (String name : columns) {
                            ColumnInfo info = new ColumnInfo();
                            info.index = cursor.getColumnIndex(name);
                            name = FieldType.cleanName(name);
                            for (FieldInfo field : fields) {
                                if (name.equals(field.name)) {
                                    info.fieldInfo = field;
                                    typeColumnMap.put(name, info);
                                    break;
                                }
                            }
                        }
                        List<T> list = new ArrayList<>(length);
                        do {
                            Object t = constructor.newInstance();
                            for (String fieldName : typeColumnMap.keySet()) {
                                Field field = FieldManager.getField(clazz, fieldName);
                                if (field != null) {
                                    field.setAccessible(true);
                                    ColumnInfo info = typeColumnMap.get(fieldName);
                                    FieldType fieldType = info.fieldInfo.fieldType;
                                    Class<?> typeClazz = fieldType.clazz;
                                    int index = info.index;
                                    if (typeClazz == String.class) {
                                        field.set(t, cursor.getString(info.index));
                                    } else if (typeClazz == Integer.class || typeClazz == int.class) {
                                        field.setInt(t, cursor.getInt(index));
                                    } else if (typeClazz == Float.class || typeClazz == float.class) {
                                        field.setFloat(t, cursor.getFloat(index));
                                    } else if (typeClazz == Double.class || typeClazz == double.class) {
                                        field.setDouble(t, cursor.getDouble(index));
                                    } else if (typeClazz == Long.class || typeClazz == long.class) {
                                        field.setLong(t, cursor.getLong(index));
                                    } else if (typeClazz == Short.class || typeClazz == short.class) {
                                        field.setShort(t, cursor.getShort(index));
                                    } else if (typeClazz == Byte.class || typeClazz == byte.class) {
                                        field.setByte(t, (byte) cursor.getInt(index));
                                    } else if (typeClazz == Boolean.class || typeClazz == boolean.class) {
                                        field.setBoolean(t, cursor.getInt(index) == 1);
                                    } else {
                                        field.set(t, fieldType.fromDb(cursor.getString(info.index)));
                                    }
                                }
                            }
                            list.add((T) t);
                        } while (cursor.moveToNext());
                        return list;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CursorToModelException();
                }
            }
            cursor.close();
        }
        return null;
    }
}

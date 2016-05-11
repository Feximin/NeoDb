package com.feximin.neodb.manager;

import android.database.Cursor;

import com.feximin.neodb.core.DBHelper;
import com.feximin.neodb.model.TableInfo;
import com.feximin.neodb.exceptions.CreateTableFailedException;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.FieldType;
import com.feximin.neodb.model.Model;
import com.feximin.neodb.utils.SingletonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableManager  {
	
	private List<TableInfo> mTableList = new ArrayList<>();
	private TableManager(){
	}

	public static TableManager getInstance(){
		return SingletonUtil.getInstance(TableManager.class);
	}
	
	public void createTable(TableInfo en){
        String name = en.name;
		StringBuffer state = new StringBuffer("CREATE TABLE IF NOT EXISTS ").append(name).append(" (");
		for(FieldInfo entry : en.fieldList){
			FieldType type = entry.fieldType;
			state.append(FieldType.decorName(entry.name))
				.append(" ")
				.append(type.dbMetaType).append(type.getDefaultSqlState())
				.append(",");
		}
		state.deleteCharAt(state.length() - 1);
		state.append(")");
		try {
			DBHelper.getInstance().execSQL(state.toString());
		}catch (Exception e){
			e.printStackTrace();
			throw new CreateTableFailedException(name);
		}
		mTableList.add(en);
	}

	public TableInfo modelToTable( Class<? extends  Model> clazz){
		String name = getTableName(clazz);
		List<FieldInfo> mFieldsMap = FieldManager.getFieldList(clazz);
		TableInfo entity = new TableInfo(name, mFieldsMap);
		return entity;
	}

	private Pattern pattern = Pattern.compile("__\\w+__");
	private String QUERY_ALL_TABLE = "SELECT name, sql FROM sqlite_master WHERE type='table'";
	//获取所有的表，以及每个表中所有的字段，只需要知道字段名称，无需知道其他信息，type,typeClazz,
	public List<TableInfo> queryAllTable(){
        DBHelper helper = DBHelper.getInstance();
		Cursor cursor = helper.rawQuery(QUERY_ALL_TABLE);
		if (cursor != null){
            if (cursor.moveToFirst()){
                int index = cursor.getColumnIndex("name");
                int indexSql = cursor.getColumnIndex("sql");
                if(index > -1 && indexSql > -1){
                    do{
                        String sqlState = cursor.getString(indexSql);
                        Matcher matcher = pattern.matcher(sqlState);
                        List<String> columnNames = new ArrayList<>();
                        while(matcher.find()){
                            String str = matcher.group();

                            columnNames.add(FieldType.cleanName(str));
                        }
                        int size = columnNames.size();
                        if(size > 0) {
                            List<FieldInfo> fields = new ArrayList<>(size);
                            for (String cn : columnNames) {
                                fields.add(new FieldInfo(cn));
                            }
                            TableInfo table = new TableInfo();
                            String name = cursor.getString(index);
                            table.name = name;
                            table.fieldList = fields;
                            mTableList.add(table);
                        }
                    }while(cursor.moveToNext());
                }
            }
            cursor.close();
        }
        helper.close();
		return mTableList;
	}
	
    public String getTableName(Class<? extends Model > clazz){
		String name = "table_"+clazz.getSimpleName();
		return name;
	}


    private String ADD_FIELD = "ALTER TABLE %s ADD COLUMN %s %s";
    //添加一个字段
    public void addField(FieldInfo entity, String tableName){
		String name = FieldType.decorName(entity.name);
		FieldType type = entity.fieldType;
        String sql =  String.format(ADD_FIELD, tableName, name, type.dbMetaType + type.getDefaultSqlState());
        DBHelper.getInstance().execSQL(sql);
    }
	
	
}

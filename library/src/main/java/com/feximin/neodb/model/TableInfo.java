package com.feximin.neodb.model;

import com.feximin.neodb.manager.FieldManager;
import com.feximin.neodb.manager.TableManager;

import java.util.List;

/**
 *
 */
public class TableInfo {
	public String name;
	public List<FieldInfo> fieldList;
	public Class<? extends Model> clazz;

	public TableInfo(){}

	public TableInfo(Class<? extends Model> clazz) {
		this.clazz = clazz;
		this.fieldList = FieldManager.getFieldList(clazz);
		this.name = TableManager.getInstance().getTableName(clazz);
	}

	public TableInfo(String name, List<FieldInfo> list){
		this.name = name;
		this.fieldList = list;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableInfo tableInfo = (TableInfo) o;

        return !(name != null ? !name.equals(tableInfo.name) : tableInfo.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

package com.feximin.neodb.model;

import com.feximin.neodb.manager.FieldManager;

public class FieldInfo {
	public String name;
	public FieldType fieldType;

	public static FieldType MULTI_USER_FIELD_TYPE = new FieldTypeAdapter(String.class, "VARCHAR(10)", "''");
	public static FieldType PRIMARY_FIELD_TYPE = new FieldTypeAdapter(int.class, "INTEGER PRIMARY KEY AUTOINCREMENT", null);
	public static FieldInfo PRIMARY_FIELD_INFO = new FieldInfo("p_k_id", PRIMARY_FIELD_TYPE);

	public FieldInfo(){}


	public FieldInfo(String name, Class<?> typeClazz){
		this(name, FieldManager.getFieldType(typeClazz));
	}

	public FieldInfo(String name, FieldType tp){
		this.name = name;
		this.fieldType = tp;
	}

	public FieldInfo(String name){
		this.name = name;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FieldInfo info = (FieldInfo) o;

		return !(name != null ? !name.equals(info.name) : info.name != null);

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
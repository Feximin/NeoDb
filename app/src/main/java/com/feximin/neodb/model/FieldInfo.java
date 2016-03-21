package com.feximin.neodb.model;

import com.feximin.neodb.manager.FieldManager;

import java.util.ArrayList;
import java.util.List;

public class FieldInfo {
	public String name;
	public FieldType fieldType;

	public static String M_U_NAME = "cur_login_user_id";
	public static String M_U_TYPE = "VARCHAR(10)";
	public static String P_K_NAME = "p_k_id";
	public static String P_K_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";


	public static final List<FieldInfo> sReserveFieldInfoList = new ArrayList<>();

	static {
		sReserveFieldInfoList.add(new FieldInfo(M_U_NAME, new FieldTypeAdapter(String.class, M_U_TYPE, "''")));
		sReserveFieldInfoList.add(new FieldInfo(P_K_NAME, new FieldTypeAdapter(int.class, P_K_TYPE, null)));
	}
	public FieldInfo(){}

	public static boolean isReserveFieldName(String name){
		for(FieldInfo info : sReserveFieldInfoList){
			if(name.equals(info.name)){
				return true;
			}
		}
		return false;
	}

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
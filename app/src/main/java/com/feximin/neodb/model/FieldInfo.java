package com.feximin.neodb.model;

import com.feximin.neodb.manager.FieldManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldInfo {
	public String name;
	public FieldType fieldType;

//	public static String M_U_NAME = "cur_login_user_id";
//	public static String M_U_TYPE = "VARCHAR(10)";
//	public static String P_K_NAME = "p_k_id";
//	public static String P_K_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";

	public static FieldType MULTI_USER_FIELD_TYPE = new FieldTypeAdapter(String.class, "VARCHAR(10)", "''");
	public static FieldType PRIMARY_FIELD_TYPE = new FieldTypeAdapter(int.class, "INTEGER PRIMARY KEY AUTOINCREMENT", null);
	public static FieldInfo PRIMARY_FIELD_INFO = new FieldInfo("p_k_id", PRIMARY_FIELD_TYPE);



	public static final List<FieldInfo> sReserveFieldInfoList = new ArrayList<>();

//	static {
//		sReserveFieldInfoList.add(new FieldInfo(M_U_NAME, new FieldTypeAdapter(String.class, M_U_TYPE, "''")));
//		sReserveFieldInfoList.add(new FieldInfo(P_K_NAME, new FieldTypeAdapter(int.class, P_K_TYPE, null)));
//	}
	public FieldInfo(){}

//	public static boolean isReserveFieldName(String name){
//		for(FieldInfo info : sReserveFieldInfoList){
//			if(name.equals(info.name)){
//				return true;
//			}
//		}
//		return false;
//	}

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

	/**
	 public Field getDeclaredField(String name) // 获得该类自身声明的所有变量，不包括其父类的变量
	 public Field getField(String name) // 获得该类自所有的public成员变量，包括其父类变量
	 */
	public static Field getField(Class<? extends Model> clazz, String fieldName){
		Field field = null;
		Class<?> cl = clazz;
		do{
			try {
				field = clazz.getDeclaredField(fieldName);
				int modifier = field.getModifiers();
				//如果是父类的话只有public或者protected的变量有效
				boolean isAvailableModifier = cl == clazz || Modifier.isPublic(modifier) || Modifier.isProtected(modifier);
				if (isAvailableModifier) break;
			}catch (NoSuchFieldException e){
				e.printStackTrace();
			}
		}while((cl = cl.getSuperclass()) != Model.class);
		return field;
	}

	public static String getMultiUserFieldName(Class<? extends Model> clazz){
		List<FieldInfo> fieldInfoList = FieldManager.getFieldList(clazz);

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
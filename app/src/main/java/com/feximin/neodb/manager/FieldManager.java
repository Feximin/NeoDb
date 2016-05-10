package com.feximin.neodb.manager;

import com.feximin.neodb.annotation.NonField;
import com.feximin.neodb.exceptions.IllegalFieldName;
import com.feximin.neodb.exceptions.IllegalTypeException;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.FieldType;
import com.feximin.neodb.model.FieldTypeAdapter;
import com.feximin.neodb.model.Model;
import com.feximin.neodb.utils.NeoUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldManager {

	public static final Map<Class<? extends Model>, List<FieldInfo>> sModelFieldMaps = new HashMap<>();

	/**
	 * 需要存储到数据库的字段都有哪些
	 * @param clazz
	 * @return
     */
	public static synchronized List<FieldInfo> getFieldList(Class<? extends Model> clazz) {
		if(sModelFieldMaps.containsKey(clazz)){
			return sModelFieldMaps.get(clazz);
		}else{
			List<FieldInfo> list = new ArrayList<>();
			Class cl = clazz;
			do{
				Field[] fields = cl.getDeclaredFields();
				if(NeoUtil.isNotEmpty(fields)){
					for(Field f : fields){
						int modifier = f.getModifiers();
						//如果是父类的话只有public或者protected的变量有效
						boolean isAvailableModifier = cl == clazz || Modifier.isPublic(modifier) || Modifier.isProtected(modifier);
						if(isAvailableModifier){
							String name = f.getName();
							if(name.equals(FieldInfo.M_U_NAME) || name.equals(FieldInfo.P_K_NAME)){
								throw new IllegalFieldName();
							}
							if(!f.isAnnotationPresent(NonField.class) && !name.startsWith("$")){		//以$开头的是Object中
								FieldInfo entity = new FieldInfo(name, f.getType());
								list.add(entity);
							}
						}
					}
				}
			}while((cl = cl.getSuperclass()) != Model.class);
			list.addAll(FieldInfo.sReserveFieldInfoList);
			sModelFieldMaps.put(clazz, list);
			return list;
		}
	}

	public static final String INTEGER = "INTEGER";
	public static final String VARCHAR_10 = "VARCHAR(10)";

	private static final List<FieldType> sValidFieldType = new ArrayList<>();

	static {

		//所有的基础数据类型
		addFieldType(new FieldTypeAdapter(Long.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(long.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(Float.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(float.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(Double.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(double.class, VARCHAR_10, 0));
		addFieldType(new FieldTypeAdapter(Character.class, VARCHAR_10, "''"));
		addFieldType(new FieldTypeAdapter(char.class, VARCHAR_10, "''"));

		addFieldType(new FieldTypeAdapter(Byte.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(byte.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(Integer.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(int.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(Boolean.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(boolean.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(Short.class, INTEGER, 0));
		addFieldType(new FieldTypeAdapter(short.class, INTEGER, 0));

		addFieldType(new FieldType(String.class, VARCHAR_10, "''") {
			@Override
			public String toDb(Object value) {
				return String.valueOf(value);
			}

			@Override
			public String fromDb(String ori) {
				return ori;
			}
		});

	}

	public static void addFieldType(FieldType type){
			sValidFieldType.add(type);
	}

	public static FieldType getFieldType(Class<?> clazz){
		for(FieldType ty : sValidFieldType){
			if(ty.clazz == clazz){
				return ty;
			}
		}
		throw  new IllegalTypeException(clazz);
	}



}

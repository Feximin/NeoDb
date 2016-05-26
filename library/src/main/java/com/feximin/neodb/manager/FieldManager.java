package com.feximin.neodb.manager;

import com.feximin.neodb.annotation.Ignore;
import com.feximin.neodb.annotation.MultiUser;
import com.feximin.neodb.annotation.Primary;
import com.feximin.neodb.exceptions.IllegalTypeException;
import com.feximin.neodb.exceptions.MultiFieldException;
import com.feximin.neodb.exceptions.MultiMultiUserException;
import com.feximin.neodb.exceptions.MultiPrimaryKeyException;
import com.feximin.neodb.exceptions.NotMultiUserModeClassException;
import com.feximin.neodb.exceptions.PrimaryKeyTypeException;
import com.feximin.neodb.model.FieldInfo;
import com.feximin.neodb.model.FieldType;
import com.feximin.neodb.model.FieldTypeAdapter;
import com.feximin.neodb.utils.NeoUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldManager {

	public static final Map<Class<? >, List<FieldInfo>> sModelFieldMaps = new HashMap<>();
	public static final Map<Class<? >, FieldInfo> sMultiUserIdentifyFieldInfoMaps = new HashMap<>();


    public static FieldInfo getMultiUserIdentifyFieldInfo(Class<? > clazz, boolean...throwIfNotMultiUserMode){
        if (sMultiUserIdentifyFieldInfoMaps.containsKey(clazz)){
            return sMultiUserIdentifyFieldInfoMaps.get(clazz);
        }else {
            boolean isMultiUserMode = false;
            FieldInfo fieldInfo = null;
            Class cl = clazz;
            do {
                if (cl.isAnnotationPresent(MultiUser.class)) {
                    if (isMultiUserMode) throw new MultiMultiUserException();
                    MultiUser multiUser = (MultiUser) cl.getAnnotation(MultiUser.class);
                    fieldInfo = new FieldInfo(multiUser.field(), FieldInfo.MULTI_USER_FIELD_TYPE);
                    isMultiUserMode = true;
                }
            } while ((cl = cl.getSuperclass()) != Object.class);
            if (fieldInfo == null && throwIfNotMultiUserMode.length > 0 && throwIfNotMultiUserMode[0]) throw new NotMultiUserModeClassException(clazz);
            if (fieldInfo != null) sMultiUserIdentifyFieldInfoMaps.put(clazz, fieldInfo);
            return fieldInfo;
        }
    }

	/**
	 * 如果有MultiUser注解，则是多用户系统，需要多加一个字段来表示user_id
	 * 如果字段上有Ignore,表示不进行存储
	 * 如果字段上有Primary,表示主键
	 *
	 * 需要存储到数据库的字段都有哪些
	 * @param clazz
	 * @return
     */
	public static synchronized List<FieldInfo> getFieldList(Class<? > clazz) {
		if(sModelFieldMaps.containsKey(clazz)){
			return sModelFieldMaps.get(clazz);
		}else{
			List<FieldInfo> list = new ArrayList<>();
			Class cl = clazz;
			boolean hasPrimary = false;
            addFieldInfo(list, getMultiUserIdentifyFieldInfo(clazz));
            do{
				Field[] fields = cl.getDeclaredFields();
				if(NeoUtil.isNotEmpty(fields)){
					for(Field f : fields){
						if(isAvailableModifier(clazz, cl, f)){
							String name = f.getName();
							if(!f.isAnnotationPresent(Ignore.class) && !name.startsWith("$")){		//以$开头的是Object中
								if (f.isAnnotationPresent(Primary.class)){
									if (hasPrimary) throw  new MultiPrimaryKeyException(name);
									Class<?> typeClazz = f.getType();
                                    if (typeClazz != int.class && typeClazz != Integer.class){
                                        throw new PrimaryKeyTypeException(name);
                                    }
                                    addFieldInfo(list, new FieldInfo(name, FieldInfo.PRIMARY_FIELD_TYPE));
									hasPrimary = true;
								}else{
                                    addFieldInfo(list, new FieldInfo(name, f.getType()));
                                }
							}
						}
					}
				}
			}while((cl = cl.getSuperclass()) != Object.class);
            if (!hasPrimary){
                addFieldInfo(list, FieldInfo.PRIMARY_FIELD_INFO);
            }
			sModelFieldMaps.put(clazz, list);
			return list;
		}
	}

    //只有
    public static boolean isAvailableModifier(Class<?> realClazz, Class<?> superClazz, Field field){
        int modifier = field.getModifiers();
        //不能是final的也不能是static的，
        if (realClazz == superClazz){
            return !Modifier.isStatic(modifier) && !Modifier.isFinal(modifier);
        }else{          //如果是父类的话还必须是public或者protect的范围
            return !Modifier.isFinal(modifier) && !Modifier.isFinal(modifier) && (Modifier.isPublic(modifier) || Modifier.isProtected(modifier));
        }
    }

    /**
     public Field getDeclaredField(String name) // 获得该类自身声明的所有变量，不包括其父类的变量
     public Field getField(String name) // 获得该类自所有的public成员变量，包括其父类变量
     */
    public static Field getField(Class<? > clazz, String fieldName){
        Field field = null;
        Class<?> cl = clazz;
        do{
            try {
                field = clazz.getDeclaredField(fieldName);
                if (isAvailableModifier(clazz, cl, field)) break;
            }catch (NoSuchFieldException e){
                e.printStackTrace();
            }
        }while((cl = cl.getSuperclass()) != Object.class);
        return field;
    }

    public static void addFieldInfo(List<FieldInfo> list, FieldInfo info){
        if (info == null) return;
        if (list.contains(info)){
            throw new MultiFieldException(info.name);
        }
        list.add(info);
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

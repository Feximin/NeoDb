package com.feximin.neodb.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SingletonUtil {

	private static final Map<Class<?>, Object> INSTANCE_MAP = new HashMap<Class<?>, Object>();
	/**
	 * 必须有一个无参的构造函数
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <E> E getInstance(Class<E> clazz){
		if(INSTANCE_MAP.containsKey(clazz)){
			return (E) INSTANCE_MAP.get(clazz);
		}else{
			E instance = null;
			synchronized (clazz) {
				try {
					Constructor<E> constructor = clazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					instance = constructor.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				if(instance != null) INSTANCE_MAP.put(clazz, instance);
			}
			return instance;
		}
	}
}

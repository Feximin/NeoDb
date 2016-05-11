package com.feximin.neodb.utils;

import java.util.List;

/**
 * Created by Neo on 16/1/7.
 */
public class NeoUtil {

    public static <T>boolean isEmpty(List<T> list){
        return list == null || list.size() == 0;
    }

    public static <T> boolean isNotEmpty(List<T> list){
        return !isEmpty(list);
    }

    public static boolean isEmpty(Object[] list){
        return list == null || list.length == 0;
    }

    public static boolean isNotEmpty(Object[] list){
        return !isEmpty(list);
    }
}

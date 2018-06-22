package com.ehu.util;

import com.ehu.annotation.NoNeedConvert;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * bean工具类.
 *
 * @author demon
 * @Date 2016/5/11 9:40
 */
public class BeanUtils {

    /**
     * 将java bean转到map，map的key为给定的attrs
     *
     * @param obj   bean实例
     * @param clazz bean的class
     * @param attrs 需要转换的属性
     * @return 实体的属性map，key为属性名，value为属性值
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object obj, Class clazz, String[] attrs) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> resultMap = new HashMap<>();
        for (String attr : attrs) {
            Method method = clazz.getMethod(getMethodGetName(attr));
            resultMap.put(attr, method.invoke(obj));
        }
        return resultMap;
    }

    /**
     * bean转为map
     *
     * @param obj
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object obj, Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> resultMap = new HashMap<>();
        for (Field field : fields) {
            if (getMethodGetName(field.getName()).equalsIgnoreCase("getSerialVersionUID") || field.isAnnotationPresent(NoNeedConvert.class)) {
                continue;
            }
            Method method = clazz.getMethod(getMethodGetName(field.getName()));
            resultMap.put(field.getName(), method.invoke(obj));
        }
        return resultMap;
    }

    /**
     * 组装get方法名
     *
     * @param attrName
     * @return
     */
    public static String getMethodGetName(String attrName) {
        StringBuffer result = new StringBuffer("get");
        result.append(attrName.substring(0, 1).toUpperCase());
        result.append(attrName.substring(1, attrName.length()));
        return result.toString();
    }

    /**
     * 组装set方法名
     *
     * @param attrName
     * @return
     */
    public static String getMethodSetName(String attrName) {
        StringBuffer result = new StringBuffer("set");
        result.append(attrName.substring(0, 1).toUpperCase());
        result.append(attrName.substring(1, attrName.length()));
        return result.toString();
    }

    /**
     * 复制属性值
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (getMethodGetName(field.getName()).equalsIgnoreCase("getSerialVersionUID")) {
                continue;
            }
            try {
                Method getMethod = source.getClass().getMethod(getMethodGetName(field.getName()));
                Method setMethod = target.getClass().getMethod(getMethodSetName(field.getName()), field.getType());
                setMethod.invoke(target, getMethod.invoke(source));
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 获取复制的实体
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDeepCopy(Object obj) {
        T result = null;
        try {
            result = (T) obj.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        copyProperties(obj, result);
        return result;
    }

    /**
     * 调用指定属性的set方法
     *
     * @param clazz
     * @param field
     * @param obj
     * @param args
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static void invokeSet(Class clazz, Field field, Object obj, Object... args) {
        Method method = null;
        try {
            method = clazz.getMethod(getMethodSetName(field.getName()), field.getType());
            method.invoke(obj, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用get方法
     *
     * @param clazz
     * @param source
     * @param fieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object invokeGet(Class clazz, Object source, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        Object result = null;
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                try {
                    Method getMethod = clazz.getMethod(getMethodGetName(field.getName()));
                    result = getMethod.invoke(source);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

//    /**
//     * Object数组转bean
//     *
//     * @param objArr
//     * @param t
//     * @param <T>
//     * @return
//     */
//    public static <T> T objArrToBean(Object[] objArr, Class<T> t) {
//        T result = null;
//        try {
//            result = t.newInstance();
//            Field[] fields = t.getDeclaredFields();
//            int i = 0;
//            for (Field field : fields) {
//                if (!field.isAnnotationPresent(NoNeedConvert.class)) {
//                    invokeSet(t, field, result, objArr[i]);
//                    i++;
//                }
//            }
//        } catch (InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

//    /**
//     * list<Object[]>转成list<T>
//     *
//     * @param objList
//     * @param t
//     * @param <T>
//     * @return
//     */
//    public static <T> List<T> listObjToListBean(List<Object[]> objList, Class<T> t) {
//        List<T> resultList = new ArrayList<>();
//        for (Object[] objArr : objList) {
//            T bean = objArrToBean(objArr, t);
//            resultList.add(bean);
//        }
//        return resultList;
//    }

//    /**
//     * 设置bean的default属性，主要用于添加操作
//     *
//     * @param object
//     * @param clazz
//     */
//    public static void setAddDefaultValue(Object object, Class clazz) {
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            String fieldName = field.getName();
//            if (fieldName.equals("createTime")) {
//                invokeSet(clazz, field, object, new Date());
//            } else if (fieldName.equals("createBy")) {
//                invokeSet(clazz, field, object, String.valueOf(BaseConstants.CURRENT_MERCHANT.get().getSmiid()));
//            } else if (fieldName.equals("updateTime")) {
//                invokeSet(clazz, field, object, new Date());
//            } else if (fieldName.equals("updateBy")) {
//                invokeSet(clazz, field, object, String.valueOf(BaseConstants.CURRENT_MERCHANT.get().getSmiid()));
//            } else {
//                // do nothing or do something else to set the default value
//            }
//        }
//    }
//
//    /**
//     * 设置bean的default属性，主要用于修改操作
//     *
//     * @param object
//     * @param clazz
//     */
//    public static void setUpdateDefaultValue(Object object, Class clazz) {
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            String fieldName = field.getName();
//            if (fieldName.equals("updateTime")) {
//                invokeSet(clazz, field, object, new Date());
//            } else if (fieldName.equals("updateBy")) {
//                invokeSet(clazz, field, object, String.valueOf(BaseConstants.CURRENT_MERCHANT.get().getSmiid()));
//            } else {
//                // do nothing or do something else to set the default value
//            }
//        }
//    }

    /**
     * 抽离list中的共同属性组合成一个bean has a list的结构
     *
     * @param metaList 数据库查询出来的待处理的List
     * @param clazz    最终结果集中元素的Class
     * @param sonAttrs 子列表的属性数组
     * @param flag     用于标识2条数据是否相等的标识字段，结果bean需要Override此字段的equals方法
     * @param <X>      最终结果集中元素的Class
     * @param <Y>      数据库查询出来的元素的Class
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static <X, Y> List<X> retriveCommonAttrsList(List<Y> metaList, Class<X> clazz, String[] sonAttrs, String flag, String sonName) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        // 对查询出的数据进行处理，组合成bean has a list的结构，因为是一条sql查询出来的
        // 这里如果不用copyOnWriteArrayList会出现线程异常的错误
        List<X> resultList = Lists.newCopyOnWriteArrayList();
        // 遍历查询出来的数据
        for (Y y : metaList) {
            if (resultList.size() == 0 && metaList.size() != 0) {
                X x = clazz.newInstance();
                org.springframework.beans.BeanUtils.copyProperties(y, x);
                resultList.add(x);
            }
            // 遍历准结果数据列表，抽离共通属性
            for (Iterator<X> iterator = resultList.iterator(); iterator.hasNext(); ) {
                X x = iterator.next();
                List<Map<String, Object>> sonList = (List<Map<String, Object>>) invokeGet(clazz, x, sonName);
                // 如果结果集中已经包含此条信息，则将相应的子数据添加到list中
                if (invokeGet(clazz, x, flag) == invokeGet(y.getClass(), y, flag)) {
                    // 添加子集到子集list中
                    Map<String, Object> sonMap = Maps.newHashMap();
                    for (String attr : sonAttrs) {
                        sonMap.put(attr, invokeGet(y.getClass(), y, attr));
                    }
                    if (!hasAllNullValues(sonMap)) {
                        sonList.add(sonMap);
                    }
                } else {
                    // 如果结果集中不包含此条信息，则添加进去
                    X newX = clazz.newInstance();
                    org.springframework.beans.BeanUtils.copyProperties(y, newX);
                    if (!resultList.contains(newX)) {
                        // 添加子集到子集list中
                        List<Map<String, Object>> tempSonList = (List<Map<String, Object>>) invokeGet(clazz, newX, sonName);
                        Map<String, Object> sonMap = Maps.newHashMap();
                        for (String attr : sonAttrs) {
                            sonMap.put(attr, invokeGet(y.getClass(), y, attr));
                        }
                        if (!hasAllNullValues(sonMap)) {
                            tempSonList.add(sonMap);
                        }
                        resultList.add(newX);
                        break;
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 判断map中的value是否都为空
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean hasAllNullValues(Map<K, V> map) {
        return Collections.frequency(map.values(), null) == map.size();
    }
}

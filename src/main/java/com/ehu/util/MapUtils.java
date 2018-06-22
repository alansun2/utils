package com.ehu.util;

import ch.hsr.geohash.GeoHash;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {
    private static final int numberOfCharacters = 5;

    public static String getGeoHashStr(double lon, double lat) {
        return GeoHash.geoHashStringWithCharacterPrecision(lat, lon, numberOfCharacters);
    }

    /**
     * 获取周围的地图区块
     *
     * @param geoHashStr
     * @return
     */
    public static String[] getAroundGeoHash(String geoHashStr) {
        GeoHash geoHash = GeoHash.fromGeohashString(geoHashStr);
        GeoHash[] gList = geoHash.getAdjacent();
        String[] sList = new String[gList.length + 1];
        for (int i = 0; i < gList.length; i++) {
            sList[i] = gList[i].toBase32();
        }
        sList[8] = geoHashStr;
        return sList;
    }

    /**
     * 获取周围的地图区块
     *
     * @param geoHashStr
     * @return
     */
    public static List<String> getAroundGeoHashReturnList(String geoHashStr) {
        GeoHash geoHash = GeoHash.fromGeohashString(geoHashStr);
        GeoHash[] gList = geoHash.getAdjacent();
        List<String> sList = new ArrayList<>();
        for (int i = 0; i < gList.length; i++) {
            sList.add(gList[i].toBase32());
        }
        sList.add(geoHashStr);
        return sList;
    }

    private static double EARTH_RADIUS = 6371.393;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算两个经纬度之间的距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> list2Map(List<V> list, Class<V> c, String... keyMethodNames) {
        Map<K, V> map = new HashMap<>();

        if (list != null) {
            try {
                int length = keyMethodNames.length;
                Method[] methods = new Method[length];
                for (int i = 0; i < length; i++) {
                    String keyMethodName = keyMethodNames[i];
                    methods[i] = c.getMethod(keyMethodName);
                }

                for (int i = 0; i < list.size(); i++) {
                    V value = list.get(i);
                    K key = null;
                    for (Method method : methods) {
                        if (key == null) {
                            key = (K) method.invoke(value);
                        } else {
                            key = (K) (key + "" + method.invoke(value));
                        }
                    }
                    map.put(key, value);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("field can't match the key!");
            }
        }

        return map;
    }

    /**
     * 使用reflect(反射)进行转换
     *
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object map2Bean(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();//新实例

        try {
            BeanUtils.populate(obj, map);
        } catch (Exception e) {
            System.out.println("transMap2Bean2 Error " + e);
        }

        return obj;
    }

    /**
     * bean转化为map
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }
}

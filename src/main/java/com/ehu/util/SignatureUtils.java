package com.ehu.util;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author alan
 * @createtime 18-1-9 * 验证
 */
@Slf4j
public class SignatureUtils {
    private static String key = "qafa2341234sdfsfafasfa";

    public static String getSign(Map<String, Object> map) {
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!Objects.equals(entry.getValue(), "") && !entry.getKey().equals("sign")) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }

        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; ++i) {
            sb.append(arrayToSort[i]);
        }

        String result = sb.toString();
        result = result + "key=" + key;
        result = MathUtils.md5Encryption(result).toUpperCase();
        return result;
    }
}

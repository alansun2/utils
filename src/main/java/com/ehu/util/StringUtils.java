package com.ehu.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

    /**
     * 字符串是否为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String... string) {
        for (String str : string) {
            if (str == null || "".equals(str.trim()) || str.equals("null"))
                return true;
        }
        return false;
    }

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim()) || str.equals("null"))
            return true;
        return false;
    }

    /**
     * 字符串是否为空
     * @param string
     * @return
     */
    public static boolean isBlank(String string){
        if (string == null || "".equals(string.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 取的UUID生成的随机数
     *
     * @return
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, uuid.indexOf("-"));
    }

    /**
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return 返回文件类型
     */
    public static String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 比较两个字符串的长度。1 ： str1 > str2 ; 0: str1 = str2; -1 : str1 < str2
     *
     * @param str1 1
     * @param str2 2
     * @return 1 ： str1 > str2 ; 0: str1 = str2; -1 : str1 < str2
     */
    public static int compareStringLength(String str1, String str2) {
        int c = str1.compareTo(str2);
        if (c == 0) {
            return 0;
        } else if (c > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 去除字符串中的全部空格
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        return str.replaceAll("\\ ", "");
    }

    /**
     * 匹配字符串
     *
     * @param str
     * @param regex
     * @return
     */
    public static boolean matchPattern(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }

    /**
     * @param str
     * @param regex 正则表达式
     * @return
     */
    public static List<String> getRegexMatchStr(String regex, String str) {
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(str);
        List<String> regexStr = new ArrayList<>();
        while (matcher.find()) {
            regexStr.add(matcher.group(0));
        }
        return regexStr;
    }

    /**
     * 获取当前登录用户ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = "";
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }catch (Exception e){
        }
        return ip;
    }

    /**
     * 判断是否包含数组中的字符串
     *
     * @param source
     * @param urls
     * @return
     */
    public static boolean contains(String source, String[] urls) {
        for (String url : urls) {
            if (source.contains(url)) {
                return true;
            }
        }

        return false;
    }
}

package com.ehu.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

/**
 * 日期处理工具类
 *
 * @author chenlong 2015-12-17
 */
public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_SHORT_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String DATE_SHORT_FORMAT_ = "yyyy.MM.dd HH:mm";

    public static final String TOMORROW_FORMAT = "MM月dd日";

    public static final String HOUR_MINUTES_FORMAT = "HHmm";

    public static final String DATE_SHORT = "yyyy-MM-dd";

    public static final String DATE_LONG_FORMAT = "yyyyMMddHHmmssSSS";

    public static final String SECOND_PATTERN = "MM-dd HH:mm";

    public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";

    public static final String DATE_DAY_FORMAT = "yyyy-MM-dd";

    public static final String DATE_DAY_FORMAT2 = "yyyyMMdd";

    public static final String HOUR_MIN_SEC = "HH:mm:ss";

    public static final String HOUR_MIN = "HH:mm";

    public static final String SECOND = "SECOND";

    /**
     * 获取指定格式的当前时间
     *
     * @param format 指定格式
     * @return
     */
    public static String getNow(final String format) {
        try {
            return formatDate(new Date(), format);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前的小时分钟数
     *
     * @return
     */
    public static int getTimeByHourMinute() {
        Date today = new Date();
        SimpleDateFormat f = new SimpleDateFormat(HOUR_MINUTES_FORMAT);
        return Integer.parseInt(f.format(today));
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDate() {
        Date today = new Date();
        SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
        return f.format(today);
    }

    /**
     * 根据时间戳转换时间类型
     *
     * @param time
     * @return
     */
    public static Date getDateByTime(int time) {
        return new Date((long) time * 1000L);
    }

    /**
     * 获取当前时间的10位时间戳
     *
     * @return
     */
    public static int getCurrentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static Date getDate(Date date, int day) {
        return (Date) getDate(date, day, null, 0);
    }

    public static String getDate(Date date, int day, String dateFormat) {
        return (String) getDate(date, day, dateFormat, 0);
    }

    /**
     * 获取明天的日期 格式为（xx月xx日）
     * 负数表示 往前推 day天
     * 正数表示 往后推 day天
     *
     * @return
     */
    public static Object getDate(Date date, int day, String dateFormat, int e) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        // 把日期往后增 加一天.整数往后推,负数往前移动
        calendar.add(Calendar.DATE, day);

        // 这个时间就是日期往后推一天的结果
        date = calendar.getTime();
        if (dateFormat != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            return formatter.format(date);
        } else {
            return date;
        }
    }

    /**
     * 是否在运营期
     *
     * @return
     */
    public static boolean isOperationPeriod() {
        Calendar cal = Calendar.getInstance();

        //当前时间
        long currentTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 5); //设置凌晨5点
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 21); //设置晚上21点
        long endTime = cal.getTimeInMillis();

        return startTime < currentTime && currentTime < endTime;
    }

    /**
     * 将Date类型日期格式化为字符串
     *
     * @param date   Date类型日期
     * @param format 目标格式
     * @return String
     */
    public static String formatDate(final Date date, final String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(date);
    }

    /**
     * 获取以当天为准的指定日期的开始时间 ， 0表示当前 -1表示昨天
     *
     * @return
     */
    public static int getStartTimeByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, day * 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取今日所剩时间
     *
     * @return
     */
    public static int getTodayLeft() {
        DateTime dateTime = DateTime.now();
        return Seconds.secondsBetween(dateTime, dateTime.millisOfDay().withMaximumValue()).getSeconds();
    }

    /**
     * 获取时间差
     *
     * @param date1
     * @param date2
     * @param pattern
     * @return
     */
    public static Object getDifTime(DateTime date1, DateTime date2, String pattern) {
        if (pattern.equals(SECOND)) {
            return Seconds.secondsBetween(date1, date2).getSeconds();
        }
        return null;
    }

    /**
     * 获取时间差
     *
     * @param date1
     * @param date2
     * @param pattern
     * @return
     */
    public static Object getDifTime(Date date1, Date date2, String pattern) {
        DateTime dateTime1 = new DateTime(date1);
        DateTime dateTime2 = new DateTime(date2);
        if (pattern.equals(SECOND)) {
            return Seconds.secondsBetween(dateTime1, dateTime2).getSeconds();
        }
        return null;
    }

    /**
     * 将字符串格式化为Date类型
     *
     * @param date   日期字符串
     * @param format 日期的格式
     * @return Date
     * @throws Exception
     */
    public static Date parseDate(final String date, final String format)
            throws Exception {
        if (date == null || date.trim() == "") {
            return null;
        }

        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.parse(date);
    }

    /**
     * 获取今天之前的日期
     *
     * @param minus
     * @return
     */
    public static Date getDayBeforeNow(int minus) {
        DateTime result = DateTime.now().minusDays(minus);
        return result.toDate();
    }

    /**
     * 判断是否是同一天
     * 比较date2 - diff == date1
     *
     * @param date1
     * @param date2
     * @param diff
     * @return
     */
    public static boolean isSameDay(DateTime date1, DateTime date2, int diff) {
        return date1.toString(DATE_SHORT).equals(date2.minusDays(diff).toString(DATE_SHORT));
    }

}

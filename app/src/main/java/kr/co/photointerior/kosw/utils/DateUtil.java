package kr.co.photointerior.kosw.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.rest.model.ActivityPeriod;

/**
 * Created by kugie.
 * 2018. 04. 30.
 */
public class DateUtil {
    public final static String DATE_FORMAT_DEFAULT = "yyyyMMdd";
    public final static String DATE_FORMAT_DEFAULT_WITH_DOT = "yyyy.MM.dd";
    /**
     * Returns a list of week.
     * @param weekAmount amount of week count
     * @return
     */
    public static List<ActivityPeriod> createWeeklyPeriod(int weekAmount){
        List<ActivityPeriod> list = new ArrayList<>();
        Calendar cal = DateUtil.getCalendar();
        for( int i = 0; i < weekAmount; i++ ){
            ActivityPeriod ap = new ActivityPeriod();
            ap.setBaseDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
            ap.setStepValue(String.valueOf(cal.get(Calendar.WEEK_OF_MONTH)));
            ap.setType(PeriodType.WEEKLY);

            setStartAndEndDateOfWeek((Calendar)cal.clone(), ap);

            cal.add(Calendar.WEEK_OF_YEAR, -1);
            ap.pickupVisibleString();
            list.add(ap);
        }
        return list;
    }

    public static void  setStartAndEndDateOfWeek(Calendar cal, ActivityPeriod ap){
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        ap.setStartDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        ap.setEndDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
    }

    /**
     * Returns a list of month.
     * @param year year amount
     * @return
     */
    public static List<ActivityPeriod> createMonthlyPeriod(int year){
        List<ActivityPeriod> list = new ArrayList<>();
        int limit = year * 12;
        Calendar cal = DateUtil.getCalendar();
        for( int i = 0; i < limit; i++ ){
            ActivityPeriod ap = new ActivityPeriod();
            ap.setBaseDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
            ap.setType(PeriodType.MONTHLY);
            ap.setStepValue(String.valueOf(cal.get(Calendar.MONTH)+1));
            setStartAndEndDateOfMonth((Calendar)cal.clone(), ap);
            cal.add(Calendar.MONTH, -1);
            ap.pickupVisibleString();
            list.add(ap);
        }
        return list;
    }

    public static void  setStartAndEndDateOfMonth(Calendar cal, ActivityPeriod ap){
        cal.set(Calendar.DAY_OF_MONTH, 1);
        ap.setStartDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        ap.setEndDate(DateUtil.formatDate(cal.getTime(), DATE_FORMAT_DEFAULT));
    }

    /**
     * Shift calendar field amount by given amount.
     * @param date
     * @param calendarField
     * @param amount
     * @param format
     * @param locale
     * @return
     */
    public static String shift(Date date, int calendarField, int amount, String format, Locale locale){
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        calendar.add(calendarField, amount);
        return formatDate(calendar.getTime(), format);
    }

    /**
     * Shift calendar field amount by given amount.
     * @param date
     * @param calendarField
     * @param amount
     * @param locale
     * @return
     */
    public static Date shift(Date date, int calendarField, int amount, Locale locale){
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        calendar.add(calendarField, amount);
        return calendar.getTime();
    }
    /**
     * Returns formatted date string with given {@link Date}.
     * @param date format target date
     * @return formatted string.
     */
    public static String formatDateYMDHms(Date date) {
        return new SimpleDateFormat(DateFormat.FORMAT_DEFAULT.getFormat(), Locale.US).format(date);
    }

    /**
     * Check whether the specific date and time is within the specific period.
     * All datetime should be has same format.
     * @param compareTime datetime string
     * @param compareTimeFormat format of datetime
     * @param startTime start datetime string
     * @param endTime end datetime string.
     * @param format datetime format
     * @return true-The datetime is in the interval.
     */
    public static boolean isExistViaTerms(
            String compareTime, String compareTimeFormat,
            String startTime, String endTime, String format){
        long ct = getTime(compareTimeFormat, compareTime);

        Calendar c = getCalendar(getDate(format, startTime));
        long start = c.getTimeInMillis();

        Calendar e = getCalendar(getDate(format, endTime));
        long end = e.getTimeInMillis();
        return start <= ct && ct <= end;
    }
    /**
     * Returns the time of day as long in 1/1000 second.
     * @param oldFormat datetime format for parsing.
     * @param dateString datetime
     * @return long
     */
    public static long getTime(
            String oldFormat,
            String dateString) {
        return getDate(oldFormat, dateString).getTime();
    }
    /**
     * Returns the Date object with a date in a specific format.
     * @param format
     * @param date
     * @return
     */
    public static Date getDate( String format, String date ){
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = sf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
    /**
     * It takes a Date object and sets it to Calendar object.
     * @param date
     * @return
     */
    public static Calendar getCalendar(Date date) {
        Calendar c = Calendar.getInstance(Locale.KOREA);
        c.setTime(date);
        return c;
    }

    /**
     * Returns base Calendar's instance.
     * @return
     */
    public static Calendar getCalendar(){
        return Calendar.getInstance();
    }

    /**
     * Formats the current datetime of the System and returns it.
     * If format is null or ("") "yyyyMMddHHmmss" is use as the default format.
     * @param format
     * @return
     */
    public static String currentDate( String format ){
        if( StringUtil.isEmptyOrWhiteSpace( format ) ){
            format = "yyyyMMddHHmmss";
        }
        return new SimpleDateFormat( format ).format(new Date());
    }

    public static String formatDate(Date date, String format) {
        if (date == null)
            return "1970-01-01 00:00:00";
        return new SimpleDateFormat(format).format(date);
    }
    /**
     * Checks whether the value is older than the current datetime.
     * Checks by 00:00:00 of date.
     * @param format
     * @param dateText
     * @return
     */
    public static boolean isPassedDate(String format, String dateText){
        Date d = getDate(format, currentDate(format));
        Calendar cc = getCalendar(d);

        cc.set(Calendar.HOUR, 0);
        cc.set(Calendar.MINUTE, 0);
        cc.set(Calendar.MILLISECOND, 0);
        Calendar sc = getCalendar(getDate(format, dateText));
        sc.set(Calendar.HOUR, 0);
        sc.set(Calendar.MINUTE, 0);
        sc.set(Calendar.MILLISECOND, 0);
        return sc.getTimeInMillis() < cc.getTimeInMillis();
    }

    /**
     * Converts a specific date format to another date format ans returns it.
     *@param oldFormat
     *@param dateString
     *@param newFormat
     *@return
     *@exception ParseException
     */
    public static String formatDate(
            String oldFormat,
            String dateString,
            String newFormat) {
        if(StringUtil.isEmpty(dateString)){
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat(oldFormat);
        SimpleDateFormat sd = new SimpleDateFormat(newFormat);
        String rtValue = "";
        try {
            Date d = sf.parse(dateString);
            rtValue = sd.format(d);
        } catch (ParseException e) {e.printStackTrace();}
        return rtValue;
    }

    public static final String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date convertToDate(String dateString) {
        Date d = null;
        try {
            d = new SimpleDateFormat(DATA_FORMAT, Locale.US).parse(dateString);
        } catch (ParseException e) {
        }
        return d;
    }
    public static String convertToDateString(Date date) {
        return new SimpleDateFormat(DATA_FORMAT, Locale.US).format(date);
    }

//    public static Date getFirstDateOfWeek(Locale locale){
//        return getFirstOrLastDateOfWeek(
//                new Date(System.currentTimeMillis()), FIRST_DATE_OF_WEEK, locale);
//    }

//    public static final int FIRST_DATE_OF_WEEK = 1;
//
//    public static final int LAST_DATE_OF_WEEK = 2;

//    public static Date getFirstOrLastDateOfWeek(Date date, int field, Locale locale){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.setFirstDayOfWeek(Calendar.MONDAY);
//        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
//        calendar.add(Calendar.DAY_OF_MONTH, -(weekDay-1));
//        if( field == LAST_DATE_OF_WEEK){
//            calendar.add(Calendar.DAY_OF_MONTH, 6);
//        }
//        return calendar.getTime();
//    }

    /**
     * week type num.
     */
    public enum DayType {
        FIRST_DAY_OF_WEEK,//start day of week
        LAST_DAY_OF_WEEK,//end day of week
        DAY,
        WEEK,
        MONTH,
        FIRST_DAY_OF_MONTH,
        LAST_DAY_OF_MONTH,
        YEAR,
        MONTH_OF_YEAR
    }

    /**
     * Returns first or last day of week.
     * @param date {@link Date}
     * @param startDay start day of week to sets.(ex:Calendar.MONDAY)
     * @param dayType Week start type. {@link DayType}
     * @param locale {@link Locale}
     * @return Date instance based on {@link DayType}.
     */
    public static Date getFirstOrLastDateOfWeek(Date date, int startDay, DayType dayType, Locale locale){
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(startDay);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, -(weekDay-1));
        if( DayType.LAST_DAY_OF_WEEK.equals(dayType)){
            calendar.add(Calendar.DAY_OF_MONTH, 6);
        }
        return calendar.getTime();
    }

    /**
     * Returns first or last day of week.
     * @param date {@link Date}
     * @param startDay start day of week to sets.(ex:Calendar.MONDAY)
     * @param dayType Week start type. {@link DayType}
     * @param locale {@link Locale}
     * @return Formatted string based on given format.
     */
    public static String getFirstOrLastDayOfWeek(Date date, int startDay, DayType dayType, String format, Locale locale){
        return format(getFirstOrLastDateOfWeek(date, startDay, dayType, locale), format, locale);
    }

    /**
     * Returns first day of week based on current date.
     * @param startDay start day of week to sets.(ex:Calendar.MONDAY)
     * @param dayType Week start type. {@link DayType}
     * @param format date format string
     * @param locale {@link Locale}
     * @return formatted string based on given format
     */
    public static String getFirstDayOfWeek(int startDay, DayType dayType, String format, Locale locale) {
        return getFirstOrLastDayOfWeek(new Date(System.currentTimeMillis()), startDay, dayType, format, locale);
    }
    /**
     * Returns last day of week based on current date.
     * @param startDay start day of week to sets.(ex:Calendar.MONDAY)
     * @param dayType Week start type. {@link DayType}
     * @param format date format string
     * @param locale {@link Locale}
     * @return formatted string based on given format
     */
    public static String getLastDayOfWeek(int startDay, DayType dayType, String format, Locale locale) {
        return getFirstOrLastDayOfWeek(new Date(System.currentTimeMillis()), startDay, dayType, format, locale);
    }

    /**
     * Returns first day of month.
     * @param date base date
     * @param format date format
     * @param locale {@link Locale}
     * @return formatted day string based on given {@link Date} and format.
     */
    public static String getFirstDayOfMonth(Date date, String format, Locale locale){
        Calendar c  = Calendar.getInstance(locale);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return format(c.getTime(), format, locale);
    }

    /**
     * Returns first {@link Date} of month based on given {@link Date}.
     * @param date
     * @param locale
     * @return
     */
    public static Date getFirstDayOfMonth(Date date, Locale locale){
        Calendar c  = Calendar.getInstance(locale);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }
    /**
     * Returns first day of month.
     * @param format date format
     * @param locale {@link Locale}
     * @return formatted day string based on current date and format.
     */
    public static String getFirstDayOfMonth(String format, Locale locale) {
        return getFirstDayOfMonth(new Date(System.currentTimeMillis()), format, locale);
    }
    /**
     * Returns last day of month.
     * @param date base date
     * @param format date format
     * @param locale {@link Locale}
     * @return formatted day string based on given {@link Date} and format.
     */
    public static String getLastDayOfMonth(Date date, String format, Locale locale){
        Calendar c  = Calendar.getInstance(locale);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format(c.getTime(), format, locale);
    }
    /**
     * Returns last day of month.
     * @param format date format
     * @param locale {@link Locale}
     * @return formatted day string based on current date and format.
     */
    public static String getLastDayOfMonth(String format, Locale locale){
        return getLastDayOfMonth(new Date(System.currentTimeMillis()), format, locale);
    }

    /**
     * Returns maximum day count of given {@link Date}/
     * @param date
     * @return
     */
    public static int getMaxDaysOfMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    /**
     * Return formatted string of date.
     * @param d {@link Date}
     * @param format format string.
     * @param locale {@link Locale}
     * @return formatted day string based on given {@link Date} and format.
     */
    public static String format(Date d, String format, Locale locale) {
        return new SimpleDateFormat(format, locale).format(d);
    }

    public static int getYear(){
        return getYear(new Date(System.currentTimeMillis()));
    }

    public static int getYear(Date date) {
        return getCalendar(date).get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        return (getCalendar(date).get(Calendar.MONTH)) + 1;
    }

    /**
     * Returns actual maximum days of each month.
     * @param baseDate
     * @return
     */
    public static int[] getDaysOfMonth(Date baseDate) {
        int[] daysOfMonth = new int[12];
        int year = getYear(baseDate);
        Calendar cal = Calendar.getInstance();
        cal.set(year, 0, 1);
        for( int i = 0; i < daysOfMonth.length; i++ ) {
            System.out.println(cal.getTime());
            daysOfMonth[i] = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.add(Calendar.MONTH, 1);
        }
        return daysOfMonth;
    }
    private DateUtil(){}
}

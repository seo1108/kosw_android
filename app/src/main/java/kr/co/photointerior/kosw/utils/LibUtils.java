/*
 * @(#)Utils.java
 * date : 2015. 4. 16.
 */
package kr.co.photointerior.kosw.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 유틸리티 성 메소드의 정의 클래스.
 */
public class LibUtils {
	/**
	 * 넘겨 받은 문자(열)이 null이거나 공백문자("")인가를 검증한다.
	 * @param s
	 * @return true : null이거나, 공백문자("")이다.
	 */
	public static boolean isEmpty( String s ){
		return s == null || "".equals(s);
	}
	public static boolean isEmptyOrWhiteSpace(String...params){
		for( String s : params ){
			if( !isEmpty(s)){
				return false;
			}
		}
		return true;
	}
	/**
	 * 넘겨 받은 문자(열)이 null이거나 공백문자("") 또는 whitespace로만 구성되었는가를 검증한다.
	 * @param s
	 * @return true : null이거나, 공백문자("") 또는 whitespace로만 구성되었다. 
	 */
	public static boolean isEmptyOrWhiteSpace( String s ){
		return s == null || "".equals(s) || isWhiteSpace(s);
	}
	/**
	 * 문자열이 스페이스문자(" ")만으로 이루어 졌는가를 검사한다. Escape문자는 검사하지 않느다.
	 * @param s
	 * @return true : 스페이스문자로만 이루어 졌다. null이나 공백문자("")일 경우 false를 반환한다.
	 */
	public static boolean isWhiteSpace(String s){
		if(isEmpty(s)){
			return false;
		}
		char c;
		for( int i = 0, n = s.length(); i < n;i++ ){
			c = s.charAt(i);
			if( c != ' '){
				return false; 
			}
		}
		return true;
	}
	
	/**
     *0-9숫자만으로 이루어진 문자열인가를 검증 한다. null이거나 공백문자("')일 경우는 무조건
     *false를 return한다.
     *
	 *@param s 검증 할 문자(열)
	 *@return true : 숫자형 문자(열), false : 숫자 이외의 문자가 포함되어 있음
	 */
	public static boolean isDigit( String s ){
		if( isEmpty(s)){
			return false;
		}
	    Pattern pattern = Pattern.compile( "[^0-9]" );
	    return !pattern.matcher( s ).find();
	}
	/**
	*문자열을 특정 위치에서 잘라서 반환한다.
	*@param str : 문자(열)
	*@param len :문자(열)을 자를 길이. 이 길이는 Byte단위이다.(한글 * 2 )
	*@param suffix : 끝에 붙일 문자열
	*@return String
	*/
	public static String cut(String str, int len, String suffix) {
		if (str == null || "".equals(str))
			return "none";
		int slen = 0, blen = 0;
		int depth = 0;
		char c;
		try {
			if (str.getBytes("MS949").length > len - depth) {
				while (blen + 1 < len - depth) {
					c = str.charAt(slen);
					blen++;
					slen++;
					if (c > 127)
						blen++; //2-byte character..
				}
				str = str.substring(0, slen) + suffix;
			}
		} catch (UnsupportedEncodingException e) {
			return str;
		}
		return str;
	}
	/**
	 * 특정 일시가 지정된 기간내에 있는가를 검사한다.
	 * @param compareTime 검사할 일시
	 * @param compareTimeFormat 검사할 일시의 형식화 문자열
	 * @param startTime 구간 시작일시
	 * @param endTime 구간종료 일시
	 * @param format 구간시작/종료일시의 형식화 문자열
	 * @return true-구간내에 있다.
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
	*특정 포맷으로 된 날짜의 시간을 1/1000초 단위의 long으로 반환한다.
	*주의)반드시 dateString은 oldFormat형태로 되어 있어야 함.
	*@param oldFormat : 날짜의 포맷,
	*@param dateString : oldFormat에 해당하는 날짜형태
	*@return long 1970년1월1일 00:00:00부터 지금까지의 1/1000단위 시간
	*@exception ParseException
	*/
	public static long getTime(
		String oldFormat,
		String dateString) {
		return getDate(oldFormat, dateString).getTime();
	}
	/**
	 * 특정 Format으로 된 날짜의 Date 객체를 얻어 반환한다.
	 * @param format
	 * @param date
	 * @return 문자열로 된 date를 Date로 변환한 Date instance
	 */
	public static Date getDate(String format, String date ){
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
	 * Date 객체를  받아 Calendar에 설정하여 반환한다.
	 * @param date Date instance of current day
	 * @return 현재 일자가 설정 된 Calendar 객체
	 */
	public static Calendar getCalendar(Date date) {
		Calendar c = Calendar.getInstance(Locale.KOREA);
		c.setTime(date);
		return c;
	}
	/**
	 * 시스템의 현재 일시를 format형식으로 반환한다. format이 null이거나 공백문자("")일 경우
	 * "yyyy년MM월dd일 HH시mm분ss초"을 기본 format로 한다.
	 * @param format
	 * @return 현재 일자를 format으로 형식화 한 문자열
	 */
	public static String currentDate(String format ){
		if( isEmptyOrWhiteSpace( format ) ){
			format = "yyyy년MM월dd일 HH시mm분ss초";
		}
		return new SimpleDateFormat( format ).format(new Date());
	}
	
	public static String formatDate(Date date, String format) {
		if (date == null)
			return "1970-01-01 00:00:00";
		return new SimpleDateFormat(format).format(date);
	}
	/**
	 * dateText가 현재일자보다 과거일자일가를 검사한다. 0시 0분 0초를 기준으로 검사한다.
	 * @param format 검사할 일자의 형식
	 * @param dateText 검사할 일자
	 * @return true - 과거이다.
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
	*특정 날짜 포맷을 다른 날짜 포맷으로 변환하여 반환 한다.<br>
	*주의)반드시 dateString은 oldFormat형태로 되어 있어야 함.
	*@param oldFormat : 바꾸고자 하는 날짜의 포맷,
	*@param dateString : oldFormat에 해당하는 날짜형태
	*@param newFormat : dateString을 바꿀 새로운 날짜 포맷
	*@return String newFormat으로 변환 된 날짜. dateString이 null이거나 공백문자("")면
	*공백문자("")를 반환한다.
	*@exception ParseException
	*/
	public static String formatDate(
		String oldFormat,
		String dateString,
		String newFormat) {
		if(isEmpty(dateString)){
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
	
	public static String format(double value, String format) {
		NumberFormat nf = new DecimalFormat( format );
		return nf.format( value );
	}
	
	/**
	 * 서버 응답코드가 정상처리인가를 검사한다.
	 * HTTP 응담의 2xx를 기준으로 응답코드가 해당 범위에 속하는가를 기준으로 한다.<br/>
	 * @param resCode
	 * @return
	 */
	public static boolean isWorkSuccess(String resCode){
		return isWorkSuccess(Integer.valueOf(resCode));
	}
	
	public static boolean isWorkSuccess(int resCode){
		return resCode >= 200 && resCode <= 226;
	}
	
	/**
	 * 응답코드와 응답 {@link JSONObject}에 "error" 항목이 있는가를 기준으로 성공/실패를 결정 반환한다.
	 * @param resJson 응답 json
	 * @return true-서버작업 성공.
	 * @throws JSONException
	 */
	public static boolean isWorkSuccess(JSONObject resJson)throws JSONException {
		return isWorkSuccess(resJson.getString("res_status_code")) && !isWorkFail(resJson);
	}
	
	/**
	 * 서버 응답코드가 서버에러인가를 검사.
	 * HTTP 응담의 5xx를 기준으로 응답코드가 해당 범위에 속하는가를 기준으로 한다.<br/>
	 * @param resCode
	 * @return
	 */
	public static boolean isServerError(String resCode){
		int value = Integer.valueOf(resCode);
		return value >= 500 && value <= 599;
	}
	
	/**
	 * 서버 요청시 작업에 실패했는가를 검사한다. 
	 * 예외의 경우나 서버에러의 경우도 포함하여, {@link JSONObject}에 "error" 항목이 존해하는가를 기준으로 판단한다.
	 * @param resJson
	 * @return
	 */
	public static boolean isWorkFail(JSONObject resJson)throws JSONException {
		return !resJson.isNull("error");
	}
	
	/**
	 * 두 문자(열)이 같은 문자(열)인가를 반환한다. 실제 값을 비교한다.
	 * source가 null일 경우 target도 null이간로 판단한다.
	 * @param source
	 * @param target
	 * @return true : 같다, source와 target이 모두 null일경우 true를 반환한다.
	 */
	public static boolean isEquals(String source, String target){
		if( source == null )
			return target == null;
		return source.equals(target);
	}
	/**
	 * 두 문자(열)이 같은 문자(열)인가를 반환한다. 실제 값을 비교한다.
	 * source가 null일 경우 target도 null인가로 판단한다.
	 * @param source
	 * @param target
	 * @return true : 같다, source와 target이 모두 null일경우 true를 반환한다.
	 */
	public static boolean isEqualsIgnoreCase(String source, String target){
		if( source == null )
			return target == null;
		return source.equalsIgnoreCase(target);
	}
	
	/**
	 * <strong>주의 : InputStream은 이 메소드를 호출한 곳에서 close()해야 함.</strong>
	 * <br/>2013. 1. 10., kugie@rapids.kr
	 * @param inputStream 복사할 원본파일 스트림.
	 * @param target
	 * @return true-성공.
	 */
	public static boolean copyTo(InputStream inputStream, File target) {
		try {
			OutputStream out = new FileOutputStream(target);
			try {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				if( out != null ){
					out.flush();
					out.close();
				}
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	/**
	 * 파일 복사.
	 * <br/>2013. 1. 10., kugie@rapids.kr
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean copyTo(File source, File target ){
		boolean result = false;
		InputStream in = null;
		try{
			in = new FileInputStream(source);
			result = copyTo( in, target );
		}catch( FileNotFoundException e ){
			e.printStackTrace();
			result = false;
		}finally{
			if( in != null ){
				try {in.close();} catch (IOException e) {}
			}
		}
		return result;
	}
	
	public static String urlEncodeToUtf8(String str){
		String encodeResult = str;
		try {
			encodeResult = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e("common-lib", "excp="  + e.getMessage());
;		}
		return encodeResult;
	}
	/** 
	 * null일 경우 공백문자("")를 반환.
	* @param txt
	* @return */
	public static String getEmptyText(String txt){
		return isEmptyOrWhiteSpace(txt) ? "" : txt;
	}
	
	/**
	 * 특정 일시가 현재일시보다 큰가를 검사한다.
	 * @param sourceTime 검사할 일시
	 * @param sourceTimeFormat 검사할 일시의 형식화 문자열
	 * @return true-크다
	 */
	public static boolean isBiggerViaCurrentTime(String sourceTime, String sourceTimeFormat){
		return getTime(sourceTimeFormat, sourceTime) > System.currentTimeMillis();
	}
	
	/**
	 * 특정 일시가 현재일시보다 과거의 일시인가를 검사한다. 
	 * @param format 검사할 일시의 형식
	 * @param dateText 검사할 일시
	 * @return true-과거이다. 즉, 기한이 지났다.
	 */
	public static boolean isPassedTime(String format, String dateText){
		Calendar cc = getCalendar(new Date(System.currentTimeMillis()));
		Calendar sc = getCalendar(getDate(format, dateText));
		return sc.getTimeInMillis() < cc.getTimeInMillis();
	}
	/**
	 * 특정 날짜를 기준으로 Calendar의 Field에 해당하는 값을 더하거나 빼서 특정 형식으로
	 * 형식화 해서 반환한다. Defautl Locale, Timezone을 이용한다.
	 * @param date 기준일자
	 * @param calendarField Calendar class의 Field
	 * @param amount 더가거나 뺄 숫자
	 * @param format 결과를 형식화 할 Format
	 * @return 날짜형 문자열
	 */
	public static String shift(Date date, int calendarField, int amount, String format){
		Calendar calendar = getGregorianCalendar(null, null);
		calendar.setTime(date);
		calendar.add(calendarField, amount);
		return formatDate(calendar.getTime(), format);
	}
	/**
	 * GregorianCalendar를 반환한다.
	 * @param l Locale
	 * @param t Timezone
	 * @return Locale과 Tiemzone이 모두 null이 아닐때는 GregorianCalendar(t, l);를
	 * Local만 null이 아닐때는 GregorianCalendar(l);를 Timezone만 null이 아닐때는
	 * GregorianCalendar(t);를 반환한다.
	 * 모두 null일 때는 GregorianCalendar();를 반환한다.
	 */
	public static Calendar getGregorianCalendar(Locale l, TimeZone t){
		if( l != null && t != null){
			return new GregorianCalendar(t, l);
		}
		if( l == null && t == null){
			return new GregorianCalendar();
		}
		if( l != null ){
			return new GregorianCalendar(l);
		}else{
			return new GregorianCalendar(t);
		}
	}

	/**
	 * 패스워드 유효성 검증
	 * @param pwd 패스워드
	 * @return true-통과
	 */
	public static boolean isValidPassword(String pwd){
		PasswordValidator pv = PasswordValidator.instance(false, false, false, 6, 16);
		return pv.validatePassword(pwd);
	}
	private LibUtils(){}
}

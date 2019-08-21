package kr.co.photointerior.kosw.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * Utility class for String.
 * Created by kugie.
 * 2018. 04. 30.
 */
public class StringUtil {
    /**
     * Determines whether the value is zero.
     * @param value
     * @return
     */
    public static boolean isZero(String value){
        if( !isDigit(value)){
            return false;
        }
        return Integer.valueOf(value) == 0;
    }
    /**
     * Test that the string is empty.
     * @param s
     * @return true null or empty string("")
     */
    public static boolean isEmpty( String s ){
        return s == null || "".equals(s);
    }
    /**
     * Verify that the character is null or consist of a nul char("") or whitespace.
     * @param s
     * @return
     */
    public static boolean isEmptyOrWhiteSpace( String s ){
        return s == null || "".equals(s) || isWhiteSpace(s);
    }

    public static String isEmptyOrWhiteSpace(String s, String def){
        return isEmptyOrWhiteSpace(s) ? def : s;
    }

    /**
     * 전달 받은 문자들이 null이거나 공백 문자인가를 검사한다.
     * @param values
     * @return 하나라도 Empty 이면 true
     */
    public static boolean hasEmptyOrWhiteSpace(String...values){
        if( values == null ){
            return true;
        }
        for( String s : values ){
            if( isEmptyOrWhiteSpace(s) ){
                return true;
            }
        }
        return false;
    }
    /**
     * Examine whether the string is achieved by the space character(" ") only.
     * Escape character shall not be inspected.
     * @param s
     * @return true : space char
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
     * Determines whether the string is configured with digit[0-9].
     *@param s
     *@return true : digit
     */
    public static boolean isDigit( String s ){
        if( isEmptyOrWhiteSpace(s)){
            return false;
        }
        Pattern pattern = Pattern.compile( "[^0-9]" );
        return !pattern.matcher( s ).find();
    }

    /**
     * Returns the formatted number by given format.
     * @param value
     * @param format
     * @return
     */
    public static String format(double value, String format) {
        NumberFormat nf = new DecimalFormat( format );
        return nf.format( value );
    }

    /**
     * Calculates and returns walking goal step.
     * @param setsValue
     * @param divAmt
     * @return
     */
    public static int getWalkingGoal(int setsValue, int divAmt) {
        int result = setsValue / divAmt;
        int a = setsValue % divAmt;
        int h = (divAmt/2);
        if( a > 0 ) {
            if( a > h) {
                result += 1;
            }
        }
        return result;
    }


    /**
     * Tests whether the first string and the second string are exactly same string.
     * @param source
     * @param target
     * @return true : equal
     */
    public static boolean isEquals( String source, String target){
        if( source == null )
            return target == null;
        return source.equals(target);
    }
    /**
     * Tests whether the first string and the second string are same string.
     * It's use ignore case.
     * @param source
     * @param target
     * @return true : equal
     */
    public static boolean isEqualsIgnoreCase( String source, String target){
        if( source == null )
            return target == null;
        return source.equalsIgnoreCase(target);
    }

    private StringUtil(){}
}

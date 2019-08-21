package kr.co.photointerior.kosw.utils;

/**
 * Date string format enum class.
 * It's has readable date string formats.
 *
 * Created by kugie.
 * 2018. 04. 30.
 */
public enum DateFormat {
    FORMAT_DEFAULT("yyyyMMddHHmmss"),
    FORMAT_DATE_DELIM_DOT("yyyy.MM.dd HH:mm:ss"),
    FORMAT_DATE_DELIM_SLAGH("yyyy/MM/dd HH:mm:ss"),
    FORMAT_DATE_DELIM_DASH("yyyy-MM-dd HH:mm:ss");
    private String format;
    DateFormat(String format){
        this.format = format;
    }

    public String getFormat(){
        return this.format;
    }
}

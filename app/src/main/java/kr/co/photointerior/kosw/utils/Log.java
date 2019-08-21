package kr.co.photointerior.kosw.utils;

/**
 * Log class
 */
public class Log {
    public static boolean LOGGABLE = false;
    public static final String TAG = "dspone.phonefinder";

    /**
     * Debug log.
     * @param message
     */
    public static void d(String message) {
        if(LOGGABLE) {
            d(TAG, message);
        }
    }

    /**
     * Info log.
     * @param message
     */
    public static void i(String message){
        if(LOGGABLE) {
            LogUtils.i(TAG, message);
        }
    }
    /**
     * Debug log.
     * @param tag
     * @param message
     */
    public static void d(String tag, String message) {
        if(LOGGABLE) {
            LogUtils.d(tag, message);
        }
    }

    /**
     * Error log.
     * @param message
     */
    public static void e(String message) {
        if(LOGGABLE) {
            e(TAG, message);
        }
    }

    /**
     * Error log.
     * @param tag
     * @param message
     */
    public static void e(String tag, String message) {
        if(LOGGABLE) {
            LogUtils.e(tag, message);
        }
    }

    /**
     * Error log.
     * @param message
     * @param e
     */
    public static void e(String message, Throwable e){
        if(LOGGABLE) {
            e(TAG, message, e);
        }
    }

    /**
     * Error log.
     * @param tag
     * @param message
     * @param e
     */
    public static void e(String tag, String message, Throwable e) {
        if(LOGGABLE) {
            LogUtils.e(tag, message, e);
        }
    }

    /**
     * Sets whether or not the log is enabled.
     * @param logEnable if value is 'false' log be disabled.
     */
    public static void setLogEnabled(boolean logEnable){
        LOGGABLE = logEnable;
    }
}

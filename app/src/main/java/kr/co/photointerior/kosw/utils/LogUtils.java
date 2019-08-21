
package kr.co.photointerior.kosw.utils;

import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import kr.co.photointerior.kosw.conf.ConditionHolder;
import kr.co.photointerior.kosw.global.Env;

public final class LogUtils {

    private static final boolean DEBUG = Env.Bool.LOG_ENABLE.getValue();

    private static final String LOG_PREFIX = ConditionHolder.instance().getLogPrefix();
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 50;


    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void d(final String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void d(final String tag, String message, Throwable cause) {
        if (DEBUG) {
            Log.d(tag, message, cause);
        }
    }

    public static void v(final String tag, String message) {
        if (DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void v(final String tag, String message, Throwable cause) {
        if (Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void i(final String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void i(final String tag, String message, Throwable cause) {
        if (DEBUG) {
            Log.i(tag, message, cause);
        }
    }

    public static void w(final String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void w(final String tag, String message, Throwable cause) {
        if (DEBUG) {
            Log.w(tag, message, cause);
        }
    }

    public static void e(final String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void e(final String tag, String message, Throwable cause) {
        if (DEBUG) {
            Log.e(tag, message, cause);
        }

    }

    private LogUtils() {
    }

    public static void err(String tag, String log) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String name = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            String lineNumber = stackTraceElement.getLineNumber() + "";
            typedLog(LogType.LOG_E, makeLogTag(tag), "\n[" + className + "]\n[" + name + "][" + lineNumber + "] :\n" + log);
        }
    }

    public static void err(String tag, Object o) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String name = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            String lineNumber = stackTraceElement.getLineNumber() + "";
            String log = o != null ? o.toString() : "[NULL]";
            typedLog(LogType.LOG_E, makeLogTag(tag), "\n[" + className + "]\n[" + name + "][" + lineNumber + "] :\n" + log);
        }
    }

    public static void debug(String tag, String log) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String name = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            String lineNumber = stackTraceElement.getLineNumber() + "";
            typedLog(LogType.LOG_D, makeLogTag(tag), "\n[" + className + "]\n[" + name + "][" + lineNumber + "] :\n" + log);
        }
    }

    public static void debug(String tag, Object o) {
        if (DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String name = stackTraceElement.getMethodName();
            String className = stackTraceElement.getClassName();
            String lineNumber = stackTraceElement.getLineNumber() + "";
            String log = o != null ? o.toString() : "[NULL]";
            typedLog(LogType.LOG_D, makeLogTag(tag), "\n[" + className + "]\n[" + name + "][" + lineNumber + "] :\n" + log);
        }
    }

    enum LogType {
        LOG_E, LOG_D
    }

    public static void typedLog(LogType logType, String tag, String log) {
        if (DEBUG) {
            switch (logType) {
                case LOG_D:
                    Log.d(tag, log);
                    break;

                case LOG_E:
                    Log.e(tag, log);
                    break;
            }
        }
    }

    public static void log(Map<String, Object> map){
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> keys = map.keySet().iterator();
            for (; keys.hasNext(); ) {
                String k = keys.next();
                sb.append(k).append("=").append(map.get(k)).append("\n");
            }
            LogUtils.err("KOSW", sb.toString());
        }
    }

    public static void log(String prefix, Map<String, Object> map){
        if (DEBUG) {
            StringBuilder sb = new StringBuilder(prefix).append("=\n");
            Iterator<String> keys = map.keySet().iterator();
            for (; keys.hasNext(); ) {
                String k = keys.next();
                sb.append(k).append("=").append(map.get(k)).append("\n");
            }
            LogUtils.err("KOSW", sb.toString());
        }
    }
}

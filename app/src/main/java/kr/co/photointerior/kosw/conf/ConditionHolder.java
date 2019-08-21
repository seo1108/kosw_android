package kr.co.photointerior.kosw.conf;

import kr.co.photointerior.kosw.service.fcm.FcmMessageWorker;
import kr.co.photointerior.kosw.service.fcm.FcmTokenWorker;
import kr.co.photointerior.kosw.global.AppMode;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 라이브러리에서 사용되는 각종 환경변수, 설정등 처리.
 * Created by kugie
 */
public class ConditionHolder {
    private final String TAG = ConditionHolder.class.getSimpleName();
    private final static ConditionHolder instance = new ConditionHolder();
    private AppCondition condition;

    public synchronized void init(AppCondition condition){
        this.condition = condition;
    }

    /**
     * Returns the base url for normal WebView using http(s).
     * @return
     */
    public String getRestBaseUrl(){
        return condition.getBaseUrl();
    }

    /**
     * Returns the sd-card directory for save files.
     * @return
     */
    public String getFileStorePath(){
        return condition.getDiskPath();
    }

    /**
     * Returns the absolute file path with given filename.
     * @param filename
     * @return
     */
    public String getDiskPath(String filename){
        return condition.getDiskPath(filename);
    }

    /**
     * returns the byte buffer size for file in/out stream.
     * @return
     */
    public int getByteBufferSize(){
        return condition.getBufferSize();
    }

    /**
     * returns the fcm token worker class.
     * @return
     */
    public FcmTokenWorker getFcmTokenWorker(){
        try {
            return condition.getFcmTokenWorker().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * returns the fcm push message worker class.
     * @return
     */
    public FcmMessageWorker getFcmMessageWorker(){
        try {
            return condition.getFcmMessageRowker().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Service mode
     * @see AppMode
     */
    public boolean isModeService(){
        return AppMode.SERVICE.equals(condition.getAppMode());
    }

    /**
     * @return log prefix
     */
    public String getLogPrefix(){
        return StringUtil.isEmptyOrWhiteSpace(condition.getLogPrefix()) ? "RWV-LIB-" : condition.getLogPrefix();
    }

    public static ConditionHolder instance(){
        return instance;
    }

    private ConditionHolder(){}

}

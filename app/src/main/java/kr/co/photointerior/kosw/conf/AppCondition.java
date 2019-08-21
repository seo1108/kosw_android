package kr.co.photointerior.kosw.conf;

import android.app.Activity;

import java.io.File;

import kr.co.photointerior.kosw.service.fcm.FcmMessageWorker;
import kr.co.photointerior.kosw.service.fcm.FcmTokenWorker;
import kr.co.photointerior.kosw.global.AppMode;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 *
 * Created by kugie
 */

public class AppCondition {
    //rest api
    private Boolean useRestApi = true;
    private String restBaseUrl;

    //use ssl or not
    private Boolean useSSL = false;

    //normal web
    private String baseUrl;

    //Notification
    private int notificationSmallIcon;
    private int notificationLargeIcon;
    private String notificationTitle;
    private String notificationContent;
    private Class<Activity> notificationIntentClass;

    //download
    private String diskPath;
    private int bufferSize;//file read,write byte buffer size

    //fcm worker classes
    private Class<? extends FcmTokenWorker> fcmTokenWorker;
    private Class<? extends FcmMessageWorker> fcmMessageRowker;

    //app mode
    private AppMode appMode;
    //log prefix
    private String logPrefix;
    /**
     * Returns whether ths REST API is enabled.
     * @return Default value is 'true'.
     */
    public Boolean isUseRestApi() {
        return useRestApi;
    }

    /**
     * Enable or disable the SSL protocol.
     * @return If 'false' use 'HTTP'.
     */
    public boolean isUseSSL() {
        return useSSL;
    }

    /**
     * Returns the REST API base url.
     * @return If 'useRestApi==false' it returns null.
     */
    public String getRestBaseUrl(){
        if( !isUseRestApi() ){
            return null;
        }
        if(StringUtil.isEmptyOrWhiteSpace(restBaseUrl)){
            throw new IllegalStateException("Base url can't be null.");
        }
        if( isUseSSL() && !restBaseUrl.startsWith("https://")){
            throw new IllegalStateException("Should be use SSL protocol.[HTTPS]");
        }
        if( !restBaseUrl.endsWith("/") ){
            return restBaseUrl + "/";
        }
        return restBaseUrl;
    }

    /**
     * Returns the http(s) url for {@link android.webkit.WebView}
     * @return
     */
    public String getBaseUrl(){
        return baseUrl;
    }
    /**
     * Returns the notification small icon id.
     * @return
     */
    public int getNotificationSmallIcon() {
        return notificationSmallIcon;
    }

    /**
     * Returns the notification large icon id.
     * @return
     */
    public int getNotificationLargeIcon() {
        return notificationLargeIcon;
    }

    /**
     * Returns the notification title text id.
     * @return
     */
    public String getNotificationTitle() {
        return notificationTitle;
    }

    /**
     * Returns the notification context text id.
     * @return
     */
    public String getNotificationContent() {
        return notificationContent;
    }

    public Class<Activity> getNotificationIntentClass() {
        return notificationIntentClass;
    }

    /**
     * Returns the sd-card path of application.
     * @return
     */
    public String getDiskPath(){
        return diskPath;
    }
    /**
     * @param filename
     * @return 파일 절대경로
     */
    public String getDiskPath(String filename){
        return getDiskPath() + File.separator + filename;
    }

    /**
     * Returns the byte buffer size for file in/out stream.
     * @return
     */
    public int getBufferSize(){
        return bufferSize;
    }

    public Class<? extends FcmTokenWorker> getFcmTokenWorker(){
        return fcmTokenWorker;
    }
    public Class<? extends FcmMessageWorker> getFcmMessageRowker(){
        return fcmMessageRowker;
    }

    /**
     * @return app service mode
     */
    public AppMode getAppMode(){
        return appMode;
    }

    /**
     * @return Log prefix
     */
    public String getLogPrefix(){
        return logPrefix;
    }
    /**
     * ServiceCondition builder class.
     */
    public static class Builder{
        //Rest API
        private Boolean useRestApi = true;
        private String restBaseUrl;
        //http(s)
        private Boolean useSSL = false;
        //normal url
        private String baseUrl;
        //Notification
        private int notificationSmallIcon;
        private int notificationLargeIcon;
        private String notificationTitle;
        private String notificationContent;
        private Class<Activity> notificationIntentClass;
        //disk path
        private String diskPath;
        private int bufferSize = 4096;
        //fcm worker classes
        private Class<? extends FcmTokenWorker> fcmTokenWorker;
        private Class<? extends FcmMessageWorker> fcmMessageRowker;

        //app mode
        private AppMode appMode;
        //log prefix
        private String logPrefix;
        /**
         * Enable or disable the REST API.
         * @param useRestApi If 'false' the REST API disabled.
         * @return
         */
        public Builder useRestApi(Boolean useRestApi) {
            this.useRestApi = useRestApi;
            return this;
        }

        /**
         * Sets the REST API base url.
         * @param restBaseUrl It should be end with '/'.
         * @return
         */
        public Builder restBaseUrl(String restBaseUrl) {
            this.restBaseUrl = restBaseUrl;
            return this;
        }

        /**
         * Sets the SSL use flag.
         * @param useSSL If 'false' SSL not use.
         * @return
         */
        public Builder useSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }

        /**
         * Sets the url for WebView using http(s).
         * @param url
         * @return
         */
        public Builder baseUrl(String url){
            this.baseUrl = url;
            return this;
        }

        /**
         * Sets the notification small icon res id.
         * @param notificationSmallIcon
         * @return
         */
        public Builder notificationSmallIcon(int notificationSmallIcon) {
            this.notificationSmallIcon = notificationSmallIcon;
            return this;
        }

        /**
         * Sets the notification large icon res id.
         * @param notificationLargeIcon
         * @return
         */
        public Builder notificationLargeIcon(int notificationLargeIcon) {
            this.notificationLargeIcon = notificationLargeIcon;
            return this;
        }

        /**
         * Sets the notification title text id.
         * @param notificationTitle
         * @return
         */
        public Builder notificationTitle(String notificationTitle) {
            this.notificationTitle = notificationTitle;
            return this;
        }

        /**
         * Sets the notification content text id.
         * @param notificationContent
         * @return
         */
        public Builder notificationContent(String notificationContent) {
            this.notificationContent = notificationContent;
            return this;
        }

        /**
         * Intent class.
         * This class will be used to PendingIntent.
         * @param clz
         * @return
         */
        public Builder notificationIntentClass(Class clz){
            this.notificationIntentClass = clz;
            return this;
        }

        /**
         * @param path sd-card path for file
         * @return
         */
        public Builder diskPath(String path){
            this.diskPath = path;
            return this;
        }

        /**
         * Sets the byte buffer size for file stream.
         * @param size
         * @return
         */
        public Builder byteBufferSize(int size){
            this.bufferSize = size;
            return this;
        }

        public Builder fcmTokenWorker(Class<? extends FcmTokenWorker> worker){
            this.fcmTokenWorker = worker;
            return this;
        }

        public Builder fcmMessageWorker(Class<? extends FcmMessageWorker> worker){
            this.fcmMessageRowker = worker;
            return this;
        }

        public Builder appMode(AppMode mode){
            this.appMode = mode;
            return this;
        }
        public Builder logPrefix(String prefix){
            this.logPrefix = prefix;
            return this;
        }

        /**
         * Creates {@link AppCondition} instance and returns it.
         * @return
         */
        public AppCondition build(){
            AppCondition condition = new AppCondition();
            //Rest API
            condition.useRestApi = this.useRestApi;
            condition.restBaseUrl = this.restBaseUrl;

            condition.useSSL = this.useSSL;
            condition.baseUrl = this.baseUrl;

            //Notification
            condition.notificationSmallIcon = this.notificationSmallIcon;
            condition.notificationLargeIcon = this.notificationLargeIcon;
            condition.notificationTitle = this.notificationTitle;
            condition.notificationContent = this.notificationContent;
            condition.notificationIntentClass = this.notificationIntentClass;
            condition.diskPath = this.diskPath;
            condition.bufferSize = this.bufferSize;

            condition.fcmTokenWorker = this.fcmTokenWorker;
            condition.fcmMessageRowker = this.fcmMessageRowker;

            condition.appMode = this.appMode;
            condition.logPrefix = this.logPrefix;

            return condition;
        }
    }
}

package kr.co.photointerior.kosw.global;

import android.os.Environment;

import java.io.File;

/**
 * 환경변수 정의 클래스입니다.
 * Created by kugie on 2018. 4. 30.
 */
public class Env {
    /** 앱 서비스 모드 정의. 서비스 모드에 따라 변경 할 것. */
    //public final static AppMode APP_MODE = AppMode.DEV;
//    public final static AppMode APP_MODE = AppMode.TEST;
    public final static AppMode APP_MODE = AppMode.SERVICE;

    /** 앱 사용자 기기 타입 */
    public static final String DEVICE_TYPE = "A";

    public static final int[] REQ_CODE = {
            1001, 2001, 3001, 5001
    };

    /** true / false 관리 */
    public enum Bool{
        /** 비콘 디버깅을 할 것인가 여부 */
        BEACON_DEBUG(false),
        /** 로그출력 할 것인가 여부 */
        LOG_ENABLE(false),
        FILE_LOG_ENABLE(false),
        SERVER_LOG_ENABLE(false),
        /** 햄버거메뉴 테스트용 메뉴을 활성화할 것인가 여부 */
        SHOW_EXTRA_MENU(false),
        SHOW_TEST_VIEW(false),
        /** 고도측정시 1기압을 사용할 것인가 여부 */
        USE_ALTITUDE_DEFAULT(false),
        /** 가상으로 계단 올라가기 사용할 것인가 여부 */
        USE_TEST_DUMMY_FLOOR_UP(false);
        private boolean value;
        Bool(boolean value){
            this.value = value;
        }

        public boolean getValue(){
            return value;
        }

        public boolean isTrue(){
            return value;
        }
    }

    /**
     * 서비스 환경에 따른 URL 정의
     */
    public enum Url{
        /** Rest Api urls */
        URL_API(
                "http://182.162.141.243/app2/",//dev
                /*"http://192.168.43.232:8080/app2/",//dev*/
                /*"http://192.168.219.10:8080/app2/",//dev*/
                "http://103.60.127.32:8899/app/",//test
                //"http://kos.kingofthestairs.com/app/" //service
                "http://kingofthestairs.com/app2/" //service
        ),
        /** 이미지등 관리자와 사용자, API등에서 동시에 사용하는 리소스 Url */
        URL_GLOBAL(
                /*"http://192.168.55.55:8080/global/",//dev*/
                "http://kos.kingofthestairs.com/global/", //service
                "http://103.60.127.32:8899/global/",//test
                "http://kos.kingofthestairs.com/global/" //service
        ),
        /** 친구에게 공유 URL */
        URL_SHARE("http://kingofthestairs.com","http://kingofthestairs.com","http://kingofthestairs.com");

        private String devUrl;
        private String testUrl;
        private String serviceUrl;
        /**
         * @param devUrl 개발
         * @param testUrl 테스트
         * @param serviceUrl 서비스
         */
        Url(String devUrl, String testUrl, String serviceUrl){
            this.devUrl = devUrl;
            this.testUrl = testUrl;
            this.serviceUrl = serviceUrl;
        }

        /**
         * @return url of each element.
         */
        public String url(){
            if(AppMode.DEV.equals(APP_MODE)){
                return this.devUrl;
            }else if(AppMode.TEST.equals(APP_MODE)){
                return this.testUrl;
            }
            return this.serviceUrl;
        }
    }

    /**
     * Web resource path.
     */
    public enum UrlPath{
        /** 로고 */
        LOGO("logo"),
        /** 캐릭터 */
        CHARACTER("character"),
        /** 현재 나의 캐릭터 */
        //MY_CHARACTER("api/user/myCharacter"),

        //CHARACTER_NEW("images/charecter"),

        /** 카페 가이드**/
        CAFEGUIDE("http://stairsking.co.kr/cafe/image/guide.html"),
        /** 이용약관, 개인정보처리방침 */
        PROVISION("terms.html"),
        /** 도움말 */
        HELP("help/help.html"),
        /** 계단 고유코드란 */
        STAIR_CODE("info.html");
        private String path;
        UrlPath(String path){
            this.path = path;
        }

        public String path(){
            return path;
        }

        public String url(){
            return Url.URL_GLOBAL.url() + path();
        }
    }

    /**
     * @see  kr.co.photointerior.kosw.utils.LogUtils
     */
    public static final String LOG_PREFIX = "Kosw-";

    public static final String EXTERNAL_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 파일 저장 외부 Directory.
     */
    public static final String EXTERNAL_PATH = EXTERNAL_ROOT + File.separator + "kosw";

    public static final String[] PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            /*"android.permission.CAMERA",*/
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"
    };

    /**
     * Fragment view type enum.
     */
    public enum FragmentType{
        /** 메인화면 */
        HOME(100),
        /** 정보설정 */
        INFO_SETTING(200),
        /** 공지 이벤트  */
        NOTICE_EVENT(300),
        NOTICE_EVENT_OPEN(301),
        /** 활동기록 */
        ACTIVITY_RECORD(400),
        /** 활동분석 */
        ACTIVITY_ANALYSIS(500),
        /** 개인 랭킹 */
        RANKING_INDIVIDUAL(600),
        /** 그룹 랭킹 */
        RANKING_GROUP(601),
        /** 그룹 랭킹 */
        GGR_HISTORY(700),
        /** 명예의 전당  */
        CITY_RANKING(710);

        private int code;
        /**
         * @param code Fragment code
         */
        FragmentType(int code){
            this.code = code;
        }
        public int code(){
            return this.code;
        }
    }

    /**
     * Broadcast receiver Action and intent filter.
     */
    public enum Action{
        EXIT_ACTION("kr.co.photointerior.kosw.EXIT_ACTION"),
        CHARACTER_CHANGED_ACTION("kr.co.photointerior.kosw.CHARACTER_CHANGED_ACTION"),
        BEACON_RANGING_SERVICE_ACTION("kr.co.photointerior.kosw.BEACON_SERVICE"),
        PUSH_RECEIVE_ACTION("kr.co.photointerior.kosw.PUSH_RECEIVE_ACTION"),
        PUSH_NOTIFICATION_ACTION("kr.co.photointerior.kosw.PUSH_NOTIFICATION_ACTION"),
        BEACON_MONITORING_ACTION("kr.co.photointerior.kosw.BEACON_MONITORING_ACTION"),
        BEACON_MONITORING_ALTITUDE_ACTION("kr.co.photointerior.kosw.BEACON_MONITORING_ALTITUDE_ACTION"),
        BEACON_BEACON_TEST_INTO_ACTION("kr.co.photointerior.kosw.BEACON_BEACON_TEST_INTO_ACTION"),
        KOSW_SERVICE_RESTART_ACTION("kr.co.photointerior.kosw.KOSW_SERVICE_RESTART_ACTION"),
        KOSW_RANGE_RESTART_ACTION("kr.co.photointerior.kosw.KOSW_RANGE_RESTART_ACTION"),
        CAFE_LOGO_GAIN_ACTION("kr.co.photointerior.kosw.CAFE_LOGO_GAIN_ACTION"),
        APP_IS_BACKGROUND_ACTION("kr.co.photointerior.kosw.APP_IS_BACKGROUND_ACTION");
        private String action;
        Action(String action){
            this.action = action;
        }
        public String action(){
            return this.action;
        }
        public boolean isMatch(String otherAction){
            return action().equals(otherAction);
        }
    }

    public static String getHtmlUrl(UrlPath path){
        return Env.Url.URL_API.url().concat(path.path());
    }

    private Env(){}
}

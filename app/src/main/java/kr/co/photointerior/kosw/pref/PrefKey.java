package kr.co.photointerior.kosw.pref;

/**
 * {@link android.content.SharedPreferences} store key enum.
 * Created by kugie on 2017. 11. 14..
 */
public enum PrefKey {
    /**사용자 아이디*/
    USER_ID,
    /**사용자 패스워드*/
    USER_PWD,
    /**사용자 이름 */
    USER_NAME,
    /** 닉네임 */
    USER_NICK,
    /**사용자 로그인 토큰*/
    USER_TOKEN,
    /**사용자 캐릭터*/
    USER_CHARACTER,
    CHARACTER_MAIN,
    CHARACTER_MAIN_URL,
    CHARACTER_SUB,
    /**FCM 토큰*/
    FCM_TOKEN,
    FCM_TOKEN_REFRESHED,
    /**푸시수신 여부 Flag*/
    PUSH_RECEIVE_FLAG,
    /**사용자가 속한 회사 코드*/
    COMPANY_CODE,
    /**사용자가 속한 회사 로고*/
    COMPANY_LOGO,
    /**사용자가 속한 회사 칼라*/
    COMPANY_COLOR,
    COMPANY_COLOR_NUM,
    /**사용자가 활동하는 건물 코드 */
    BUILDING_CODE,
    BUILDING_NAME,
    BUILDING_SEQ,
    BUILDING_LAT,
    BUILDING_LNG,
    BUILDING_ID,
    /**사용자가 속한 건물 주소*/
    BUILDING_ADDR,
    FCM_BADGE_COUNT,
    /** 층간 높이를 계산하는 데 사용되는 비율 */
    FLOOR_GAP_RATE,
    // 카카오톡 오픈 아이디
    OPEN_ID,
    LOGIN_TYPE,
    /**백그라운드 실행 여부 Flag*/
    AUTO_TRACKING_FLAG
}
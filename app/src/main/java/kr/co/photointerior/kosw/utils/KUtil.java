package kr.co.photointerior.kosw.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.HashMap;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;

/**
 * 계단왕 앱에서 사용되는 유틸리티성 메소드 집합 클래스.
 */
public class KUtil {
    /**
     * Retrofit2 기본 QueryMap을 반환.
     * @return device, token을 기본으로 보유
     */
    public static Map<String, Object> getDefaultQueryMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("device", Env.DEVICE_TYPE);
        map.put("token", getUserToken());
        map.put("buildCode", getBuildingCode());
        return map;
    }

    public static Map<String, Object> getQueryMap(Map<String, Object> other){
        Map<String, Object> map = getDefaultQueryMap();
        map.putAll(other);
        return map;
    }

    /**
     * 사용자 토큰이 저장되어 있는가 검사.
     * @return
     */
    public static boolean hasUserToken(){
        String tk = Pref.instance().getStringValue(PrefKey.USER_TOKEN, "");
        return !StringUtil.isEmptyOrWhiteSpace(tk);
    }

    /**
     * 사용자 아이디가 저장되어 있는가 검사.
     * @return
     */
    public static boolean hasUserId(){
        String tk = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        return !StringUtil.isEmptyOrWhiteSpace(tk);
    }

    /**
     * 블루투스가 켜져 있는가를 확인
     * @return
     */
    public static boolean isBluetoothOn(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            return false;
        }
        return bluetoothAdapter.isEnabled();

    }
//    /**
//     * 캐릭터 이미지 Web Url 반환
//     * @param charFilename 캐릭터 이미지 파일명
//     * @return
//     */
//    public static String getCharacterUrl(String charFilename){
////        "api/user/character?token=&f="
//        String uri = Env.UrlPath.CHARACTER.url();
//        uri = uri.substring(0, uri.length()-1) + "?token=" + getStringPref(PrefKey.USER_TOKEN, "");
//        return uri + "&f=" + charFilename;
//        //return Env.UrlPath.CHARACTER.url() + charFilename;
//    }

//    /**
//     * 나의 캐릭터 이미지 URL 반환
//     * @param isMain true-계단포함된 메인화면 캐릭터, false-일반 캐릭터
//     * @return
//     * @deprecated
//     */
//    public static String getMyCharacterUrl(boolean isMain){
//        if(isMain) {
//            return Env.Url.URL_API.url() + Env.UrlPath.MY_CHARACTER.path() + "?main=Y&token=" + getUserToken() +
//                    "&code=" + getCharacterCode();
//        }
//        return Env.Url.URL_API.url() + Env.UrlPath.MY_CHARACTER.path() + "?main=N&token=" + getUserToken() +
//                "&code=" + getCharacterCode();
//    }

//    /**
//     * 메인, 기타화면에서 사용되는 캐릭터 URL 반환.
//     * @param imageFilename
//     * @return
//     */
//    public static String getCharacterImgUrl(String imageFilename){
//        return Env.Url.URL_API.url() + Env.UrlPath.CHARACTER.path() + "?file=" +imageFilename;
//    }

    public static String getMainCharacterImgUrl(){
        return Env.UrlPath.CHARACTER.url().concat("/").concat(getMainCharacter());
    }
    public static String getSubCharacterImgUrl(){
        return Env.UrlPath.CHARACTER.url().concat("/").concat(getSubCharacter());
    }
    public static String getSubCharacterImgUrl(String imageFile){
        return Env.UrlPath.CHARACTER.url().concat("/").concat(imageFile);
    }

    public static String getCompanyLogoImgUrl(){
        return Env.UrlPath.LOGO.url().concat("/").concat(getCompanyLogo());
    }

    public static String getUserToken(){
        return Pref.instance().getStringValue(PrefKey.USER_TOKEN, "");
    }

    public static String getCompanyLogo(){
        return getStringPref(PrefKey.COMPANY_LOGO, "img_company.png");
    }
    /**
     * 로그이미지 Web URL 반환.
     * @param logoImageFilename
     * @return
     */
    public static String getLogoUrl(String logoImageFilename){
        return Env.UrlPath.LOGO.url() + logoImageFilename;
    }

    public static String getStringPref(PrefKey key, String defValue){
        return Pref.instance().getStringValue(key, defValue);
    }

    public static boolean saveStringPref(PrefKey key, String value){
        return Pref.instance().saveStringValue(key, value);
    }

    /**
     * @return true-수시수신
     */
    public static boolean isPushReceive(){
        return "Y".equals(getStringPref(PrefKey.PUSH_RECEIVE_FLAG, "N"));
    }

    public static boolean isFcmTokenRefreshed(){
        return Pref.instance().getBooleanValue(PrefKey.FCM_TOKEN_REFRESHED, false);
    }

    public static String getCharacterCode(){
        return getStringPref(PrefKey.USER_CHARACTER, "");
    }

    public static String getBuildingCode(){
        return getStringPref(PrefKey.BUILDING_CODE, "");
    }

    public static String getMainCharacter(){
        return getStringPref(PrefKey.CHARACTER_MAIN, "");
    }
    public static String getSubCharacter(){
        return getStringPref(PrefKey.CHARACTER_SUB, "");
    }

    public static String getNickName(){
        String nick = getStringPref(PrefKey.USER_NICK, "");
        if(StringUtil.isEmptyOrWhiteSpace(nick)){
            nick = getStringPref(PrefKey.USER_ID, "");
            if(nick != null){
                nick = nick.split("@", -1)[0];
            }else{
                nick = "";
            }
        }
        return nick;
    }

    public static RequestOptions getGlideNoCacheOption(){
        return new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);
    }

    public static RequestOptions getGlideCacheOption(){
        return new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new ObjectKey(getStringPref(PrefKey.USER_CHARACTER, "")))
                        .skipMemoryCache(false);
    }

    public static RequestOptions getGlideCacheOptionMainCharacter(){
        int width = AUtil.displayWith();
        int height = Float.valueOf( width / 0.72222f).intValue();
        return new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.signature(new ObjectKey(getStringPref(PrefKey.USER_CHARACTER, "")))
                        .error(R.drawable.img_runc_ww04)
                        .placeholder(R.drawable.img_runc_place_holder)
                        .override(width, height)
                        .skipMemoryCache(false);
    }

    public static RequestOptions getGlideCacheOption(String charCode){
        return new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new ObjectKey(charCode))
                        .skipMemoryCache(false);
    }

    public static void startRangingService(Context context){
        context.startService(getRangingServiceIntent(context));
    }

    public static void stopRangingService(Context context){
        context.stopService(getRangingServiceIntent(context));
    }

    public static Intent getRangingServiceIntent(Context context){
        Intent intent = new Intent(context, BeaconRagingInRegionService.class);
        //intent.setPackage(context.getPackageName());
        return intent;
    }

    /**
     * 올라간 계단수에 따른 칼로리 소모값 반환.
     * @param amount 올라간 총 층수
     * @param stairAmt 1개 층의 개단수
     * @return
     */
    public static String calcCalorie(float amount, float stairAmt){
        return StringUtil.format((amount * stairAmt) * 0.15f, "#,##0");
    }

    public static String calcCalorieDefault(float amount){
        return StringUtil.format((amount * 24f) * 0.15f, "#,##0");
    }

    /**
     * 올라간 계단수에 따른 연장 수명(초) 반환
     * @param amount 올라간 총 층수
     * @param stairAmt 1개 층의 개단수
     * @return
     */
    public static String calcLife(float amount, float stairAmt){
        return StringUtil.format((amount * stairAmt) * 4f, "#,##0");
    }
    public static String calcLifeDefault(float amount){
        return StringUtil.format((amount * 24f) * 4f, "#,##0");
    }
    private KUtil(){}
}

package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.BbsOpen;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.social.kakao.KakaoSignupActivity;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 회원과 관련한 기본 activity.
 */
public class BaseUserActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(BaseUserActivity.class);
    protected static Activity self;
    /**
     * 로그인 시도
     * @param email
     * @param pwd
     */
    public void tryLogin(final String email, final String pwd){
        showSpinner("");
        UserService service =
                new DefaultRestClient<UserService>(getBaseContext()).getClient(UserService.class);
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("id", email);
        query.put("pwd", pwd);

        Call<AppUserBase> call = service.tryLogin(query);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, "____________________" + response.raw().toString());
                closeSpinner();
                AppUserBase base;
                if(response.isSuccessful()){
                    base = response.body();
                    base.setUserId(email);
                    LogUtils.err(TAG, "login data :" + base.string());
                    if( base.isSuccess() ){
                        storeUserTokenAndMovesToGps(base);

                    }else{
                        showWarn(R.id.input_warn, base.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_server_not_smooth);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                LogUtils.e(TAG, "request data=" + call.request().toString());
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    /**
     * 로그인 시도
     * @param openid
     * @param email
     * @param nickname
     */
    public void tryKakaoLogin(final String openid, final String email, final String nickname){
        AppUserBase user = DataHolder.instance().getAppUserBase();
        user.setNickName(nickname);

        showSpinner("");
        UserService service =
                new DefaultRestClient<UserService>(getBaseContext()).getClient(UserService.class);
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("openid", openid);
        query.put("email", email);
        query.put("device", "A");
        query.put("nickname", nickname);

        Call<AppUserBase> call = service.tryKakaoLogin(query);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, "____________________" + response.raw().toString());
                closeSpinner();
                AppUserBase base;
                if(response.isSuccessful()){
                    base = response.body();
                    base.setUserId(email);
                    //base.setUserId("k"+openid);
                    LogUtils.err(TAG, "login data :" + base.string());
                    if( base.isSuccess() ){
                        storeUserTokenAndMovesToGps(base);
                        //storeUserTokenAndMovesToMain(base);


                    } /*else if ( base.getResponseCode().equals("1003") ) {
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        bundle.putString("nickname", nickname);
                        callActivity(SignUpActivity.class, bundle, false);
                    }*/
                    else{
                        showWarn(R.id.input_warn, base.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_server_not_smooth);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                LogUtils.e(TAG, "request data=" + call.request().toString());
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    /**
     * 서버에서 받은 Token {@link android.content.SharedPreferences}에 저장 후 Main 이동.
     * @param user
     */
    public void storeUserTokenAndMovesToMain(AppUserBase user){
        /*Pref pref = Pref.instance();
        pref.saveStringValue(PrefKey.USER_ID, user.getUserId());
        pref.saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
        pref.saveStringValue(PrefKey.USER_NICK, user.getNickName());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.USER_CHARACTER, user.getCharacterCode());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.COMPANY_LOGO, user.getCompanyLogo());
        pref.saveStringValue(PrefKey.COMPANY_COLOR, user.getCompanyColor());
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, user.getMainCharFilename());
        pref.saveStringValue(PrefKey.CHARACTER_SUB, user.getSubCharFilenane());
        pref.saveStringValue(PrefKey.BUILDING_CODE, user.getBuildingCode());
        if(user.getBeaconUuidList() != null) {
            KsDbWorker.replaceUuid(getBaseContext(), user.getBeaconUuidList());//UUID 교체
        }*/
        storeUserValues(user);
        initNotReadBbsStatus(user.getBbsSequences());//공지사항 읽음 상태 처리

        Bundle bu = new Bundle();
        bu.putSerializable("_TODAY_ACTIVITY_", user.getTodayActivity());

        // kmj mod
        callActivity(MainActivity.class, bu,true);
    }

    public void storeUserTokenAndMovesToGps(AppUserBase user){
        /*Pref pref = Pref.instance();
        pref.saveStringValue(PrefKey.USER_ID, user.getUserId());
        pref.saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
        pref.saveStringValue(PrefKey.USER_NICK, user.getNickName());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.USER_CHARACTER, user.getCharacterCode());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.COMPANY_LOGO, user.getCompanyLogo());
        pref.saveStringValue(PrefKey.COMPANY_COLOR, user.getCompanyColor());
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, user.getMainCharFilename());
        pref.saveStringValue(PrefKey.CHARACTER_SUB, user.getSubCharFilenane());
        pref.saveStringValue(PrefKey.BUILDING_CODE, user.getBuildingCode());
        if(user.getBeaconUuidList() != null) {
            KsDbWorker.replaceUuid(getBaseContext(), user.getBeaconUuidList());//UUID 교체
        }*/

        storeUserValuesGps(user);
        initNotReadBbsStatus(user.getBbsSequences());//공지사항 읽음 상태 처리

        Bundle bu = new Bundle();
        bu.putSerializable("_TODAY_ACTIVITY_", user.getTodayActivity());


        sendFcmToken();


        //callActivity(GPSAcceptActivity.class,null,true);
        callActivity(MainActivity.class,null,true);
    }

    private void sendFcmToken(){
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        if ( KUtil.getStringPref(PrefKey.FCM_TOKEN, "") == "") {
            String id = FirebaseInstanceId.getInstance().getToken() ;
            if (id == null || id.isEmpty()) {
                return ;
            }
            KUtil.saveStringPref(PrefKey.FCM_TOKEN, id);
        }
        query.put("fcmToken", KUtil.getStringPref(PrefKey.FCM_TOKEN, ""));

        Call<ResponseBase> call =
                new DefaultRestClient<App>(getBaseContext())
                        .getClient(App.class).sendFcmToken(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    Pref.instance().saveBooleanValue(PrefKey.FCM_TOKEN_REFRESHED, false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {}
        });
    }

    // 빌딩 설정후 로그인 과 빌딩정보 저장
    public void storeUserValues(AppUserBase user){
        DataHolder.instance().setAppUserBase(user);


        Pref pref = Pref.instance();
        pref.saveStringValue(PrefKey.USER_ID, user.getUserId());
        pref.saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
        pref.saveStringValue(PrefKey.USER_NICK, user.getNickName());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.USER_CHARACTER, user.getCharacterCode());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.COMPANY_LOGO, user.getCompanyLogo());
        pref.saveStringValue(PrefKey.COMPANY_COLOR, user.getCompanyColor());
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, user.getMainCharFilename());
        pref.saveStringValue(PrefKey.CHARACTER_SUB, user.getSubCharFilenane());
        pref.saveStringValue(PrefKey.BUILDING_SEQ, user.getBuild_seq());
        pref.saveStringValue(PrefKey.BUILDING_NAME, user.getBuild_name());
        pref.saveStringValue(PrefKey.BUILDING_ADDR, user.getBuild_addr());
        pref.saveStringValue(PrefKey.BUILDING_LAT, String.valueOf(user.getBuild_lat()));
        pref.saveStringValue(PrefKey.BUILDING_LNG, String.valueOf(user.getBuild_lng()));
        pref.saveStringValue(PrefKey.BUILDING_CODE, user.getBuildingCode());
        pref.saveStringValue(PrefKey.BUILDING_ID, user.getPlace_id());
        pref.saveStringValue(PrefKey.OPEN_ID, "k" + user.getOpenid());
        pref.saveStringValue(PrefKey.LOGIN_TYPE, user.getLoginType());

        if(user.getBeaconUuidList() != null) {
            KsDbWorker.replaceUuid(getBaseContext(), user.getBeaconUuidList());//UUID 교체
        }
    }


    // 빌딩 설정 전 로그인
    public void storeUserValuesGps(AppUserBase user){

        DataHolder.instance().setAppUserBase(user);

        Pref pref = Pref.instance();
        pref.saveStringValue(PrefKey.USER_ID, user.getUserId());
        pref.saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
        pref.saveStringValue(PrefKey.USER_NICK, user.getNickName());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.USER_CHARACTER, user.getCharacterCode());
        pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, user.getPushFlag());
        pref.saveStringValue(PrefKey.COMPANY_LOGO, user.getCompanyLogo());
        pref.saveStringValue(PrefKey.COMPANY_COLOR, user.getCompanyColor());
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, user.getMainCharFilename());
        pref.saveStringValue(PrefKey.CHARACTER_SUB, user.getSubCharFilenane());
        pref.saveStringValue(PrefKey.OPEN_ID, "k" + user.getOpenid());
        pref.saveStringValue(PrefKey.LOGIN_TYPE, user.getLoginType());


        // 도원빌딩 강제 매핑
        pref.saveStringValue(PrefKey.BUILDING_SEQ, "118");
        pref.saveStringValue(PrefKey.BUILDING_NAME, "도원빌딩");
        pref.saveStringValue(PrefKey.BUILDING_ADDR, "대한민국 서울특별시 마포구 성산동");
        pref.saveStringValue(PrefKey.BUILDING_LAT, "37.5666447");
        pref.saveStringValue(PrefKey.BUILDING_LNG, "126.9122815");
        pref.saveStringValue(PrefKey.BUILDING_CODE, "100001");
        pref.saveStringValue(PrefKey.BUILDING_ID, "ChIJtwmC7eGYfDURUWALZJGt1bE");


        /*
        pref.saveStringValue(PrefKey.BUILDING_SEQ, user.getBuild_seq());
        pref.saveStringValue(PrefKey.BUILDING_NAME, user.getBuild_name());
        pref.saveStringValue(PrefKey.BUILDING_ADDR, user.getBuild_addr());
        pref.saveStringValue(PrefKey.BUILDING_LAT, String.valueOf(user.getBuild_lat()));
        pref.saveStringValue(PrefKey.BUILDING_LNG, String.valueOf(user.getBuild_lng()));

        pref.saveStringValue(PrefKey.BUILDING_CODE, user.getBuildingCode());
        pref.saveStringValue(PrefKey.BUILDING_ID, user.getPlace_id());

        */


        if(user.getBeaconUuidList() != null) {
            KsDbWorker.replaceUuid(getBaseContext(), user.getBeaconUuidList());//UUID 교체
        }
    }

    private void initNotReadBbsStatus(List<String> bbsSeqs){
        if(bbsSeqs != null) {
            /*for (String bbsSeq : bbsSeqs) {
                BbsOpen open = KsDbWorker.getLocalBbsReadFlags(getBaseContext(), bbsSeq);
                if (open == null) {
                    open = new BbsOpen();
                    open.setReadFlag("N");
                    open.setBbsSeq(Integer.valueOf(bbsSeq));
                    KsDbWorker.insertBbsReadStatus(getBaseContext(), open);
                }
            }*/
        }
    }

    /**
     * 로그인 토큰이 존재하는 경우 서버 로그인 시도.
     */
    public void tryLoginByToken(){
        final String email = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", email);
        LogUtils.log(queryMap);

        Call<AppUserBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).tryLoginByTokenAndId(queryMap);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, response.raw().toString());
                AppUserBase user;
                if(response.isSuccessful()){
                    user = response.body();
                    if(user.isSuccess()){
                        //LogUtils.e(TAG, "login data on splash=" + response.body().getTodayActivity().string());
                        LogUtils.err(TAG, "login data on splash=" + response.body().string());
                        storeUserTokenAndMovesToGps(user);
                        //storeUserTokenAndMovesToMain(user);

                    }else{
                        callActivity(LoginActivity.class, true);
                    }
                }else{
                    callActivity(LoginActivity.class, true);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                callActivity(LoginActivity.class, true);
            }
        });
    }

    public void tryLoginByTokenToMain(){
        final String email = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", email);
        LogUtils.log(queryMap);
        Call<AppUserBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).tryLoginByTokenAndId(queryMap);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, response.raw().toString());
                AppUserBase user;
                if(response.isSuccessful()){
                    user = response.body();
                    if(user.isSuccess()){
                        //LogUtils.e(TAG, "login data on splash=" + response.body().getTodayActivity().string());
                        LogUtils.err(TAG, "login data on splash=" + response.body().string());
                        storeUserTokenAndMovesToMain(user);
                    }else{
                        callActivity(LoginActivity.class, true);
                    }
                }else{
                    callActivity(LoginActivity.class, true);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                callActivity(LoginActivity.class, true);
            }
        });
    }


    public void tryReLoginByToken(Acceptor acceptor){
        final String email = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        final String build_seq = Pref.instance().getStringValue(PrefKey.BUILDING_SEQ, "");
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", email);
        queryMap.put("build_seq", build_seq);
        LogUtils.log(queryMap);
        Call<AppUserBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).tryLoginByTokenAndIdBuild(queryMap);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, response.raw().toString());
                AppUserBase user;
                if(response.isSuccessful()){
                    user = response.body();
                    if(user.isSuccess()){
                        //LogUtils.e(TAG, "login data on splash=" + response.body().getTodayActivity().string());
                        LogUtils.e(TAG, "login data on splash=" + response.body().string());
                        storeUserValues(user);
                    }
                }
                if(acceptor != null){
                    acceptor.accept();
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                if(acceptor != null){
                    acceptor.accept();
                }
            }
        });
    }



    // 카카오
    @Override
    protected void onResume() {
        super.onResume();
        KoswApp.setCurrentActivity(this);
        self = BaseUserActivity.this;
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = KoswApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            KoswApp.setCurrentActivity(null);
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        startActivity(intent);
        finish();
    }


//    /**
//     * 서버에서 받은 Token {@link android.content.SharedPreferences}에 저장 후 Main 이동.
//     * @param user
//     */
//    private void storeUserTokenAndMovesToMain(AppUserBase user){
//        Pref pref = Pref.instance();
//        pref.saveStringValue(PrefKey.USER_ID, user.getUserId());
//        pref.saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
//        pref.saveStringValue(PrefKey.USER_NICK, user.getNickName());
//        pref.saveStringValue(PrefKey.USER_CHARACTER, user.getCharacterCode());
//        pref.saveStringValue(PrefKey.COMPANY_LOGO, user.getCompanyLogo());
//        pref.saveStringValue(PrefKey.COMPANY_COLOR, user.getCompanyColor());
//        callActivity(MainActivity.class, true);
//    }
}

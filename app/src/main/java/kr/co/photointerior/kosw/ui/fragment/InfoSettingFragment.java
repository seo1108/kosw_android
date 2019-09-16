package kr.co.photointerior.kosw.ui.fragment;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Profile;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.service.stepcounter.StepCounterService;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.ui.InfoSettingGoupActivity;
import kr.co.photointerior.kosw.ui.InfoSettingStairAddActivity;
import kr.co.photointerior.kosw.ui.InfoSettingPasswordActivity;
import kr.co.photointerior.kosw.ui.InfoSettingProfileActivity;
import kr.co.photointerior.kosw.ui.InfoSettingStairViewActivity;
import kr.co.photointerior.kosw.ui.InfoSignupCafeActivity;
import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.ui.NoticeEventActivity;
import kr.co.photointerior.kosw.ui.ProvisionActivity;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.SimpleSpanBuilder;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.widget.CircleImageView;
import kr.co.photointerior.kosw.widget.MenuRow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 정보설정 Fragment.
 */
public class InfoSettingFragment extends BaseFragment {
    private String TAG = LogUtils.makeLogTag(InfoSettingFragment.class);
    private Dialog mDialog;
    private DataHolder mDataHolder = DataHolder.instance();
    private int[] mBtnIds = {
            R.id.btn_change_profile,
            R.id.btn_change_password,
            /*
            R.id.btn_add_stair,
            R.id.btn_show_stair,
            */
            R.id.btn_goup_building,
            /*
            R.id.btn_signup_cafe,
            */
            R.id.btn_push_setting,
            R.id.btn_auto_tracking_setting,
            R.id.btn_share,
            R.id.btn_logout,
            R.id.btn_withdraw,
            R.id.btn_menu_provision,
            R.id.btn_menu_notice
    };

    public static BaseFragment newInstance(BaseActivity activity){
        BaseFragment frag = new InfoSettingFragment();
        frag.mActivity = activity;

        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {
        //getView(R.id.btn_push_setting).setVisibility(View.GONE);
    }

    @Override
    protected void attachEvents() {
        View.OnClickListener clickListener = new FragmentClickListener(this);
        for( int x : mBtnIds ){
            getView(x).setOnClickListener(clickListener);
        }
        getView(R.id.btn_push_setting).setOnClickListener(clickListener);
        getView(R.id.btn_share).setOnClickListener(clickListener);
        getView(R.id.btn_auto_tracking_setting).setOnClickListener(clickListener);

        setInitAutoTrackingSwitch();
    }

    @Override
    public void performFragmentClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_change_profile://프로필 변경
                mActivity.callActivity(InfoSettingProfileActivity.class, false);
                break;
            case R.id.btn_change_password://비밀번호 변경
                mActivity.callActivity(InfoSettingPasswordActivity.class, false);
                break;
            case R.id.btn_goup_building:// 내가 오른 빌딩
                mActivity.callActivity(InfoSettingGoupActivity.class, false);
                break;
                /*
            case R.id.btn_signup_cafe:// 카페 가입
                mActivity.callActivity(InfoSignupCafeActivity.class, false);
                break;
                */
            case R.id.btn_push_setting://푸시수신 설정
                changePushReceiveFlag();
                break;
            case R.id.btn_auto_tracking_setting://백그라운드 실행 허용
                changeAutoTrackingFlag();
                break;
            case R.id.btn_share://공유하기
                shareKosw();
                break;
            case R.id.btn_logout://로그아웃
                logout();
                break;
            case R.id.btn_withdraw://탈퇴하기
                withdrawal();
                break;
            case R.id.btn_menu_provision://이용약관 보기
                //openWebBrowser(Env.UrlPath.PROVISION.url(), false);
                mActivity.callActivity(ProvisionActivity.class, false);
                break;
            case R.id.btn_menu_notice://이용약관 보기
                //openWebBrowser(Env.UrlPath.PROVISION.url(), false);
                mActivity.callActivity(NoticeEventActivity.class, false);
                break;
        }
    }

    /** 탈퇴 경고 팝업 */
    private void withdrawal(){
        if(!mActivity.isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    requestWithdrawal();
                }
            };
            String msg = getString(R.string.warn_withdraw);
            mDialog =
                    new DialogCommon(mActivity,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{getString(R.string.txt_cancel), null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    /** 탈퇴 진행 */
    private void requestWithdrawal(){
        showSpinner("");
        final Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("id", KUtil.getStringPref(PrefKey.USER_ID, ""));
        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(mActivity)
                        .getClient(UserService.class)
                        .withdrawal(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                ResponseBase base;
                if(response.isSuccessful()){
                    base = response.body();
                    LogUtils.err(TAG, "profile=" + base.string());
                    if(base.isSuccess()){
                        Pref.instance().clear();
                        showWithdrawalResultPopup();
                    }else{
                        toast(R.string.warn_withdraw_fail);
                    }
                }else{
                    toast(R.string.warn_withdraw_fail);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    /** 회원탈퇴 완료 팝업 */
    private void showWithdrawalResultPopup(){

        if(!mActivity.isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    mActivity.sendBroadcast(new Intent(Env.Action.EXIT_ACTION.action()));
                    mActivity.finish();
                }
            };
            String msg = getString(R.string.warn_withdraw_finished);
            mDialog =
                    new DialogCommon(mActivity,
                            acceptor,
                            getString(R.string.txt_title_guide),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    /**
     * 로그아웃 처리. 빌딩코드 남김.
     * @date 2018.06.06. 로그아웃시 모든 저장 데이터 삭제하도록 수정
     */
    private void logout(){
        if(!mActivity.isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    Pref pref = Pref.instance();
                    /*pref.saveStringValue(PrefKey.USER_ID, "");
                    pref.saveStringValue(PrefKey.USER_TOKEN, "");
                    pref.saveStringValue(PrefKey.USER_NICK, "");
                    pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, "");
                    pref.saveStringValue(PrefKey.USER_CHARACTER, "");
                    pref.saveStringValue(PrefKey.PUSH_RECEIVE_FLAG, "");*/
                    pref.clear();
                    mActivity.sendBroadcast(new Intent(Env.Action.EXIT_ACTION.action()));
                    mActivity.stopService(new Intent(mActivity, BeaconRagingInRegionService.class));//비콘 서비스 종료
                    mActivity.finish();
                }
            };
            String msg = getString(R.string.warn_logout);
            mDialog =
                    new DialogCommon(mActivity,
                            acceptor,
                            getString(R.string.txt_title_guide),
                            msg,
                            new String[]{"취소", null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }

    }

    private void shareKosw(){
        String subject = "[" + getString(R.string.app_name) + "]";
        String text = "재미있는 계단어플 \"계단왕\" 깔고 오르면 끝!\n".concat(Env.Url.URL_SHARE.url());

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유할 앱을 선택하세요.");
        startActivity(chooser);
    }

    /** 푸시수신 상태 서버 반영 */
    private void changePushReceiveFlag(){
        showSpinner("");
        final Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("flag", (KUtil.isPushReceive()? "N" : "Y"));


        Call<ResponseBase> call =
                new DefaultRestClient<App>(mActivity).getClient(App.class)
                        .updatePushFlag(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                ResponseBase base;
                if(response.isSuccessful()){
                    base = response.body();
                    LogUtils.err(TAG, "profile=" + base.string());
                    if(base.isSuccess()){
                        KUtil.saveStringPref(PrefKey.PUSH_RECEIVE_FLAG, (String)query.get("flag"));
                        setPushSwitch();
                    }else{
                        toast(R.string.warn_push_fail);
                    }
                }else{
                    toast(R.string.warn_server_not_smooth);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void setPushSwitch(){
        MenuRow push = getView(R.id.btn_push_setting);
        if(KUtil.isPushReceive()){
            push.setIconRight(R.drawable.ic_swip_on);
        }else{
            push.setIconRight(R.drawable.ic_swip_off);
        }
    }

    private void changeAutoTrackingFlag(){
        setAutoTrackingSwitch();
    }

    private void setAutoTrackingSwitch(){
        MenuRow auto = getView(R.id.btn_auto_tracking_setting);
        String background = "";
        if(isMyServiceRunning(StepCounterService.class)) {
            auto.setIconRight(R.drawable.ic_swip_off);

            Intent walk_intent = new Intent(mActivity, StepCounterService.class);
            mActivity.stopService(walk_intent);
            //AppConst.IS_BACKGROUND = false;
            updateNotification();

            background = "manual";
        }else{
            auto.setIconRight(R.drawable.ic_swip_on);

            Intent walk_intent = new Intent(mActivity, StepCounterService.class);
            mActivity.startService(walk_intent);
            //AppConst.IS_BACKGROUND = true;
            updateNotification();

            background = "auto";
        }

        SharedPreferences prefr = mActivity.getSharedPreferences("background", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("background", background);
        editor.commit();
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity, AppConst.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "";
        if(!isMyServiceRunning(StepCounterService.class)) {
            title = "[수동측정]";
        } else {
            title = "[자동측정]";
        }

        builder.setContentTitle(title)
                .setContentText("[랭킹:" + AppConst.NOTI_RANKS + "] " + AppConst.NOTI_FLOORS + "F / " + AppConst.NOTI_CALS + "kcal / " + AppConst.NOTI_SECS + "sec")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_floor))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                //.setColorized(true)
                .setColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark))
                .setContentIntent(contentPendingIntent);

        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(AppConst.NOTIFICATION_ID, builder.build());
    }

    private void setInitAutoTrackingSwitch(){
        MenuRow auto = getView(R.id.btn_auto_tracking_setting);
        if(isMyServiceRunning(StepCounterService.class)) {
            auto.setIconRight(R.drawable.ic_swip_on);
        }else{
            auto.setIconRight(R.drawable.ic_swip_off);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /** 서버에서 사용자 정보를 획득  */
    private void getUserInfoFromServer(){
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("buildCode", KUtil.getStringPref(PrefKey.BUILDING_CODE, ""));
        LogUtils.log("user info by [buildCode, token]", query);
        Call<Profile> call =
                new DefaultRestClient<UserService>(getContext())
                        .getClient(UserService.class).getUserInfo(query);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                Profile profile;
                if(response.isSuccessful()){
                    profile = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if(profile.isSuccess()){
                        mDataHolder.setProfile(profile);
                        setInitialData();
                    }else{
                        toast(profile.getResponseMessage());
                        //mActivity.onBackPressed();
                    }
                }else{
                    toast(R.string.warn_server_not_smooth);
                    //mActivity.onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
                //mActivity.onBackPressed();
            }
        });
    }

    @Override
    protected void setInitialData() {


        Profile pf = mDataHolder.getProfile();
        AppUserBase user = DataHolder.instance().getAppUserBase() ;


        /*캐릭터 이미지*/
        CircleImageView cv = getView(R.id.ic_character);
        //String charUrl = KUtil.getMyCharacterUrl(false);
        String charUrl = KUtil.getSubCharacterImgUrl();
        LogUtils.err(TAG, "character url=" + charUrl);

        Glide.with(this)
                .applyDefaultRequestOptions(KUtil.getGlideCacheOption())
                .load( charUrl ).thumbnail(.5f).into(cv);
        //Glide.with(this).load(KUtil.getCharacterUrl(pf.getCharacterCode())).thumbnail(.5f).into(cv);

        String email = pf.getUserId();
        //String nickAndName ="\n하록선장 / 최인국\n포토인테리어\n정보관리\n가입일.2018.05.02";
        String nickAndName = getString(R.string.txt_profile_desc);
        /*nickAndName = MessageFormat.format(nickAndName,
                    pf.getNickName(),
                    getTextValue(pf.getUserName()),
                    getTextValue(pf.getCompanyName()),
                    getTextValue(pf.getDepartName()),
                    pf.getRegisterData()
                );*/
        /*
        nickAndName = MessageFormat.format(nickAndName,
                getTextValue(pf.getCompanyName()),
                StringUtil.isEmptyOrWhiteSpace(pf.getDepartName())?"":pf.getDepartName(),
                pf.getRegisterData()
        );
        */

        nickAndName = MessageFormat.format(nickAndName,
                pf.getNickName() ,
                pf.getRegisterData()
        );

        TextView info = getView(R.id.txt_user_info);
        SimpleSpanBuilder ssb = new SimpleSpanBuilder();
//        ssb.append(email,
//                new ForegroundColorSpan(getResources().getColor(R.color.color_38c1ec)),new RelativeSizeSpan(1.1f));
        ssb.append(nickAndName,
                new ForegroundColorSpan(getResources().getColor(R.color.color_1a1a1a)),new RelativeSizeSpan(1.1f));
        info.setText(ssb.build());

        MenuRow versionRow = getView(R.id.btn_app_version);
        String version = getString(R.string.txt_app_version);
        version = MessageFormat.format(version, AUtil.getVersionName(getContext(), mActivity.getPackageName()));
        versionRow.setMenuTitle(version);

        setPushSwitch();

    }

    private String getTextValue(String value){
        return StringUtil.isEmptyOrWhiteSpace(value) ? getString(R.string.txt_info_not_exist):value;
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_info_setting;
    }

    @Override
    public void onResume() {
        getUserInfoFromServer();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mDataHolder.clearProfile();
        super.onDestroy();
    }
}

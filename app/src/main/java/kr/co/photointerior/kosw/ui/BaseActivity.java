package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.service.beacon.AltitudeManager;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.service.beacon.DirectionManager;
import kr.co.photointerior.kosw.service.beacon.MeasureObj;
import kr.co.photointerior.kosw.service.beacon.StepManager;
import kr.co.photointerior.kosw.service.beacon.StepSensorService;
import kr.co.photointerior.kosw.service.net.NetworkConnectivityReceiver;
import kr.co.photointerior.kosw.service.noti.NotiService;
import kr.co.photointerior.kosw.service.noti.RestartService;
import kr.co.photointerior.kosw.service.stepcounter.StepCounterService;
import kr.co.photointerior.kosw.social.kakao.KakaoSignupActivity;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.ui.dialog.ProgressSpinnerDialog;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 하위 Activity class가 상속받아야 할 추상 Activity class입니다.
 * Created by kugie on 2018. 4. 30.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = LogUtils.makeLogTag(MainActivity.class);

    protected Dialog mSpinnerDialog;
    protected View mContentRootView;
    protected Toolbar mToolBar;
    protected DrawerLayout mDrawer;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected NavigationView mNavigationView;
    protected FrameLayout mContentFrame;
    public KoswApp app  ;

    /** 캐릭터 변경 리시버 */
    protected BroadcastReceiver mCharacterChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Env.Action.CHARACTER_CHANGED_ACTION.isMatch(intent.getAction())) {
                //updateCharacter();
            }
        }
    };

    protected BroadcastReceiver mMeasureStairActionDetectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Env.Action.APP_IS_BACKGROUND_ACTION.isMatch(intent.getAction())) {
                toast("수동 측정 중 by mMeasureStairActionDetectReceiver");
            }
        }
    };

    /** 카페 로고 획득 리시버 */
   /* protected BroadcastReceiver mCafeLogoGainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            toast("카페리스트 획득 성공 0 " + intent.getAction());
            if(Env.Action.CAFE_LOGO_GAIN_ACTION.isMatch(intent.getAction())) {
                //updateCharacter();
                updateCafeLogo();
            }
        }
    };*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (KoswApp) getApplication() ;
        checkBatteryWhiteList();

        //LocalBroadcastManager.getInstance(this)
        //        .registerReceiver(mMeasureStairActionDetectReceiver, new IntentFilter(Env.Action.APP_IS_BACKGROUND_ACTION.action()));

        // 죽지않는 서비스 구현
        /*restartService = new RestartService();
        Intent intent = new Intent(BaseActivity.this, NotiService.class);

        IntentFilter intentFilter = new IntentFilter("kr.co.photointerior.kosw.service.noti.NotiService");
        //브로드 캐스트에 등록
        registerReceiver(restartService,intentFilter);
        // 서비스 시작
        startService(intent);*/

        //Thread.setDefaultUncaughtExceptionHandler(((KoswApp)getApplication()).getUncaughtExceptionHandler());
    }

    /**
     * Action bar, drawer layout 초기화
     */
    public void initActionBarAndDrawer(){
        initToolbar();
        initDrawer();
//        mToolBar = getView(R.id.toolbar);
//        setSupportActionBar(mToolBar);
//        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setTitle("");
    }

    /**
     * Initialize action toolbar.
     */
    protected void initToolbar(){
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if( !(this instanceof MainActivity) ){
            setActionBarBaIcon(R.drawable.ic_top_arrow);
            mToolBar.setNavigationOnClickListener(v-> {
                onBackPressed();
            });
        }else{
            mToolBar.setNavigationOnClickListener(v-> {
                mDrawer.openDrawer(Gravity.START);
            });
        }

        mToolBar.findViewById(R.id.action_bar_notice).setOnClickListener(v-> {
            callActivity(NoticeEventActivity.class, false);
        });
    }

    protected void initDrawer(){
        mDrawer = getView(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //updateCharacter();
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = getView(R.id.nav_view);
    }


    public void setActionBarBaIcon(int imgResId){
        getSupportActionBar().setHomeAsUpIndicator(imgResId);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_actionbar_back);
    }

    /**
     * 앱 사용자가 속한 회사 칼라로 변경
     */
    public void changeColors(){
        int intColor = getCompanyColor();
        changeStatusBarColor(intColor);
        changeColors(intColor);
    }

    public void changeColorsBySharedPreferences(int intColor) {
        changeStatusBarColor(intColor);
        changeColors(intColor);
    }

    /**
     * 회사칼라 HEX 를 int로 변환한 것 반환.
     * @return
     */
    public int getCompanyColor(){
        int color = Pref.instance().getIntValue(PrefKey.COMPANY_COLOR_NUM,-1);
        int intColor ;
        if(color >= 0 ){
            intColor = getResources().getColor(DataHolder.instance().getBgColors()[color]);
        }else{
            intColor = getResources().getColor(R.color.colorPrimary);
        }
        return intColor;
    }
    /**
     * Action bar, background color 변경
     * @param color
     */
    public void changeColors(int color){
        if (mToolBar != null) {
            //mToolBar.setBackgroundColor(color);
        }
        if (mContentFrame != null) {
            mContentFrame.setBackgroundColor(color);
        }
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
    }

    /**
     * status bar color 변경
     * @param color
     */
    public void changeStatusBarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    /**
     * 상담 action bar, icon toggle.
     * @param isMain true-main screen
     */
    public void toggleActionBarResources(boolean isMain){
        if(isMain){//메인화면의 경우 소속된 회사의 색상
            mToolBar.setBackgroundColor(getCompanyColor());
            //setActionBarBaIcon(R.drawable.ic_top_arrow);
            setActionBarBaIcon(R.drawable.ic_menu);
            TextView title = mToolBar.findViewById(R.id.action_bar_title);
            title.setTextColor(getResources().getColor(R.color.colorWhite));
            setActionBarRightIcon(R.drawable.ic_notice);
            ImageView img_logo = mToolBar.findViewById(R.id.img_cafe_logo);
            img_logo.setVisibility(View.VISIBLE);
        }else{//white
            mToolBar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            TextView title = mToolBar.findViewById(R.id.action_bar_title);
            title.setTextColor(getResources().getColor(R.color.color_1a1a1a));
            title.setVisibility(View.VISIBLE);
            setActionBarBaIcon(R.drawable.ic_top_arrow);
            setActionBarRightIcon(R.drawable.ic_top_notice);
            ImageView img_logo = mToolBar.findViewById(R.id.img_cafe_logo);
            img_logo.setVisibility(View.GONE);

            title.setGravity(Gravity.CENTER);
        }
    }

    /**
     * Action bar 우상단 아이콘 제어
     * @param isRightIconShow true-visible
     * @param iconResId if the value is bigger than 0, the icon will be changed.
     */
    public void toggleActionBarRightIcon(boolean isRightIconShow, int iconResId){
        toggleActionBarRightIconVisible(isRightIconShow);
        setActionBarRightIcon(iconResId);
    }

    /**
     * 우상단 아이콘 숨김 토쿨
     * @param visible if 'false', the icon will be hidden.
     */
    public void toggleActionBarRightIconVisible(boolean visible){
        //mToolBar.findViewById(R.id.action_bar_notice).setVisibility(visible?View.VISIBLE:View.INVISIBLE);
        mToolBar.findViewById(R.id.notice_icon_box).setVisibility(visible?View.VISIBLE:View.INVISIBLE);
    }

    /**
     * 우상단 아이콘 변경
     * @param resId if the value is bigger than 0, the icon will be changed.
     */
    public void setActionBarRightIcon(int resId){
        ImageView rightIcon = mToolBar.findViewById(R.id.action_bar_notice);
        rightIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }
    /**
     * Sets the navigation title.
     * @param titleTxtId
     */
    public void setNavigationTitle(int titleTxtId){
        setNavigationTitle(getString(titleTxtId));
    }

    /**
     * Sets the navigation title.
     * @param title
     */
    public void setNavigationTitle(String title){
       ((TextView)mToolBar.findViewById(R.id.action_bar_title)).setText(title);
    }

    /**
     * Activiy를 시작합니다.
     * @param clz Activity class
     * @param bundle Activity를 호출할 때 넘겨줄 데이터
     */
    public void callActivity(Class<?> clz, @Nullable Bundle bundle){
        callActivity(clz, bundle, false);
    }

    /**
     * Activiy를 시작합니다.
     * @param clz Activity class
     * @param finishCaller 호출한 Activity를 종료할지 여부
     */
    public void callActivity(Class<?> clz, boolean finishCaller){
        callActivity(clz, null, finishCaller);
    }

    /**
     * Activiy를 시작합니다.
     * @param clz Activity class
     * @param bundle Activity를 호출할 때 넘겨줄 데이터
     * @param finishCaller 호출한 Activity를 종료할지 여부
     */
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public void callActivity(Class<?> clz, @Nullable Bundle bundle, boolean finishCaller){
        Intent it = new Intent(getBaseContext(), clz);
        if( bundle != null ){
            it.putExtras(bundle);
        }
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_half);
        if(finishCaller){
            finish();
            //overridePendingTransition(0,0);
        }
    }

    public void callActivity2(Class<?> clz, @Nullable Bundle bundle, boolean finishCaller){
        Intent it = new Intent(getBaseContext(), clz);
        if( bundle != null ){
            it.putExtras(bundle);
        }
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_half);
        if(finishCaller){
            finish();
            //overridePendingTransition(0,0);
        }
    }


    public void callActivity(Intent intent, boolean finishCaller){
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_right, 0);
        if(finishCaller){
            finish();
            //overridePendingTransition(0, 0);
        }
    }

    /**
     * 웹 브라우저 호출
     * @param url web url
     * @param finishCaller 호출한 activity 종료 여부
     */
    public void openWebBrowser(String url, boolean finishCaller){
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,
                    "Web browser does not exists on your smart phone.",  Toast.LENGTH_LONG).show();
        }
    }

    /**
     * The subclass must be implemented that the
     * {@link android.app.Fragment} is attached {@link android.app.Activity}'s view.
     * @param type the value for determine a fragment.
     */
    public void displayFragment(Env.FragmentType type){
        throw new IllegalStateException("This method body should be implemented by sub class.[displayFragment()]");
    }

    /**
     * 각 액티비티 구성 요소의 클릭 이벤트를 처리하도록 구현합니다.
     * @param view
     */
    public void performClick(View view){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    /**
     * Implement to handle event state when a click event occurs.
     * @param view
     * @param position event position
     */
    public void performClick(View view, int position){
        throw new IllegalStateException("This method body should be implemented by sub class.[performClick(View, int)]");
    }

    /**
     * 뷰 구성요소를 추출 하도록 구현합니다.
     */
    protected void findViews(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    /**
     * 뷰 구성요소에 이벤트를 할당 하도록 구현합니다.
     */
    protected void attachEvents(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    private Dialog mCommonDialog;

    /**
     * 일반적 목적의 다이알로그 팝업을 띄웁니다.
     * @param acceptor 버튼 클릭 후처리
     * @param titleId 제목
     * @param msgId 메세지
     * @param btntxtId 버튼 3개 배열
     * @param dismissListener 버튼 3개 배열
     * @param cancelable 버튼 3개 배열
     */
    public void popupDialog(Acceptor acceptor, int titleId, int msgId, int[] btntxtId,
                            DialogInterface.OnDismissListener dismissListener, boolean cancelable){
        String l = btntxtId[0] > 0 ? getString(btntxtId[0]) : null;
        String c = btntxtId[1] > 0 ? getString(btntxtId[1]) : null;
        String r = btntxtId[2] > 0 ? getString(btntxtId[2]) : null;

        popupDialog(
                acceptor,
                getString(titleId), getString(msgId),
                new String[]{l, c, r}, dismissListener, cancelable);
    }

    public void popupDialog(final Acceptor acceptor, String title, String msg, String[] btntxt,
                            DialogInterface.OnDismissListener dismissListener, boolean cancelable){
        if(!isFinishing()) {
            if (mCommonDialog != null) {
                mCommonDialog.dismiss();
            }

            mCommonDialog = new DialogCommon(
                    this, acceptor,
                    title, msg,
                    btntxt);
            if (dismissListener != null) {
                mCommonDialog.setOnDismissListener(dismissListener);
            }
            mCommonDialog.setCancelable(cancelable);
            mCommonDialog.show();
        }
    }

    public void showWarnPopup(int msgId){
        /*popupDialog(new AbstractAcceptor(){},
                R.string.txt_title_guide, msgId, new int[]{-1, -1, R.string.txt_confirm},
                null, false);*/
        showWarnPopup(getString(msgId));
    }

    public void showWarnPopup(String msg){
        popupDialog(new AbstractAcceptor(){},
                getString(R.string.txt_title_guide), msg, new String[]{null, null, getString(R.string.txt_confirm)},
                null, false);
    }

    public void showWarnPopup(Acceptor acceptor, String msg){
        popupDialog(acceptor,
                getString(R.string.txt_title_guide), msg, new String[]{null, null, getString(R.string.txt_confirm)},
                null, false);
    }

    /**
     * 생성시 최초로 데이터를 처리하도록 구현합니다.
     */
    protected void setInitialData(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    @Override
    public void finish() {
        if(mCommonDialog != null && mCommonDialog.isShowing()){
            mCommonDialog.dismiss();
        }
        if(mSpinnerDialog != null && mSpinnerDialog.isShowing()){
            mSpinnerDialog.dismiss();
        }
        super.finish();
    }

    protected TextView getTextView(int id){
        return (TextView)findViewById(id);
    }

    protected LinearLayout getLinearLayout(int id){
        return (LinearLayout)findViewById(id);
    }

    protected EditText getEditText(int id){
        return (EditText)findViewById(id);
    }
    protected ImageView getImageView(int id){
        return (ImageView)findViewById(id);
    }
    protected <T extends View> T getView(int id){
        return findViewById(id);
    }
    protected CheckBox getCheckBox(int id){
        return (CheckBox)findViewById(id);
    }
    protected String getText(TextView tv){
        return tv.getText().toString();
    }
    protected String getTextFromTextView(int resId){
        return getText(getTextView(resId));
    }
    public void toast(int id){
        toast(getString(id));
    }
    public void toast(String txt){
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }
    public void toastLong(int id){
        toastLong(getString(id));
    }
    public void toastLong(String txt){
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    /**
     * 스피너 프로그레스 다이알로그를 노출합니다.
     * @param msg 메세지
     */
    public void showSpinner(String msg){
        if(!isFinishing()) {
            if (mSpinnerDialog != null) {
                mSpinnerDialog.dismiss();
            }
            mSpinnerDialog = new ProgressSpinnerDialog(this, msg);
            mSpinnerDialog.setCancelable(false);
            mSpinnerDialog.setCanceledOnTouchOutside(false);
            mSpinnerDialog.show();
        }
    }
    public void showSpinner(int msgId){
        if(msgId > -1) {
            showSpinner(getString(msgId));
        }
    }
    /**
     * * 스피너 프로그레스 다이알로그를 숨깁니다.
     */
    public void closeSpinner(){
        if(!isFinishing()) {
            if (mSpinnerDialog != null) {
                mSpinnerDialog.dismiss();
            }
            mSpinnerDialog = null;
        }
    }


//    /**
//     * 화면을 구성할 Fragment를 Activity에 attach 하도록 구현.
//     * @param menuType
//     * @param  params Fragment로 전달해야 할 데이터
//     */
//    public void drawFragment(Env.MenuType menuType, Object... params){
//        throw new IllegalStateException("This method must be implemented by subclass.");
//    }

    public View getContentRootView(){
        return mContentRootView;
    }
    public void setContentRootView(View view){
        mContentRootView = view;
    }

    public void setContentRootViewBackground(int colorResId){
        if(mContentRootView != null){
            mContentRootView.setBackgroundColor(getResources().getColor(colorResId));
        }else{
            throw new IllegalArgumentException("The color resource ID #" + colorResId + " not found.");
        }
    }

//    public void loadNextFragment(Env.MenuType menuType, Object...params){
//        throw new IllegalStateException("This method must be implemented by subclass.");
//    }

//    /**
//     * 서버로 데이터 요청
//     * @param call
//     * @param acceptor
//     */
//    public void requestToServer(Call<ResponseData> call, final Acceptor acceptor){
//        showSpinner(null);
//        call.enqueue(new Callback<ResponseData>() {
//            @Override
//            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
//                closeSpinner();
//                if(response.isSuccessful()){
//                    acceptor.accept(response.body());
//                }else{
//                    toast(R.string.txt_info_lookup_fail);
//                    acceptor.deny();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseData> call, Throwable t) {
//                closeSpinner();
//                LogUtil.err("load fail", t);
//                toast(R.string.txt_info_lookup_fail);
//                acceptor.deny();
//            }
//        });
//    }

    public void setImageBitmap(final ImageView imageView, final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    public void setImageBitmap(final ImageView imageView, final InputStream inputStream){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //imageView.setImageBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 각 입력 필드의 유효성 경고 텍스트 노출
     * @param textViewId 노출할 텍스트 뷰 id
     * @param stringId 노출할 문구 id
     */
    public void showWarn(int textViewId, int stringId){
        showWarn(textViewId, getString(stringId));
    }

    public void showWarn(int textViewId, String msg){
        TextView tv = getTextView(textViewId);
        tv.setText(msg);
        tv.setVisibility(View.VISIBLE);
    }

    /**
     * 노출했던 경고 텍스트 숨김.
     * @param textViewId
     */
    public void hideWarn(int textViewId){
        getView(textViewId).setVisibility(View.INVISIBLE);
    }

    /**
     * @param btnId
     * @param enable
     */
    public void toggleViewEnable(int btnId, boolean enable){
        getView(btnId).setEnabled(enable);
    }

    @Override
    public void onBackPressed() {
        if(mDrawer != null) {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    /**
     * 좌상단 Drawer Icon 제어
     */
    public void toggleDrawerIcon(boolean isHome){
        if(isHome) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mToolBar.setNavigationOnClickListener(v->{
                mDrawer.openDrawer(Gravity.START);
            });
        }else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_top_arrow);
            mToolBar.setNavigationOnClickListener(v-> {
                    onBackPressed();
            });
        }
    }

    /**
     * Sets lock or unlock state of DrawerLayout.
     * @param lock
     */
    public void lockDrawerLayout(boolean lock){
        mDrawer.setDrawerLockMode(lock ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * 캐릭터를 변경하도록 구현
     */
    public void updateCharacter(){

    }

    /**
     * 액션바 로고 변경
     */
    public void updateCafeLogo() {
        toast("카페리스트 획득 성공 1");
    }

    public void clearNotificationBadge() {

    }

    /** 읽지 않은 공지가 있는가를 검사 후 있으면 공지 아이콘 붉은 색 표시 */
    protected void lookupNotReadNotices(){
        if(mToolBar != null && !(this instanceof NoticeEventActivity) ) {
            boolean has = KsDbWorker.hasNotReadBbs(getBaseContext());
            View icon = mToolBar.findViewById(R.id.ic_new);
            if(icon != null) {
                icon.setVisibility(has ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 키보드 완료버튼 클릭시 키보드 숨김처리 위한 것.
     */
    protected TextView.OnEditorActionListener mEditorAtionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if( actionId == EditorInfo.IME_ACTION_DONE){
                AUtil.toggleSoftKeyboard(getBaseContext(), textView, false);
                return true;
            }else if( actionId == EditorInfo.IME_NULL){
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    AUtil.toggleSoftKeyboard(getBaseContext(), textView, false);
                    return true;
                }
            }
            return false;
        }
    };

    private void clearReferences() {
        Activity currActivity = KoswApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            KoswApp.setCurrentActivity(null);
        }
    }

    private void checkBatteryWhiteList() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getPackageName());
            LogUtils.err(TAG, "white list=" + isWhiteListing);
            if (!isWhiteListing) {
                //startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));

                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }

        }
    }



    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    private boolean isMessureOn = false;
    private boolean isTest = false ;
    private Location mLocation ;
    private Address mAddr ;
    private boolean mStarted;
    private double mAltitude;
    private double mAltitude2;
    private double mOrientation;
    private double mX;
    private double mY;
    private double mZ;
    private double mStep;
    private int mFloor ;

    private int mSleepCnt = 0 ;
    static private  int mMaxSleepCnt = 10 ;
    static private  String sleepMsg[] = {"걷기중 측정시간(30초)이 지났습니다.","계단이용 측정시간(5분)이 지났습니다."} ;
    private int sleepMode = 0 ;

    private double mSaveStep = 0 ;
    private Boolean isSleep = false ;

    private long startTime = 0 ;
    private long endTime = 0 ;
    private  Boolean isRedDot = false ;

    private AltitudeManager mAltiManager;
    private StepManager mStepManager;
    private DirectionManager mDirectionManager ;

    private  boolean isStart = false ;
    private  boolean isTurn = false ;
    private  boolean isContinue = false ;
    // Create the Handler
    private Handler handler = new Handler();
    private Handler processHandler = new Handler();
    private ArrayList<MeasureObj> mStartList = new ArrayList<>() ;

    private int cnt = 0 ;
    private Boolean is315 = false ;
    private  int cnt315 = 0 ;
    private  Boolean isCount = false ;

    private long goupTime = 0 ;

    // 건물 층 카운트
    private int mBuildCount = 0 ;
    private int mCurBuildCount = 0 ;

    // 건물 / 등산 모드
    private  String mIsBuild = "" ; // Y :건물계단    N : 등산계단
    private  int mClimbCount = 0 ;  // 등산 카운트 (4미터 체크)
    private  int mLogicCount = 0 ;  // 로직 카운트 (회전 )

    private RestartService restartService;

    protected void startCalcStairs() {
        try {
            getView(R.id.btn_pause).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMeasure(false);
                    startMeasure(true);
                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;

                    displayFragment(Env.FragmentType.HOME);
                }
            });
        } catch (Exception ex) {
        }

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        mIsBuild = "N" ;

        mStartList.clear();
        for (int i = 0; i < 600; i++) {
            mStartList.add(new MeasureObj());
        }


        mAltiManager = new AltitudeManager(this);
        mStepManager = new StepManager(this) ;
        mDirectionManager = new DirectionManager(this) ;

        mSleepCnt = 0 ;


        mStarted = true;
        startMeasure(true);
        handler.post(runnable);
        processHandler.post(checkProcessRunnable);

        SharedPreferences prefr = getSharedPreferences("background", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("background", "manual");
        editor.commit();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            if (mStarted) {
                checkFloor();
            }
            // Repeat every 2 seconds
            handler.postDelayed(runnable, 100);
        }
    };

    private Runnable checkProcessRunnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here

            if (isAppOnForeground()) {
                if (mStarted == false) {
                   // toast("FORE? START MEASURE" + isAppOnForeground());

                    stopService(new Intent(getBaseContext(), StepCounterService.class));
                    mStarted = false;
                    startMeasure(mStarted);
                    handler.removeCallbacks(runnable);
                    try {
                        Thread.sleep(300);
                    } catch (Exception ex) {
                    }
                    mStarted = true;
                    startMeasure(mStarted);
                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;
                    handler.post(runnable);
                }
            } else {
                //toast("FORE?" + isAppOnForeground());
                mStarted = false;
                startMeasure(mStarted);
                handler.removeCallbacks(runnable);
            }
            // Repeat every 60 seconds
            processHandler.postDelayed(checkProcessRunnable, 60*1000);
        }
    };

    public void checkFloor() {
        checkStart();
    }

    private  void checkStart() {
        try {
            if (mAltitude == 0) {
                //return ;
            }
            Log.d("999999999999", cnt + "__");
            // 30초 이상 걷기 없으면 잠금
            if (cnt >= 30 * 10) {
                if (mSaveStep <= 0) { // 걷기중이아니면
                    mSleepCnt = mMaxSleepCnt;
                    sleepMode = 0;
                    initMeasure();
                    return;
                } else {  // 120초이상 측정이 없으면 측정 잠금  mSleepCnt >= 4
                    sleepMode = 1;
                    mSleepCnt++;
                    initMeasure();
                    return;
                }
            }

            mSaveStep = 0;

            MeasureObj obj = mStartList.get(cnt);
            obj.altitude = mAltitude;
            obj.orientation = mOrientation;
            obj.step = mStep;
            obj.x = mX;
            obj.y = mY;
            obj.z = mZ;

            MeasureObj obj_b = mStartList.get(0);
            obj.altitudeGap = obj.altitude - obj_b.altitude;
            obj.orientationGap = Math.abs(obj.orientation - obj_b.orientation);
            obj.stepGap = Math.abs(obj.step - obj_b.step);
            obj.xGap = Math.abs(obj.x - obj_b.x);
            obj.yGap = Math.abs(obj.y - obj_b.y);
            obj.zGap = Math.abs(obj.z - obj_b.z);

            // 고도 합계
            double gapAlitude = 0;
            gapAlitude = obj.altitude - obj_b.altitude;

            // 스텝 합계
            double gapStep = 0;
            gapStep = obj.step - obj_b.step;
            if (cnt > 50) {
                MeasureObj obj_c;
                obj_c = mStartList.get(cnt - 50);
                obj.stepGap = Math.abs(obj.step - obj_c.step);
                gapStep = obj.step - obj_c.step;
            }

            double step = gapStep; // 초당 걸음수
            mSaveStep = step;

            List<Double> list = Arrays.asList(obj.xGap, obj.yGap, obj.zGap);
            Double mDir = Collections.max(list);

            //Log.v("kmj",String.format("count %d ,높이 :  %.2f , 걸음수 : %.2f , 방향 : %.2f",cnt,gapAlitude,step,mDir)) ;
            //tvStep.setText(String.format("스텝 : %.2f" ,step));
            //mValue.setText(String.format("높이 : %.2fm \n 방향  %.2f " , gapAlitude ,mDir)  );
            //getTextView(R.id.txt_m).setText("");
            String m = String.format("높이 : %.2f , 방향 : %.2f , 걷기 : %.2f , 시간 : %d", gapAlitude, mDir, step, cnt);
            if (isTest) {
                // ==========================   test    ==============================
                getTextView(R.id.txt_height).setText(String.format("높이 : %.2f", gapAlitude));
                getTextView(R.id.txt_dir).setText(String.format("방향 : %.2f", mDir));
                getTextView(R.id.txt_step).setText(String.format("걷기 : %.2f", step));
                getTextView(R.id.txt_time).setText(String.format("시간 : %d (%d)", 300 - cnt, mSleepCnt));
                getTextView(R.id.txt_count).setText(String.format("%d  (B:%d,M:%d)", mFloor, mLogicCount, mClimbCount));
                if (mAddr != null && mAddr.getAddressLine(0) != null) {
                    getTextView(R.id.txt_addr).setText(String.format("%s", mAddr.getAddressLine(0)));
                }
                if (mLocation != null) {
                    getTextView(R.id.txt_gps).setText(String.format("%s,%s", String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude())));
                }
            }

            if (!isContinue) {
                if (mDir > 135 && Math.abs(gapAlitude) > 1.5) {

                    // 자동측정일 경우, 5초 이내 측정이면  카운트 하지 않음 엘리베이터 사용자 걸름
                    // 수동측정은 2초
                    long curTime = System.currentTimeMillis();
                    if (cnt < 30 || (curTime - goupTime) < 3000) {
                        mSleepCnt++;
                        initMeasure();
                        return;
                    }

                    if (step > 1) { // 걷기중이면

                        AppUserBase user = DataHolder.instance().getAppUserBase();
                        mCurBuildCount = user.getBuild_floor_amt();
                        mIsBuild = user.getIsbuild();

                        if (gapAlitude > 0) {
                            mFloor++;
                            mLogicCount++;
                            mClimbCount = 0;

                            if (mIsBuild.equals("Y")) {
                                mBuildCount++;
                            } else {
                                // 건물 모드 :건물높이의 1⁄2 이상이면 건물로 전환
                                if (mLogicCount >= Math.ceil(mCurBuildCount / 2)) {
                                    mIsBuild = "Y";
                                    mBuildCount = mLogicCount;
                                }
                            }

                            if (mBuildCount == 1) {
                                startTime = System.currentTimeMillis();
                            }

                            if (mBuildCount == Math.ceil(mCurBuildCount / 2) && mBuildCount >= 4) {
                                endTime = System.currentTimeMillis();
                                isRedDot = true;
                            } else {
                                isRedDot = false;
                            }
                            isCount = true;
                            if (isTest) {
                                getTextView(R.id.txt_m).setText(m);
                            }

                            goupTime = System.currentTimeMillis();
                            sendDataToServer(1);
                        } else {
                            mBuildCount = 0;
                            mClimbCount = 0;
                            mLogicCount = 0;

                            mFloor++;
                            isCount = true;
                            if (isTest) {
                                getTextView(R.id.txt_m).setText(m);
                            }
                            goupTime = System.currentTimeMillis();
                            sendDataToServer(1);
                        }

                        // 90이상이면 높이는 클리어  방향은 보존
                        MeasureObj obj_bb = mStartList.get(0);
                        isContinue = true;
                        mStartList.clear();
                        for (int i = 0; i < 600; i++) {
                            mStartList.add(new MeasureObj());
                        }
                        Log.d("9999999999", "CNT 0 point1");
                        cnt = 0;
                        mStartList.get(0).x = obj_bb.x;
                        mStartList.get(0).y = obj_bb.y;
                        mStartList.get(0).z = obj_bb.z;
                        mStartList.get(0).xGap = obj_bb.xGap;
                        mStartList.get(0).yGap = obj_bb.yGap;
                        mStartList.get(0).zGap = obj_bb.zGap;
                        mStartList.get(0).step = obj_bb.step;
                        mStartList.get(0).altitude = mAltitude;

                        cnt++;
                        return;

                    }
                }

            }

            if (mDir > 315 && isContinue) {  //
                // 315 이상이면 높이는 클리어  방향은 보존
                MeasureObj obj_bb = mStartList.get(0);
                isContinue = false;
                for (int i = 0; i < cnt; i++) {
                    mStartList.get(i).x = obj_bb.x;
                    mStartList.get(i).y = obj_bb.y;
                    mStartList.get(i).z = obj_bb.z;
                    mStartList.get(i).xGap = obj_bb.xGap;
                    mStartList.get(i).yGap = obj_bb.yGap;
                    mStartList.get(i).zGap = obj_bb.zGap;
                    mStartList.get(i).step = obj_bb.step;
                    mStartList.get(i).altitude = mAltitude;
                }
                mStartList.get(cnt).x = obj_bb.x;
                mStartList.get(cnt).y = obj_bb.y;
                mStartList.get(cnt).z = obj_bb.z;
                mStartList.get(cnt).xGap = obj_bb.xGap;
                mStartList.get(cnt).yGap = obj_bb.yGap;
                mStartList.get(cnt).zGap = obj_bb.zGap;
                mStartList.get(cnt).step = obj_bb.step;
                mStartList.get(cnt).altitude = mAltitude;

                is315 = true;
                cnt315 = cnt;

                cnt++;
                return;

            }

            // 1초뒤에 방향만 클리어
            if (is315 && cnt > (cnt315 + 10)) {  //
                MeasureObj obj_bb = mStartList.get(0);

                for (int i = 0; i < cnt; i++) {
                    mStartList.get(i).x = mX;
                    mStartList.get(i).y = mY;
                    mStartList.get(i).z = mZ;
                    mStartList.get(i).step = obj_bb.step;
                    mStartList.get(i).altitude = obj_bb.altitude;
                }
                mStartList.get(cnt).x = mX;
                mStartList.get(cnt).y = mY;
                mStartList.get(cnt).z = mZ;
                mStartList.get(cnt).step = obj_bb.step;
                mStartList.get(cnt).altitude = obj_bb.altitude;
                is315 = false;
                cnt315 = 0;

                cnt++;
                return;
            }

            //=================================
            // 회전이 없고 3미터 이상이면 1층 측정
            //=================================

            if (Math.abs(gapAlitude) > 3) {
                long curTime = System.currentTimeMillis();
                if (cnt < 20 || (curTime - goupTime) < 2000) {
                    //mSleepCnt++;
                    initMeasure();
                    return;
                } else {
                    if (step > 1) { // 걷기중이면
                        if (gapAlitude > 0) {

                            AppUserBase user = DataHolder.instance().getAppUserBase();
                            mCurBuildCount = user.getBuild_floor_amt();
                            mIsBuild = user.getIsbuild();
                            mClimbCount++;
                            mLogicCount = 0;

                            if (mIsBuild.equals("Y")) {
                                if (mClimbCount > mCurBuildCount) {
                                    mIsBuild = "N";
                                    mFloor++;
                                    mBuildCount = mClimbCount;

                                    if (mBuildCount == 1) {
                                        startTime = System.currentTimeMillis();
                                    }
                                    if (mBuildCount == Math.ceil(mCurBuildCount / 2) && mBuildCount >= 3) {
                                        endTime = System.currentTimeMillis();
                                        isRedDot = true;
                                    } else {
                                        isRedDot = false;
                                    }
                                    if (isTest) {
                                        getTextView(R.id.txt_m).setText(m);
                                    }
                                    goupTime = System.currentTimeMillis();
                                    sendDataToServer(1);
                                }
                            } else {
                                mBuildCount++;
                                mFloor++;
                                if (mBuildCount == 1) {
                                    startTime = System.currentTimeMillis();
                                }
                                if (mBuildCount == Math.ceil(mCurBuildCount / 2) && mBuildCount >= 3) {
                                    endTime = System.currentTimeMillis();
                                    isRedDot = true;
                                } else {
                                    isRedDot = false;
                                }
                                if (isTest) {
                                    getTextView(R.id.txt_m).setText(m);
                                }
                                goupTime = System.currentTimeMillis();
                                sendDataToServer(1);
                            }
                        } else {
                            mBuildCount = 0;
                            mClimbCount = 0;
                            mLogicCount = 0;
                            if (mIsBuild.equals("Y")) {
                                //mClimbCount++;
                            } else {
                                mFloor++;
                                if (isTest) {
                                    getTextView(R.id.txt_m).setText(m);
                                }
                                goupTime = System.currentTimeMillis();
                                sendDataToServer(1);
                            }
                        }
                        initMeasure();
                        return;


                    }
                }

            }

            if (mDir > 400) {
                initMeasure();
                return;
            }

            cnt++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 빌딩 층수 카운트 (빌딩 높이 )
    private void saveBuildCount(int count) {

    }

    // 측정 시작 ,멈춤
    public void startMeasure(boolean started) {
        try {
            if (!started) {

                saveBuildCount(mBuildCount);


                goupTime = System.currentTimeMillis();

                mAltitude = 0;
                mOrientation = 0;
                mStep = 0;
                mX = 0;
                mY = 0;
                mZ = 0;

                for (int i = 0; i < 600; i++) {
                    MeasureObj obj = mStartList.get(i);
                    obj.altitude = 0.0;
                    obj.orientation = 0.0;
                    obj.step = 0.0;
                    obj.x = 0.0;
                    obj.y = 0.0;
                    obj.z = 0.0;
                    obj.xGap = 0.0;
                    obj.yGap = 0.0;
                    obj.zGap = 0.0;
                }
                Log.d("9999999999", "CNT 0 point2");
                cnt = 0;

                mAltiManager.stopMeasure();
                mDirectionManager.stopMeasure();
                mStepManager.stopMeasure();
                try {
                    findViewById(R.id.LayoutPause).setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                }

               /*if(isMyServiceRunning(StepCounterService.class)) {
                   // 자동측정중이면
                   TextView tv =  findViewById(R.id.tvPauseMent);
                   tv.setText("자동측정중입니다.");
               }*/


            } else {


                AppUserBase user = DataHolder.instance().getAppUserBase();

                if (user != null) {
                    mCurBuildCount = user.getBuild_floor_amt(); // 현재 빌딩층수 (높이)
                }

                if (!isSleep) {
                    initMeasure();
                }

                goupTime = System.currentTimeMillis();

                mAltitude = 0;
                mOrientation = 0;
                mStep = 0;
                mX = 0;
                mY = 0;
                mZ = 0;

                for (int i = 0; i < 600; i++) {
                    MeasureObj obj = mStartList.get(i);
                    obj.altitude = 0.0;
                    obj.orientation = 0.0;
                    obj.step = 0.0;
                    obj.x = 0.0;
                    obj.y = 0.0;
                    obj.z = 0.0;
                    obj.xGap = 0.0;
                    obj.yGap = 0.0;
                    obj.zGap = 0.0;

                }
                cnt = 0;

                mAltiManager = new AltitudeManager(this);
                mStepManager = new StepManager(this);
                mDirectionManager = new DirectionManager(this);

                isSleep = false;

                mAltiManager.startMeasure();
                mDirectionManager.startMeasure();
                mStepManager.startMeasure();
                try {
                    findViewById(R.id.LayoutPause).setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                }
                //mValue.setText("wait...");
            }
            mStarted = started;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void restartMeasure(){
        if(!mStarted){
            Toast.makeText(this, "측정이 시작되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{
            mAltiManager.restartMeasure();
            //mValue.setText("wait...");
        }
    }

    public void setCurrentAltitude(double altitude){
        mAltitude = altitude;
        displayAlti();
    }
    public void setCurrentAltitude2(double altitude){
        mAltitude2 = altitude;
        displayAlti();
    }

    public void setCurrentOrientation(double val  ){
        mOrientation = val;
        displayAlti();
    }

    public void setCurrentOrientation2(double x ,double y, double z  ){
        mX = x;
        mY = y;
        mZ = z;
        //displayAlti();
    }

    public void setCurrentStep(double val  ){
        mStep += val;
        displayAlti();
    }

    private void displayAlti(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    private  void initMeasure() {
        mStartList.clear();

        if (mStartList.size() == 0) {
            for (int i = 0; i < 600; i++) {
                mStartList.add(new MeasureObj());
            }
        }

        cnt = 0 ;

        isStart = false ;
        isTurn = false ;
        isContinue = false ;
        is315 = false ;
        cnt315 = 0 ;

        isCount = false ;
        goupTime = System.currentTimeMillis();

        // 5분 지나면 잠금
        if (mSleepCnt >= mMaxSleepCnt ) {
            isSleep = true;;
            mSleepCnt = 0  ;
            mStarted = false ;
            try {
                getTextView(R.id.tvPauseMent).setText(sleepMsg[sleepMode]);
            } catch (Exception e) {}
            startMeasure(false);
        }

    }


    private void sendDataToServer(int goupAmt ){
        //Toast.makeText(this, "Send to Server2", Toast.LENGTH_SHORT).show();
        mSleepCnt = 0 ;

        String token = KUtil.getUserToken();
        String buildCode = KUtil.getBuildingCode();
        if(StringUtil.isEmptyOrWhiteSpace(token) || StringUtil.isEmptyOrWhiteSpace(buildCode)){
            return;
        }

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;

        if (user == null) return;
        //return;
        try {
            Map<String, Object> query = new HashMap<>();
            query.put("token",user.getUserToken()) ;
            query.put("beacon_uuid", "");
            query.put("major_value", "");
            query.put("minor_value", "");
            query.put("install_floor", "");
            //map.put("beacon_seq", getBeaconSeq());
            query.put("beacon_seq",  user.getBeacon_seq());
            if ( user.getBeacon_seq() == 0 ) {
                DefaultCode.BEACON_SEQ.getValue() ;
            }
            query.put("stair_seq", user.getStair_seq());
            query.put("build_seq", user.getBuild_seq());
            query.put("build_name", user.getBuild_name());
            query.put("floor_amount", 0);
            query.put("stair_amount", 0);
            query.put("cust_seq", user.getCust_seq());
            query.put("cust_name", user.getCust_name());
            query.put("build_code", user.getBuildingCode());
            query.put("godo", mAltitude);
            query.put("goup_amt", goupAmt);
            query.put("curBuildCount",mCurBuildCount) ;
            query.put("buildCount",mBuildCount) ;
            query.put("isbuild",mIsBuild) ;
            query.put("country",user.getCountry()) ;
            query.put("city",user.getCity()) ;

            if (isRedDot) {
                query.put("start_time", startTime);
                query.put("end_time", endTime);
            }

            final String localTime = DateUtil.currentDate("yyyyMMddHHmmss");
            if(NetworkConnectivityReceiver.isConnected(getApplicationContext())) {
                Call<BeaconUuid> call =
                        new DefaultRestClient<App>(getApplicationContext())
                                .getClient(App.class).sendStairGoUpAmountToServer(query);
                final String goupSentTime = DateUtil.currentDate("yyyyMMdd HHmmss");

                call.enqueue(new Callback<BeaconUuid>() {
                    @Override
                    public void onResponse(Call<BeaconUuid> call, Response<BeaconUuid> response) {
                        /*
                        if (isReddot) {
                            mReddotStack.clear();
                        }
                        */
                        LogUtils.e(TAG, "success-data response raw:" + response.raw().toString());
                        if (response.isSuccessful()) {
                            LogUtils.e(TAG, "success-data response body:" + response.body().string());

                            BeaconUuid uuid = response.body();
                            if (uuid != null && uuid.isSuccess()) {
                                broadcastServerResult(uuid);
                            }else {
                                saveGoUpDataToLocalDb(query, localTime);
                            }
                            //sendBeaconLog(beaconUuid, floorDiff, "go up ", goupSentTime);//층간이동 전송 성공하면 로그 전송
                        }else {
                            saveGoUpDataToLocalDb(query, localTime);
                        }
                    }

                    @Override
                    public void onFailure(Call<BeaconUuid> call, Throwable t) {
                        saveGoUpDataToLocalDb(query, localTime);
                    }
                });
            }else{
                saveGoUpDataToLocalDb(query, localTime);
            }
        }catch (Exception e){
        }
    }

    /**
     * 계단 올라간 데이터 서버전송 실패하면 로컬에 저장 후 네트워크가 안정화 되면 전송
     * @param goupData
     */
    private void saveGoUpDataToLocalDb(Map<String, Object> goupData, String localTime){
        goupData.put("app_time", localTime);
        if(KsDbWorker.insertFailData(getApplicationContext(), goupData)){
            BusProvider.instance().post(
                    new KsEvent<Map<String, Object>>()
                            .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT_FAIL)
                            .setValue(goupData)
                            .setMainCharacterChanged(false));
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 올라간 층수 데이터 서버전송 성공시 데이터 전파
     * @param beaconUuid
     */
    public static void broadcastServerResult(BeaconUuid beaconUuid){

        Pref pref = Pref.instance();

        //if (!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getMainCharFilename()) ) {
        pref.saveStringValue(PrefKey.CHARACTER_MAIN, beaconUuid.getMainCharFilename());
        //}
        //if(!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getSubCharFilenane())) {
        pref.saveStringValue(PrefKey.CHARACTER_SUB, beaconUuid.getSubCharFilenane());
        //}

        //LogUtils.err(TAG, beaconUuid.string());
        //String prevChar = KUtil.getStringPref(PrefKey.CHARACTER_MAIN, "");
        //boolean isMainChracterChanged = !StringUtil.isEquals(prevChar, beaconUuid.getMainCharFilename());
        boolean isMainChracterChanged = false ;

        String charImageFile = pref.getStringValue(PrefKey.CHARACTER_MAIN,"") ;

        // 기존 이미지와 동일 하면 스킵
        if ( !charImageFile.equals(beaconUuid.getMainCharFilename()) ) {
            isMainChracterChanged = true ;
        }

        if(isMainChracterChanged) {
            KUtil.saveStringPref(PrefKey.CHARACTER_MAIN, beaconUuid.getMainCharFilename());
        }

        if(!StringUtil.isEmptyOrWhiteSpace(beaconUuid.getSubCharFilenane())) {
            KUtil.saveStringPref(PrefKey.CHARACTER_SUB, beaconUuid.getSubCharFilenane());
        }

        BusProvider.instance().post(
                new KsEvent<BeaconUuid>()
                        .setType(KsEvent.Type.UPDATE_FLOOR_AMOUNT)
                        .setValue(beaconUuid)
                        .setMainCharacterChanged(isMainChracterChanged)
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unregisterReceiver(restartService);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }







    protected void measureDestroy() {
        stopService(new Intent(getBaseContext(), StepSensorService.class));
        mStepManager.stopMeasure();

        SharedPreferences prefr = getSharedPreferences("background", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("background", "auto");
        editor.commit();

        // 자동측정서비스 시작
        if (!isMyServiceRunning(StepCounterService.class)) {
            Intent startintent = new Intent(this, StepCounterService.class);
            startService(startintent);
            toast("수동 종료 / 서비스 측정 시작 by onDestroy");
        } else {
            toast("수동 종료 / 서비스 측정 이미 시작됨 by onDestroy");
        }
    }

    protected void measureStart() {
        /*toast("수동 시작 / 측정 시작 by measureStart");
        stopService(new Intent(getBaseContext(), StepCounterService.class));
        mStarted = false;
        startMeasure(mStarted);
        handler.removeCallbacks(runnable);
        try { Thread.sleep(300); } catch (Exception ex) { }
        mStarted = true;
        startMeasure(mStarted);
        handler.post(runnable);*/
    }

    protected void measureStop() {
        /*toast("수동 종료 / 서비스 측정 시작 by measureStop");
        mStarted = false;
        startMeasure(mStarted);
        handler.removeCallbacks(runnable);*/
    }


    protected void measurePause() {
        mStepManager.stopMeasure();

        SharedPreferences prefr = getSharedPreferences("background", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("background", "auto");
        editor.commit();

        // 자동측정서비스 시작
        Intent startintent = new Intent(this, StepCounterService.class);
        startService(startintent);

        toast("수동 종료 / 서비스 측정 시작 by onPause");

        handler.removeCallbacks(runnable);
        startMeasure(false);
    }

    protected void measureResume() {
        startService(new Intent(getBaseContext(), StepSensorService.class));
        mStepManager.startMeasure();

        SharedPreferences prefr = getSharedPreferences("background", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("background", "manual");
        editor.commit();

        // 자동측정서비스 종료
        Intent startintent = new Intent(this, StepCounterService.class);
        stopService(startintent);

        // 측정시작
        startMeasure(true);
        handler.post(runnable);

        toast("측정 시작 by onResume");

        LogUtils.err(TAG, "BaseActivity#onResume()");
        mStarted = true;
        mSleepCnt = 0;
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /** 캐릭터 변경 리시버 */
    /*protected BroadcastReceiver mIsAppBackgroundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Env.Action.APP_IS_BACKGROUND_ACTION.isMatch(intent.getAction())) {
                //updateCharacter();
                mStepManager.stopMeasure();

                SharedPreferences prefr = getSharedPreferences("background", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefr.edit();
                editor.putString("background", "auto");
                editor.commit();

                // 자동측정서비스 시작
                Intent startintent = new Intent(getApplicationContext(), StepCounterService.class);
                startService(startintent);

                toast("수동 종료 / 서비스 측정 시작 by onPause");

                handler.removeCallbacks(runnable);
                startMeasure(false);
            }
        }
    };*/
}

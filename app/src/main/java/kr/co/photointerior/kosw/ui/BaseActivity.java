package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.social.kakao.KakaoSignupActivity;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.ui.dialog.ProgressSpinnerDialog;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 하위 Activity class가 상속받아야 할 추상 Activity class입니다.
 * Created by kugie on 2018. 4. 30.
 */

public abstract class BaseActivity extends AppCompatActivity {
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
}

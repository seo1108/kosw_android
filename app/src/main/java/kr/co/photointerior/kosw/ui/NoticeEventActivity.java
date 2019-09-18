package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.ui.fragment.BaseFragment;
import kr.co.photointerior.kosw.ui.fragment.InfoSettingFragment;
import kr.co.photointerior.kosw.ui.fragment.NoticeEventFragment;
import kr.co.photointerior.kosw.ui.fragment.NoticeEventViewFragment;
import kr.co.photointerior.kosw.utils.Log;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 공지사항, 이벤트
 */
public class NoticeEventActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(NoticeEventActivity.class);
    private Dialog mDialog;
    private Bbs mArticle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_event);
        changeStatusBarColor(getCompanyColor());
        initToolbar();
        toggleActionBarRightIconVisible(false);
        displayFragment(Env.FragmentType.NOTICE_EVENT);
        Bundle bun = getIntent().getExtras();
        if( bun != null ){
            Bbs atc = (Bbs)bun.getSerializable("_NOTICE_");
            if(atc != null){
                new Handler().postDelayed(
                        ()->{
                            openBbsArticle(atc);
                        },
                100);
            }
        }

        app.push = null ;

    }

    @Override
    public void displayFragment(Env.FragmentType type) {
        LogUtils.d(TAG, "menu position=" + type.name());
        BaseFragment fragment = null;
        String title = getString(R.string.app_name);
        int code = type.code();
        switch(code){
            case 300://NOTICE_EVENT
                fragment = NoticeEventFragment.newInstance(this);
                title = getString(R.string.txt_notice);
                break;
            case 301://NOTICE_EVENT_OPEN
                fragment = NoticeEventViewFragment.newInstance(this, mArticle);
                title = getString(R.string.txt_notice);
                break;
        }

        if( fragment != null ){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(fragment.getClass().getSimpleName());
            transaction.commit();
            setNavigationTitle(title);
        }
    }

    public void openBbsArticle(Bbs article){
        mArticle = article;
        displayFragment(Env.FragmentType.NOTICE_EVENT_OPEN);
    }

    @Override
    protected void findViews() {}

    @Override
    protected void attachEvents() {}

    @Override
    public void performClick(View view) {}

    /**
     * 초기 데이터 설정
     */
    @Override
    protected void setInitialData() {

    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        LogUtils.err(TAG, "back stack count=" + backStackCount);
        if( backStackCount == 1 ){
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        }else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        toast("destroy");
        super.onDestroy();
    }
}

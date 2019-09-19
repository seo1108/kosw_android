package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.ui.fragment.BaseFragment;
import kr.co.photointerior.kosw.ui.fragment.InfoSettingFragment;
import kr.co.photointerior.kosw.ui.fragment.MainFragment;
import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.SimpleSpanBuilder;

/**
 * 정보설정
 */
public class InfoSettingActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(InfoSettingActivity.class);
    private Dialog mUpdateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting);
        changeStatusBarColor(getCompanyColor());
        initToolbar();
        setNavigationTitle(R.string.txt_info_setting);
        toggleActionBarRightIconVisible(false);
        displayFragment(Env.FragmentType.INFO_SETTING);

        setInitialData();
        getUserInfoFromServer();
    }

    @Override
    public void displayFragment(Env.FragmentType type) {
        LogUtils.d(TAG, "menu position=" + type.name());
        BaseFragment fragment = null;
        String title = getString(R.string.app_name);
        int code = type.code();
        switch(code){
            case 200://INFO_SETTING
                fragment = InfoSettingFragment.newInstance(this);
                title = getString(R.string.txt_info_setting);
                break;
        }

        if( fragment != null ){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
            setNavigationTitle(title);
        }
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

//        String logo = Pref.instance().getStringValue(PrefKey.COMPANY_LOGO, "");
//        String logoUrl = KUtil.getLogoUrl(logo);
//        ImageView logoView = getView(R.id.img_logo);
//        Glide.with(this).load(logoUrl).thumbnail(.5f).into(logoView);
    }

    /** 회원 정보 획득 */
    private void getUserInfoFromServer(){
        /*Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("version", AUtil.getVersionName(getBaseContext(), getPackageName()));
        queryMap = KUtil.getQueryMap(queryMap);

        App service = new DefaultRestClient<App>(getBaseContext()).getClient(App.class);
        Call<AppVersion> call = service.checkUpdate(queryMap);
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                AppVersion version;
                if(response.isSuccessful()){
                    version = response.body();
                    LogUtils.err(TAG, version.string());
                    if(version.isSuccess()){
                        doNextVersionProcess(version);
                    }else{
                        checkUserIdAndToken();
                    }
                }else{
                    checkUserIdAndToken();
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                toast(R.string.warn_commu_to_server);
                checkUserIdAndToken();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        measureStart();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        measureStop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        measureStop();
        super.onPause();
    }


    @Override
    protected void onResume() {
        measureStart();
        super.onResume();
    }
}

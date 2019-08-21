package kr.co.photointerior.kosw.ui;

import android.os.Bundle;
import android.view.View;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 최초 실행시 회사코드(계단코드)를 입렿 받는 화면
 */
public class CodeAcceptActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CodeAcceptActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_accept);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        doNextStep();
    }

    /**
     * 저장된 회사코드, 로고 URL, 칼라값 존재 유무에 따라 화면분기
     */
    private void doNextStep(){
        String code = Pref.instance().getStringValue(PrefKey.BUILDING_CODE, "");
        if(!StringUtil.hasEmptyOrWhiteSpace(code)){
           lookupCompanyCode(code);
        }
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_execute).setOnClickListener(clickListener);
        getView(R.id.btn_help_floor_code).setOnClickListener(clickListener);
        getEditText(R.id.input_code).setOnEditorActionListener(mEditorAtionListener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_execute){
            checkCode();
        }else{
            //openWebBrowser(Env.UrlPath.STAIR_CODE.url(), false);
            Bundle bun = new Bundle();
            bun.putString("_WHERE_IS_", "CODE_INPUT");
            callActivity(HelpActivity.class, bun, false);
        }
    }

    /**
     * 입력한 회사코드 유효성 검증
     */
    private void checkCode(){
        String inputCode = getTextFromTextView(R.id.input_code);
        if(StringUtil.isEmptyOrWhiteSpace(inputCode)){
            showWarn(R.id.input_warn, R.string.warn_code_required);
            return;
        }
        if(inputCode.length() < 7){
            showWarn(R.id.input_warn, R.string.warn_code_too_short);
            return;
        }
        lookupCompanyCode(inputCode);
    }

    /**
     * 입력 되었거나 Pref에서 획득한 회사코드로 로고, 칼라 조회
     */
    private void lookupCompanyCode(final String inputCode){
        //showSpinner("");
        Call<Logo> call = new DefaultRestClient<App>(getBaseContext())
                .getClient(App.class).lookupCompanyCode(Env.DEVICE_TYPE, inputCode);
        call.enqueue(new Callback<Logo>() {
            @Override
            public void onResponse(Call<Logo> call, Response<Logo> response) {
                //closeSpinner();
                Logo logo = null;
                if(response.isSuccessful()){
                    logo = response.body();
                    if(logo.isSuccess()){
                        saveLogoAndMoveToNext(logo);
                    }else {
                        showWarn(R.id.input_warn, logo.getResponseMessage());
                    }
                }else{
                    showWarn(R.id.input_warn, R.string.warn_code_error);
                }
            }

            @Override
            public void onFailure(Call<Logo> call, Throwable t) {
                //closeSpinner();
                LogUtils.err(call.toString(), t);
                //showWarn(R.id.input_warn, R.string.warn_code_error);
                callActivity(SplashActivity.class, true);
            }
        });

    }

    /**
     * 서버에서 받은 로고 정보를 저장 후 Splash 화면으로 이동.
     * @param logo
     */
    private void saveLogoAndMoveToNext(Logo logo){
        LogUtils.err(TAG, logo.string());
        Pref.instance().saveStringValue(PrefKey.BUILDING_CODE, logo.getBuildingCode());
        Pref.instance().saveStringValue(PrefKey.COMPANY_LOGO, logo.getLogoUrl());
        Pref.instance().saveStringValue(PrefKey.COMPANY_COLOR, logo.getHexColor());
        callActivity(SplashActivity.class, true);
    }

    @Override
    protected void setInitialData() {

    }
}

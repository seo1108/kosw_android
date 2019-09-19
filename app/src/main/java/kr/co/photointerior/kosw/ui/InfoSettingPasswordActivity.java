package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import java.text.MessageFormat;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
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
 * 비밀번호 찾기
 */
public class InfoSettingPasswordActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(InfoSettingPasswordActivity.class);
    private Dialog mUpdateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting_password);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {}

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.popup_close).setOnClickListener(listener);
        getView(R.id.btn_change).setOnClickListener(listener);
        getView(R.id.btn_cancel).setOnClickListener(listener);
        getEditText(R.id.input_pwd_new_confirm).setOnEditorActionListener(mEditorAtionListener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if( id == R.id.popup_close || id == R.id.btn_cancel){
            finish();
        }else{
            validatePassword();
        }
    }
    /** 입력 패스워드 유효성 검사 */
    private void validatePassword(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_pwd_new_confirm), false);
        String pwdCur = getEditText(R.id.input_pwd_current).getText().toString();
        boolean[] passed = new boolean[]{true, true, true};
        String warnTxt = "";
        if(StringUtil.isEmptyOrWhiteSpace(pwdCur) || pwdCur.length() < 8){
            //showWarn(R.id.input_warn_1, R.string.warn_pwd_invalid_current);
            warnTxt += getString(R.string.warn_pwd_invalid_current).concat("\n");
            passed[0] = false;
        }
        String pwdNew = getEditText(R.id.input_pwd_new).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(pwdNew) || pwdNew.length() < 8){
            //showWarn(R.id.input_warn_1, R.string.warn_pwd_invalid_new);
            warnTxt += getString(R.string.warn_pwd_invalid_new).concat("\n");
            passed[1] = false;
        }
        String pwdNewConf = getEditText(R.id.input_pwd_new_confirm).getText().toString();
        if(!StringUtil.isEquals(pwdNew, pwdNewConf)){
            //showWarn(R.id.input_warn_1, R.string.warn_pwd_mismatch_new);
            warnTxt += getString(R.string.warn_pwd_mismatch_new);
            passed[2] = false;
        }
        if(passed[0] && passed[1] && passed[2]) {
            changePassword(pwdCur, pwdNew);
        }else{
            showWarn(R.id.input_warn_1, warnTxt);
        }
    }

    /**
     * 패스워드 변경 진행
     * @param pwdCur
     * @param pwdNew
     */
    private void changePassword(String pwdCur, String pwdNew){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_pwd_current), false);
        showSpinner("");

        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("pc", pwdCur);
        queryMap.put("pn", pwdNew);
        queryMap.put("id", KUtil.getStringPref(PrefKey.USER_ID, ""));

        queryMap = KUtil.getQueryMap(queryMap);

        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).changePassword(queryMap);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                ResponseBase result;
                if(response.isSuccessful()){
                    result = response.body();
                    if(result.isSuccess()){
                        showPasswordResult();
                    }else{
                        showWarn(R.id.input_warn_1, result.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_pwd_not_changed);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                toast(R.string.warn_commu_to_server);
            }
        });
    }

    private Dialog mDialog;
    /**
     * 패스워드 변경 성공시 안내 팝업
     */
    private void showPasswordResult(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    finish();
                }
            };
            String msg = getString(R.string.warn_pwd_changed);
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    @Override
    protected void setInitialData() {

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

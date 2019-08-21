package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import java.text.MessageFormat;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 비밀번호 찾기
 */
public class FindPasswordActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(FindPasswordActivity.class);
    private Dialog mUpdateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {}

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(listener);
        getView(R.id.btn_find).setOnClickListener(listener);
        getEditText(R.id.input_name).setOnEditorActionListener(mEditorAtionListener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if( id == R.id.btn_back){
            finish();
        }else{
            if(AUtil.isValidEmail(getEditText(R.id.input_name).getText().toString())){
                findOutPassword();
            }else{
                showWarn(R.id.input_warn, R.string.warn_email);
            }
        }
    }

    /**
     * 패스워드 찾기 진행
     */
    private void findOutPassword(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_name), false);
        showSpinner("");
        final String id = getEditText(R.id.input_name).getText().toString();

        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", id);
        queryMap = KUtil.getQueryMap(queryMap);

        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).findOutPassword(queryMap);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                ResponseBase result;
                if(response.isSuccessful()){
                    result = response.body();
                    if(result.isSuccess()){
                        showFindOutPasswordResult();
                    }else{
                        toast(result.getResponseMessage());
                    }
                }else{
                    toast(R.string.txt_find_pass_fail);
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
     * 패스워드 찾기 성공시 안내 팝업
     */
    private void showFindOutPasswordResult(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            final String email = getEditText(R.id.input_name).getText().toString();
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    finish();
                }
            };
            String msg = getString(R.string.txt_find_pass_result);
            msg = MessageFormat.format(msg, email);
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
}

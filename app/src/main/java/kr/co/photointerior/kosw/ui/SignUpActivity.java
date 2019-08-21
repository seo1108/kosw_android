package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Depart;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 계단왕 회원가입 화면.
 */
public class SignUpActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(SignUpActivity.class);
    private AppUser mAppUser;
    private Spinner mDeptSpinner;
    private boolean mAgree;
    private Dialog mSignUpSuccessDialog;

    //private com.kakao.usermgmt.LoginButton mBtnKakao;

    //private SignUpActivity.SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        changeStatusBarColor(getCompanyColor());
        DataHolder.instance().setAppUser(new AppUser());
        mAppUser = DataHolder.instance().getAppUser();
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        mDeptSpinner = getView(R.id.spinner_depart);
    }

    @Override
    protected void attachEvents() {
        ClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(clickListener);
        getView(R.id.btn_signup).setOnClickListener(clickListener);
        getView(R.id.check_agree).setOnClickListener(clickListener);
        getView(R.id.btn_provision_view).setOnClickListener(clickListener);
        getEditText(R.id.input_name).setOnEditorActionListener(mEditorAtionListener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_signup:
                checkInput();
                break;
            case R.id.check_agree:
                checkAgreeFlag();
                break;
            case R.id.btn_provision_view:
                //openWebBrowser(Env.UrlPath.PROVISION.url(), false);
                //callActivity(ProvisionActivity.class, false);
                Bundle bun = new Bundle();
                bun.putString("_HTML_URL_", Env.getHtmlUrl(Env.UrlPath.PROVISION));
                bun.putString("_HTML_TITLE_", "이용약관");
                callActivity(ProvisionActivity.class, bun, false);
                break;
//            case R.id.ac_login_fl_kakao_login:
//                mBtnKakao.performClick();
//                break;
        }
    }
//
//    // 카카오톡 초기화
//    private void initKakao() {
//        mBtnKakao = findViewById(R.id.com_kakao_login);
//        callback = new SignUpActivity.SessionCallback();
//        Session.getCurrentSession().addCallback(callback);
//        //Session.getCurrentSession().checkAndImplicitOpen();
//    }

    /**
     * 회원가입 입력사항 유효성 검사.
     */
    private void checkInput(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_email), false);
        //이메일
        String email = getEditText(R.id.input_email).getText().toString();
        if(!AUtil.isValidEmail(email)){
            showWarn(R.id.input_warn, R.string.warn_email);
            return;
        }
        //패스워드
        String pwdMsg = "";
        boolean[] pwdPassed = new boolean[]{true, true};
        String pwd = getEditText(R.id.input_pwd).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(pwd) || pwd.length() < 8){
            //showWarn(R.id.input_warn, R.string.warn_password);
            pwdMsg += getString(R.string.warn_password).concat("\n");
            pwdPassed[0] = false;
        }
        //패스워드 비교
        String pwdConf = getEditText(R.id.input_pwd_confirm).getText().toString();
        if(!StringUtil.isEquals(pwd, pwdConf)){
            //showWarn(R.id.input_warn, R.string.warn_password_mismatch);
            pwdMsg += getString(R.string.warn_password_mismatch).concat("\n");
            pwdPassed[1] = false;
        }

        if( !pwdPassed[0] || !pwdPassed[1]){
            showWarn(R.id.input_warn, pwdMsg);
            return;
        }

        //이름
        String name = getEditText(R.id.input_name).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(name)){
            showWarn(R.id.input_warn, R.string.warn_name);
            return;
        }
        //부서
        int deptPosition = 0;
        /*
        if(mAppUser.getCompany().getDepart().size() > 1) {//부서를 선택하세요. 안내문자열 row 제외.
            deptPosition = mDeptSpinner.getSelectedItemPosition();
            if (deptPosition == 0) {
                showWarn(R.id.input_warn, R.string.warn_depart);
                return;
            }
        }else{
            deptPosition = 0;
        }
        */


        //약관동의
        if(!mAgree){
            showWarn(R.id.input_warn, R.string.warn_provision_agree);
            return;
        }

        mAppUser.setUserId(email);
        mAppUser.setUserPwd(pwd);
        mAppUser.setUserName(name);
        if(deptPosition > 0) {
            mAppUser.setDepart(mAppUser.getCompany().getDepart().get(deptPosition));
        }
        mAppUser.setBuilding(new Building());
        mAppUser.getBuilding().setBuildingCode(Pref.instance().getStringValue(PrefKey.BUILDING_CODE, ""));
        LogUtils.e(TAG, mAppUser.string());
        submitUserDataToServer();
    }

    /**
     * 회원가입 데이터 서버 전송
     */
    private void submitUserDataToServer(){
        if (null == mAppUser.getUserId() || "".equals(mAppUser.getUserId())) {
            toast(R.string.txt_check_userid);
        } else if (null == mAppUser.getUserPwd() || "".equals(mAppUser.getUserPwd())) {
            toast(R.string.txt_check_password);
        } else {
            toggleViewEnable(R.id.btn_signup, false);
            showSpinner("");
            UserService service =
                    new DefaultRestClient<UserService>(getBaseContext())
                            .getClient(UserService.class);
            Call<AppUserBase> call = service.signUp(mAppUser.createSignUpQueryMap());
            call.enqueue(new Callback<AppUserBase>() {
                @Override
                public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                    closeSpinner();
                    AppUserBase user;
                    if(response.isSuccessful()){
                        user = response.body();
                        if(user != null && user.isSuccess()){
                            Pref.instance().saveStringValue(PrefKey.USER_TOKEN, user.getUserToken());
                            Pref.instance().saveStringValue(PrefKey.USER_ID, mAppUser.getUserId());
                            showEmailCertPopup();
                        }else{
                            //showWarn(R.id.input_warn, user.getResponseMessage());
                            showSignUpResultPopup(user.getResponseMessage());
                        }
                    }else{
                        toast(R.string.warn_signup_fail);
                    }
                    toggleViewEnable(R.id.btn_signup, true);

                }

                @Override
                public void onFailure(Call<AppUserBase> call, Throwable t) {
                    toast(R.string.warn_server_not_smooth);
                    toggleViewEnable(R.id.btn_signup, true);
                    closeSpinner();
                }
            });
        }
    }

    public void showSignUpResultPopup(String msg){
        if(!isFinishing()){
            if(mSignUpSuccessDialog != null){
                mSignUpSuccessDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {

                }
            };
            msg = MessageFormat.format(msg, mAppUser.getUserId());
            mSignUpSuccessDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mSignUpSuccessDialog.setCancelable(false);
            mSignUpSuccessDialog.show();
        }
    }

    /**
     * 회원가입 완료 후 이메일 인증/관리자 승인 후 사용 알림 팝업 노출
     */
    private void showEmailCertPopup(){
        if(!isFinishing()){
            if(mSignUpSuccessDialog != null){
                mSignUpSuccessDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    callActivity(SignUpExtraInfoActivity.class, true);//TODO 완료 후 true 바꿀 것.
                }
            };
            String msg = getString(R.string.txt_signup_success);
            msg = MessageFormat.format(msg, mAppUser.getUserId());
            mSignUpSuccessDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mSignUpSuccessDialog.setCancelable(false);
            mSignUpSuccessDialog.show();
        }
    }

    /**
     * 이용약관 동의 Flag 처리
     */
    private void checkAgreeFlag(){
        mAgree = !mAgree;
        getView(R.id.check_agree)
                .setBackgroundResource(
                        mAgree ? R.drawable.ic_check : R.drawable.button_border_545454_2dp);
        getView(R.id.check_provision_view).setVisibility(View.INVISIBLE);

    }

    /**
     * 앱에 저장된 빌딩코드 기준 회사정보 획득
     */
    @Override
    protected void setInitialData() {
        /*
        CustomerService service =
                new DefaultRestClient<CustomerService>(getBaseContext()).getClient(CustomerService.class);
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("code", Pref.instance().getStringValue(PrefKey.BUILDING_CODE, ""));
        Call<Company> call = service.getCompanyInfo(query);
        call.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                Company company;
                if(response.isSuccessful()){
                    LogUtils.err(TAG, response.body().toString());
                    company = response.body();
                    if(company.isSuccess()){
                        setInitialData(company);
                    }else{
                        toast(R.string.warn_company_info_not_found);
                    }
                }else{
                    toast(R.string.warn_commu_to_server);
                }
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                toast(R.string.warn_commu_to_server);
            }
        });
        */
    }

    /**
     * 서버에서 받은 회사 정보를 회원가입 Form에 주입.
     * @param company
     */
    private void setInitialData(Company company){
        mAppUser.setCompany(company);

        LogUtils.e(TAG, company.string());
        TextView tv = getView(R.id.input_company_name);
        tv.setText(company.getCompanyName());

        List<Depart> depart = company.getDepart();
        depart.add(0, new Depart(0, getString(R.string.txt_select_depart)));

        setDepartSpinnerData(company);
    }

    /**
     * 부서정보 드럽다운 구성
     * @param company
     */
    private void setDepartSpinnerData(Company company){
        mDeptSpinner.setAdapter(
                new ArrayAdapter<>(this, R.layout.row_depart_spinner, company.getDepartTitleList()));

    }
//
//    // 카카오톡
//    private class SessionCallback implements ISessionCallback {
//
//        @Override
//        public void onSessionOpened() {
//            List<String> keys = new ArrayList<>();
//            keys.add("properties.nickname");
//            keys.add("properties.profile_image");
//            keys.add("kakao_account.email");
//
//            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
//                @Override
//                public void onFailure(ErrorResult errorResult) {
//                    String message = "failed to get user info. msg=" + errorResult;
//                    Logger.d(message);
//                }
//
//                @Override
//                public void onSessionClosed(ErrorResult errorResult) {
//                    redirectSignUpActivity();
//                }
//
//                @Override
//                public void onSuccess(MeV2Response response) {
//                /*Logger.d("user id : " + response.getId());
//                Logger.d("email: " + response.getKakaoAccount().getEmail());
//                Logger.d("profile image: " + response.getKakaoAccount()
//                        .getProfileImagePath());
//                redirectMainActivity();*/
//                    String openid = String.valueOf(response.getId());
//                    String nickname = response.getNickname();
//                    String email = response.getKakaoAccount().getEmail();
//
//                    tryKakaoLogin(openid, email, nickname);
//                }
//            });
//        }
//
//        @Override
//        public void onSessionOpenFailed(KakaoException exception) {
//            if (exception != null) {
//                Logger.e(exception);
//            }
//        }
//    }

}

package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.Admin;
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
public class MakeCafeActivity extends BaseActivity {
    private String TAG = "kmj" ;
    private AppUser mAppUser;
    private Dialog mSendSuccessDialog;
    private Admin mAdmin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_cafe);
        changeStatusBarColor(getCompanyColor());
        DataHolder.instance().setAppUser(new AppUser());
        mAppUser = DataHolder.instance().getAppUser();
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
    }

    @Override
    protected void attachEvents() {
        ClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(clickListener);
        getView(R.id.btn_signup).setOnClickListener(clickListener);
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
        }
    }

    /**
     * 입력사항 유효성 검사.
     */
    private void checkInput(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_name), false);
        //전화
        String phone  = getEditText(R.id.input_phone).getText().toString();
        if(!isValidPhone(phone)){
            showWarn(R.id.input_warn,R.string.warn_phone);
            return;
        }
        //핸드폰
        String hp  = getEditText(R.id.input_hp).getText().toString();
        if(!isValidPhone(hp)){
            showWarn(R.id.input_warn,R.string.warn_phone);
            return;
        }
        //이름
        String name = getEditText(R.id.input_name).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(name)){
            showWarn(R.id.input_warn, R.string.warn_name);
            return;
        }
        submitUserDataToServer();
    }

    private Boolean isValidPhone(String phone) {

        if (  phone.length() >= 9 && phone.length() <= 12 ) {
            return  true ;
        }
        return false ;
    }

    /**
     * 회원가입 데이터 서버 전송
     */

    private void checkCafe(String email) {


        AppUserBase user = DataHolder.instance().getAppUserBase() ;

        HashMap<String,Object> query = new HashMap<>() ;
        query.put("email", email  ) ;
        query.put("build_seq", user.getBuild_seq()  ) ;


        UserService service =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class);
        Call<Admin> call = service.selectCafe(query);
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mAdmin = response.body();
                    //
                    loadDataCafe(mAdmin) ;
                }else{

                    mAdmin = null ;
                }

            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {
                mAdmin = null ;
            }

        });
    }

    private void loadDataCafe(Admin admin) {

        getEditText(R.id.input_name).setText(admin.getCust_name());
        getEditText(R.id.input_admin).setText(admin.getAdmin_name());
        getEditText(R.id.input_phone).setText(admin.getPost_phone());
        getEditText(R.id.input_hp).setText(admin.getAdmin_phone());


        getEditText(R.id.input_name).setEnabled(false);
        getEditText(R.id.input_admin).setEnabled(false);
        getEditText(R.id.input_phone).setEnabled(false);
        getEditText(R.id.input_hp).setEnabled(false);

        Button btn =  findViewById(R.id.btn_signup) ;
        btn.setText("카페 개설 신청중입니다");
        btn.setEnabled(false);

        if (admin.getApproval_flag().equals("Y") ) {
            btn.setText("카페 개설이 완료 되었습니다");
        }
    }

    private void submitUserDataToServer(){
        toggleViewEnable(R.id.btn_signup, false);
        HashMap<String,Object> query = new HashMap<>() ;

        showSpinner("");
        AppUserBase usr =  DataHolder.instance().getAppUserBase() ;

        // admin
        query.put("admin_seq",0) ;
        query.put("cust_seq",0) ;
        query.put("email",usr.getUserId() ) ;
        query.put("passwd",usr.getUserId() ) ;
        query.put("admin_name",getEditText(R.id.input_name).getText().toString() ) ;
        query.put("admin_phone",getEditText(R.id.input_hp).getText().toString() ) ;
        query.put("active_flag","N" ) ;

        // custumer
        query.put("cust_seq",0) ;
        query.put("cust_code","") ;
        query.put("build_seq",usr.getBuild_seq());
        query.put("cust_name", getEditText(R.id.input_name).getText().toString()) ;
        query.put("post_name", getEditText(R.id.input_admin).getText().toString()) ;
        query.put("post_email",usr.getUserId() ) ;
        query.put("post_phone",getEditText(R.id.input_phone).getText().toString()) ;
        query.put("cust_remarks","") ;
        query.put("approval_flag","N") ;
        query.put("user_auto_confirm_flag","N") ;

        CustomerService service =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class);
        Call<Company> call = service.addCompany (query);
        call.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                Company com;
                if(response.isSuccessful()){
                    com = response.body();
                    query.put("cust_seq",com.getCompanySeq()) ;
                    query.put("cust_code",com.getCust_code()) ;
                    addAdmin(query);

                }else{
                    toast(R.string.warn_cafe_fail);
                    closeSpinner();
                }

            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                toast(R.string.warn_server_not_smooth);
                toggleViewEnable(R.id.btn_signup, true);
                closeSpinner();

            }
        });
    }


    private void addAdmin(Map<String,Object> query){
        toggleViewEnable(R.id.btn_signup, false);

        UserService service =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class);
        Call<Admin> call = service.addAdmin(query);
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {
                Admin com;
                if(response.isSuccessful()  ) {
                    com = response.body();
                    showSignUpResultPopup("카페개설 요청이 등록되었습니다.") ;
                }else{
                    toast(R.string.warn_cafe_fail);

                }
                closeSpinner();
            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {
                toast(R.string.warn_server_not_smooth);
                toggleViewEnable(R.id.btn_signup, true);
                closeSpinner();

            }

        });
    }

    public void showSignUpResultPopup(String msg){
        if(!isFinishing()){
            if(mSendSuccessDialog != null){
                mSendSuccessDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    finish();
                }
            };
            msg = MessageFormat.format(msg, mAppUser.getUserId());
            mSendSuccessDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{null, null, getString(R.string.txt_confirm)});
            mSendSuccessDialog.setCancelable(false);
            mSendSuccessDialog.show();
        }
    }


    /**
     * 앱에 저장된 빌딩코드 기준 회사정보 획득
     */
    @Override
    protected void setInitialData() {

        AppUserBase usr = DataHolder.instance().getAppUserBase() ;
        String email = usr.getUserId() ;

        checkCafe(email);

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

package kr.co.photointerior.kosw.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.global.DefaultCode;
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
import kr.co.photointerior.kosw.rest.model.Companys;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Depart;
import kr.co.photointerior.kosw.rest.model.Departs;
import kr.co.photointerior.kosw.rest.model.MapBuildingUser;
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
 * 계단왕 회원가입 화면.
 */
public class InfoSignupCafeActivity extends BaseUserActivity {
    private String TAG = LogUtils.makeLogTag(InfoSignupCafeActivity.class);
    private Dialog mUpdateDialog;
    private List<Company> mCompanyList;
    private List<Depart> mDepartList;
    private Spinner mCompanySpinner;
    private Spinner mDeptSpinner;
    private Company mCompany ;
    private Depart mDepart ;
    private MapBuildingUser mAppUser = null ;
    private Boolean isOut = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_signup_cafe);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        mCompanySpinner = findViewById(R.id.spinner_company);
        mCompanySpinner.setOnItemSelectedListener(new CompanyOnItemSelectedListener());
        mDeptSpinner = findViewById(R.id.spinner_depart);
    }

    @Override
    protected void attachEvents() {

        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.popup_close).setOnClickListener(listener);
        getView(R.id.btn_signup).setOnClickListener(listener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if( id == R.id.popup_close ){
            finish();
        }else{//cafe signup
            hideWarn(R.id.input_warn);
            validateBuildingCode();
        }
    }


    public class CompanyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Company company =  mCompanyList.get(pos) ;
            if (company != null ) {
                lookupDepart(company.getCompanySeq());
            }
            Log.v("kmj",company.getCompanyName()) ;
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


    // 카페가입 여부
    private void checkCafe(String email) {

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        HashMap<String,Object> query = new HashMap<>() ;
        query.put("user_seq", user.getUser_seq() ) ;
        query.put("build_seq", user.getBuild_seq() ) ;

        UserService service =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class);
        Call<MapBuildingUser> call = service.selectSignCafe(query);
        call.enqueue(new Callback<MapBuildingUser>() {
            @Override
            public void onResponse(Call<MapBuildingUser> call, Response<MapBuildingUser> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful() && response.body().isSuccess()) {
                    mAppUser = response.body();

                    if (mAppUser.getCust_seq() == DefaultCode.CUST_SEQ.getValue()) {
                        mAppUser = null ;
                        lookupCompany();
                    } else {
                        loadDataCafe(mAppUser);
                    }
                }else{
                    mAppUser = null ;
                    lookupCompany();
                }

            }

            @Override
            public void onFailure(Call<MapBuildingUser> call, Throwable t) {
                mAppUser = null ;
                lookupCompany();
            }

        });
    }

    @SuppressLint("ResourceAsColor")
    private void loadDataCafe(MapBuildingUser user) {

        mCompanyList =  new ArrayList<>() ;
        mCompanyList.add(0, new Company(0, getString(R.string.txt_select_company)));
        mCompanyList.add(new Company(user.getCust_seq(),user.getCust_name())) ;

        int selected = 1;
        List<String> list = new ArrayList<>();
        for(int i = 0; i < mCompanyList.size(); i++ ){
            Company d = mCompanyList.get(i);
            list.add(d.getCompanyName());
        }
        mCompanySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, list));
        mCompanySpinner.setSelection(selected);
        mCompanySpinner.setEnabled(false);
        mDeptSpinner.setEnabled(false);

        Button btn =  findViewById(R.id.btn_signup) ;
        TextView title =  findViewById(R.id.title_signup) ;
        TextView commment = findViewById(R.id.txt_comment);

        btn.setTextColor(R.color.text_btn_click);
        isOut = false ;
        btn.setEnabled(false);

        if(user.getApproval_flag().equals("Y")) {
            btn.setText("카페 탈퇴하기");
            title.setText("가입된 카페(회사) 입니다.");
            commment.setText("탈퇴하시려면 상단 탈퇴하기 버튼을 눌러 주세요.");
            btn.setTextColor(Color.RED);
            isOut = true ;
            btn.setEnabled(true);
        } else {
            title.setText("가입 신청중 카페(회사) 입니다.");
            btn.setText("카페에 가입신청 중입니다");
        }

    }

    /** 회사정보 조회 */
    private void lookupCompany(){

        AppUserBase usr = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("token", "");
        query.put("build_seq", usr.getBuild_seq());

        LogUtils.log("companys code lookup", query);

        Call<Companys> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).selectAllCompany(query);
        call.enqueue(new Callback<Companys>() {
            @Override
            public void onResponse(Call<Companys> call, Response<Companys> response) {
                //LogUtils.err(TAG, response.raw().toString());
                //LogUtils.err(TAG, response.body().string());
                Companys companys;
                if(response.isSuccessful()){
                    companys = response.body();
                    if(companys.isSuccess()){
                         mCompanyList = companys.getCompanys() ;
                        setCompanysData(companys);
                    }else{
                        showWarn(R.id.input_warn, companys.getResponseMessage());
                        toast(companys.getResponseMessage());
                    }
                }else{
                    showWarn(R.id.input_warn, R.string.warn_company_error);
                }
                hideWarn(R.id.input_warn);
            }

            @Override
            public void onFailure(Call<Companys> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }


    /**
     * 서버에서 받은 빌딩 정보 뷰에 할당
     * @param companys
     */
    private void setCompanysData(Companys companys){
        mCompanyList = companys.getCompanys();
        mCompanyList.add(0, new Company(0, getString(R.string.txt_select_company)));

        int selected = 0;
        List<String> list = new ArrayList<>();
        for(int i = 0; i < mCompanyList.size(); i++ ){
            Company d = mCompanyList.get(i);
            list.add(d.getCompanyName());
        }
        mCompanySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, list));
        mCompanySpinner.setSelection(selected);
    }


    /** 부서 정보 조회 */
    private void lookupDepart(int cust_seq){

        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("cust_seq", cust_seq);

        LogUtils.log("departs code lookup", query);

        Call<Departs> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).selectDepart(query);
        call.enqueue(new Callback<Departs>() {
            @Override
            public void onResponse(Call<Departs> call, Response<Departs> response) {
                //LogUtils.err(TAG, response.raw().toString());
                //LogUtils.err(TAG, response.body().string());
                Departs departs;
                if(response.isSuccessful()){
                    departs = response.body();
                    if(departs.isSuccess()){
                        mDepartList = departs.getDeparts() ;
                        setDepartData(departs);
                    }else{
                        showWarn(R.id.input_warn, departs.getResponseMessage());
                        toast(departs.getResponseMessage());
                    }
                }else{
                    showWarn(R.id.input_warn, R.string.warn_depart_error);
                }
                hideWarn(R.id.input_warn);
            }

            @Override
            public void onFailure(Call<Departs> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    /**
     * 서버에서 받은 빌딩 정보 뷰에 할당
     * @param departs
     */
    private void setDepartData(Departs departs){
        mDepartList = departs.getDeparts();
        mDepartList.add(0, new Depart(0, getString(R.string.txt_select_depart)));

        AppUserBase usr = DataHolder.instance().getAppUserBase() ;

        int selected = 0;
        List<String> list = new ArrayList<>();
        for(int i = 0; i < mDepartList.size(); i++ ){
            Depart d = mDepartList.get(i);
            list.add(d.getDepartName());
            if ( mAppUser != null ) {
                if (d.getDepartSeq() == mAppUser.getDept_seq()) {
                    selected = i;
                }
            }
        }
        mDeptSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, list));
        mDeptSpinner.setSelection(selected);
    }

    /** 입력 유효성 검사 */
    private void validateBuildingCode(){
        //AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);
        if (isOut) {
            showOutPopup("현재 가입된 카페에 탈퇴 하시겠습니까 ?");
            return ;
        }

        if(mCompanyList != null && mCompanyList.size() > 1){
            int pos = mCompanySpinner.getSelectedItemPosition();
            if(pos == 0 ){
                showWarn(R.id.input_warn, R.string.warn_required_company);
                mCompany = null ;
                return;
            }else{
                mCompany = mCompanyList.get(pos);
            }
        }

        if(mDepartList != null && mDepartList.size() > 1){
            int pos = mDeptSpinner.getSelectedItemPosition();
            if(pos == 0 ){
                showWarn(R.id.input_warn, R.string.warn_required_depart);
                mDepart = null ;
                return;
            }else{
                 mDepart = mDepartList.get(pos);
            }
        }

        if (mCompany != null  && mDepart != null) {

            sendSignupCafe();
        }
    }

    /** 카페 가입 */
    private void sendSignupCafe(){
        //AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);
        //closeSpinner();

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;

        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("cust_seq", mCompany.getCompanySeq());
        queryMap.put("build_seq", user.getBuild_seq());
        queryMap.put("admin_seq", mDepart.getAdminSeq());
        queryMap.put("dept_seq", mDepart.getDepartSeq());
        queryMap.put("user_seq", user.getUser_seq());
        queryMap.put("default_flag", "N");
        queryMap.put("approval_flag", "N");

        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).updateCustDepart(queryMap);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                ResponseBase result = response.body() ;
                if(response.isSuccessful()){
                    changeBaseBuilding();
                    showSuccessPopup(result.getResponseMessage());
                } else {
                    toast(R.string.warn_signup_cafe_fail);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                toast(R.string.warn_commu_to_server);
            }
        });
    }

    /** 카페 탈퇴 */
    private void sendOutCafe(){
        //AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);
        //closeSpinner();

        int map_seq =  mAppUser.getMap_seq() ;

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;

        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("map_seq", map_seq);

        Call<ResponseBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).deleteCustDepart(queryMap);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                ResponseBase result = response.body() ;
                if(response.isSuccessful()){
                    user.setCust_seq(DefaultCode.CUST_SEQ.getValue());
                    changeBaseBuilding();
                    finish();

                } else {
                    toast(R.string.warn_signup_cafe_out_fail);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                toast(R.string.warn_commu_to_server);
            }
        });

    }


    /** 활동하는 빌딩 변경 */
    private void changeBaseBuilding(){
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        query.put("buildCode", user.getBuildingCode());
        query.put("buildSeq", user.getBuild_seq());
        query.put("build_Seq", user.getBuild_seq());
        query.put("user_Seq", user.getUser_seq());
        query.put("cust_Seq", user.getCust_seq());

        Call<AppUserBase> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class)
                        .switchBaseBuilding(query);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                AppUserBase base;

                if(response.isSuccessful()){
                    base = response.body();
                    if(base.isSuccess()){
                        /*KsDbWorker.replaceUuid(getBaseContext(), base.getBeaconUuidList());//UUID 교체*/
                        tryReLoginByToken(new AbstractAcceptor() {
                            @Override
                            public void accept() {
                                //showSuccessPopup();
                            }
                        });//회원정보 다시 획득
                    }else{
                        //toast(base.getResponseMessage());
                    }
                }else{
                    //toast(R.string.warn_stair_not_changed);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private Dialog mDialog;
    /**
     * 성공시 안내 팝업
     */
    private void showSuccessPopup(String msg){
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
            //String msg = getString(R.string.warn_building_added);
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

    private void showOutPopup(String msg){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    sendOutCafe();
                }
            };
            //String msg = getString(R.string.warn_building_added);
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{"취소", "", "확인"});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }


    @Override
    protected void setInitialData() {
        AppUserBase usr = DataHolder.instance().getAppUserBase() ;
        String email = usr.getUserId() ;

        checkCafe(email);

    }


    /*
    public void tryLoginByTokenToMain(){
        final String email = Pref.instance().getStringValue(PrefKey.USER_ID, "");
        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("id", email);
        LogUtils.log(queryMap);
        Call<AppUserBase> call =
                new DefaultRestClient<UserService>(getBaseContext())
                        .getClient(UserService.class).tryLoginByTokenAndId(queryMap);
        call.enqueue(new Callback<AppUserBase>() {
            @Override
            public void onResponse(Call<AppUserBase> call, Response<AppUserBase> response) {
                LogUtils.e(TAG, response.raw().toString());
                AppUserBase user;
                if(response.isSuccessful()){
                    closeSpinner();

                    user = response.body();
                    if(user.isSuccess()){
                        //LogUtils.e(TAG, "login data on splash=" + response.body().getTodayActivity().string());
                        LogUtils.err(TAG, "login data on splash=" + response.body().string());
                        if (mBuilding != null) {
                            user.setBuild_seq(mBuilding.getBuildingSeq());
                            user.setBuildingCode(mBuilding.getBuildingCode());
                            user.setBuild_lat(mBuilding.getLatitude());
                            user.setBuild_lng(mBuilding.getLongitude());
                            user.setBuild_name(mBuilding.getBuildingName());
                            user.setBuild_addr(mBuilding.getBuildingAddr());
                            user.setPlace_id(mBuilding.getPlace_id());
                        }
                        if (mBuildingStair != null) {
                            user.setStair_seq(mBuildingStair.getStair_seq());
                        }
                        if (mBeacon != null) {
                            user.setBeacon_seq(mBeacon.getBeacon_seq());

                        } else {
                            user.setBeacon_seq(DefaultCode.BEACON_SEQ.getValue());
                        }

                        if (mMapBuildingUser != null) {
                            user.setCust_seq(mMapBuildingUser.getCust_seq());
                            user.setCust_name(mMapBuildingUser.getCust_name());
                            user.setDept_seq(mMapBuildingUser.getDept_seq());
                            user.setDept_name(mMapBuildingUser.getDept_name());
                        }
                        storeUserTokenAndMovesToMain(user);
                    }else{
                        closeSpinner();

                        callActivity(LoginActivity.class, true);
                    }
                }else{
                    closeSpinner();

                    callActivity(LoginActivity.class, true);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                closeSpinner();

                callActivity(LoginActivity.class, true);
            }
        });
    }
    */
}

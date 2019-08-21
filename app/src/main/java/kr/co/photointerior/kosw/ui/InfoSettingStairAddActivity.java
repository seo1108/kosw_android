package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Depart;
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
 * 계단 추가 등록
 */
public class InfoSettingStairAddActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(InfoSettingStairAddActivity.class);
    private Dialog mUpdateDialog;
    private List<Depart> mDepartList;
    private Spinner mDeptSpinner;
    private Building mBuilding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting_stair_add);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {
        mDeptSpinner = findViewById(R.id.spinner_depart);
    }

    @Override
    protected void attachEvents() {

        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.popup_close).setOnClickListener(listener);
        getView(R.id.btn_confirm).setOnClickListener(listener);
        getView(R.id.btn_register).setOnClickListener(listener);
        getView(R.id.btn_cancel).setOnClickListener(listener);
        getEditText(R.id.input_building_code).setOnEditorActionListener(mEditorAtionListener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if( id == R.id.popup_close || id == R.id.btn_cancel){
            finish();
        }else if( id == R.id.btn_confirm ){//건물코드 조회
            lookupBuildingCode();
        }else{//건물 추가 등록
            hideWarn(R.id.input_warn);
            validateBuildingCode();
        }
    }

    /** 빌딩코드 조회 */
    private void lookupBuildingCode(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);
        String buildingCode = getEditText(R.id.input_building_code).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(buildingCode) ||
                buildingCode.length() < 7 ){
            showWarn(R.id.input_warn, R.string.warn_code_too_short);
            return;
        }
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("buildCode", buildingCode);

        LogUtils.log("building code lookup", query);

        Call<Building> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                    .getClient(CustomerService.class).lookupBuildingCode(query);
        call.enqueue(new Callback<Building>() {
            @Override
            public void onResponse(Call<Building> call, Response<Building> response) {
                LogUtils.err(TAG, response.raw().toString());
                LogUtils.err(TAG, response.body().string());
                Building building;
                if(response.isSuccessful()){
                    building = response.body();
                    if(building.isSuccess()){
                        mBuilding = building;
                        setBuildingData(building);
                    }else{
                        showWarn(R.id.input_warn, building.getResponseMessage());
                        toast(building.getResponseMessage());
                        clearEachField();
                    }
                }else{
                    showWarn(R.id.input_warn, R.string.warn_code_error);
                }
                hideWarn(R.id.input_warn);
            }

            @Override
            public void onFailure(Call<Building> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    private void clearEachField(){
        TextView companyName = getTextView(R.id.input_company_name);
        companyName.setText("");
        if(mDepartList != null) {
            mDepartList.clear();
        }
    }

    /**
     * 서버에서 받은 빌딩 정보 뷰에 할당
     * @param building
     */
    private void setBuildingData(Building building){
        TextView companyName = getTextView(R.id.input_company_name);
        companyName.setText(building.getCompanyName());
        mDepartList = building.getDepartList();
        mDepartList.add(0, new Depart(0, getString(R.string.txt_select_depart)));

        int selected = 0;
        List<String> list = new ArrayList<>();
        for(int i = 0; i < mDepartList.size(); i++ ){
            Depart d = mDepartList.get(i);
            list.add(d.getDepartName());
        }
        mDeptSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, list));
        mDeptSpinner.setSelection(selected);
    }

    /** 입력 유효성 검사 */
    private void validateBuildingCode(){
        AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);
        if(mBuilding == null){
            showWarn(R.id.input_warn, R.string.warn_code_invalid);
            return;
        }
        String buildingCode = getEditText(R.id.input_building_code).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(buildingCode) ||
                buildingCode.length() < 5 ){
            showWarn(R.id.input_warn, R.string.warn_code_invalid);
            return;
        }
        if(mDepartList != null && mDepartList.size() > 1){
            int pos = mDeptSpinner.getSelectedItemPosition();
            if(pos == 0 ){
                showWarn(R.id.input_warn, R.string.warn_required_depart);
                return;
            }else{
                mBuilding.setDepart(mDepartList.get(pos));
            }
        }
        addBuilding();
    }

    /** 빌딩정보 추가 */
    private void addBuilding(){
        /*AUtil.toggleSoftKeyboard(getBaseContext(), getEditText(R.id.input_building_code), false);*/
        showSpinner("");

        Map<String, Object> queryMap = KUtil.getDefaultQueryMap();
        queryMap.put("buildCode", mBuilding.getBuildingCode());
        if(mBuilding.getDepart() != null) {
            queryMap.put("departCode", mBuilding.getDepart().getDepartSeq());
        }else{
            queryMap.put("departCode", "0");
        }

        Call<Building> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class).addBuilding(queryMap);

        call.enqueue(new Callback<Building>() {
            @Override
            public void onResponse(Call<Building> call, Response<Building> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                Building result;
                if(response.isSuccessful()){
                    result = response.body();
                    LogUtils.err(TAG, "stair add res=" + response.body().string());
                    if(result != null) {
                        if (result.isSuccess()) {
                            if (result.getBeaconUuidList() != null) {
                                KsDbWorker.replaceUuid(getBaseContext(), result.getBeaconUuidList());//UUID 교체
                            }
                            showSuccessPopup(result.getResponseMessage());
                        } else {
                            showWarn(R.id.input_warn_1, result.getResponseMessage());
                        }
                    }else{
                        toast(R.string.warn_building_added_fail);
                    }
                }else{
                    toast(R.string.warn_building_added_fail);
                }
            }

            @Override
            public void onFailure(Call<Building> call, Throwable t) {
                closeSpinner();
                toast(R.string.warn_commu_to_server);
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

    @Override
    protected void setInitialData() {

    }
}

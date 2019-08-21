package kr.co.photointerior.kosw.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 계단 전환
 */
public class InfoSettingGoupActivity extends BaseUserActivity implements ItemClickListener{
    private String TAG = LogUtils.makeLogTag(InfoSettingGoupActivity.class);
    private Dialog mDialog;
    private RecyclerView mRecyclerView;
    private BuildingAdapter mAdapter;
    private Company mCompany;
    private List<Building> mList;
    private Building mSelectedBuilding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting_goup);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        requestBuildingInfo();
    }

    @Override
    protected void findViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new ClickListener(this);
        getView(R.id.popup_close).setOnClickListener(listener);
        getView(R.id.btn_switching).setOnClickListener(listener);
        getView(R.id.btn_cancel).setOnClickListener(listener);
    }

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if( id == R.id.popup_close || id == R.id.btn_cancel){
            finish();
        }else{//건물 변경
            changeBaseBuilding();
        }
    }

    /** 활동하는 빌딩 변경 */
    private void changeBaseBuilding(){
        showSpinner(-1);
        LogUtils.err(TAG, "selected building=" + (mSelectedBuilding == null));
        if(mSelectedBuilding == null ||
                mCompany.getCurrentBuilding().getBuildingSeq().equals(mSelectedBuilding.getBuildingSeq())){
            toast(R.string.warn_stair_not_selected);
            return;
        }
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("buildCode", mSelectedBuilding.getBuildingCode());
        query.put("buildSeq", mSelectedBuilding.getBuildingSeq());
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
                        KUtil.saveStringPref(PrefKey.BUILDING_CODE, mSelectedBuilding.getBuildingCode());
                        KUtil.saveStringPref(PrefKey.BUILDING_SEQ, mSelectedBuilding.getBuildingSeq());
                        /*KsDbWorker.replaceUuid(getBaseContext(), base.getBeaconUuidList());//UUID 교체*/
                        tryReLoginByToken(new AbstractAcceptor() {
                            @Override
                            public void accept() {
                                showSuccessPopup();
                            }
                        });//회원정보 다시 획득
                    }else{
                        toast(base.getResponseMessage());
                    }
                }else{
                    toast(R.string.warn_stair_not_changed);
                }
            }

            @Override
            public void onFailure(Call<AppUserBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    /**
     * 회원이 등록한 건물 정보 획득
     */
    private void requestBuildingInfo(){
        showSpinner(-1);
        Map<String, Object> query = KUtil.getDefaultQueryMap();

        LogUtils.log("buildings", query);

        Call<Company> call =
                new DefaultRestClient<CustomerService>(getBaseContext())
                        .getClient(CustomerService.class)
                        .getCompanyBuildings(query);
        call.enqueue(new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    mCompany = response.body();
                    if(mCompany != null && mCompany.isSuccess()){
                        mList = mCompany.getBuilding();
                        if(mList == null || mList.size() == 0){
                            toast(R.string.warn_stair_info_not_exists);
                        }
                    }else{
                        toast(R.string.warn_server_not_smooth);
                    }
                }else{
                    toast(R.string.warn_server_not_smooth);
                }
                if(mList == null){
                    mList = new ArrayList<>();
                }
                setInitialData();
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    @Override
    protected void setInitialData() {

        AppUserBase user =  DataHolder.instance().getAppUserBase() ;

        for( Building b : mList ){
            if (user.getBuild_seq().equals(b.getBuildingSeq())) {
                b.setSelected(true);
            }
        }
        mAdapter = new BuildingAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 성공시 안내 팝업
     */
    private void showSuccessPopup(){
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
            String msg = getString(R.string.warn_stair_changed);
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
    public void onItemClick(View view, int position) {
        mSelectedBuilding = mList.get(position);
        for( Building b : mList ){
            b.setSelected(false);
        }
        mSelectedBuilding.setSelected(true);
        mAdapter.notifyDataSetChanged();
    }

    class BuildingAdapter extends RecyclerView.Adapter<RowHolder>{
        private Context mContext;
        private List<Building> mList;
        private LayoutInflater mInflater;

        BuildingAdapter(Context context, List<Building> list){
            mContext = context;
            mList = list;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_view_stair, parent, false);
            RowHolder holder = new RowHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {
            Building item = mList.get(position);
            if  (item.getCompanyName().equals("미등록")) {
                holder.companyName.setText(item.getBuildingName());
            } else {
                holder.companyName.setText(item.getCompanyName().concat("-").concat(item.getBuildingName()));
            }
            if(item.isSelected()){
                holder.companyName.setBackgroundResource(R.color.color_00c5ed);
                holder.companyName.setTextColor(getResources().getColor(R.color.colorWhite));
            }else{
                holder.companyName.setBackgroundResource(R.color.colorWhite);
                holder.companyName.setTextColor(getResources().getColor(R.color.color_1a1a1a));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView companyName;
        public RowHolder(View itemView) {
            super(itemView);
            pickupView();
        }

        void pickupView(){
            companyName = itemView.findViewById(R.id.txt_company_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }
}

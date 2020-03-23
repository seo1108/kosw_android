package kr.co.photointerior.kosw.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.listener.FragmentClickListener;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CustomerService;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.GGRList;
import kr.co.photointerior.kosw.rest.model.GGRRow;
import kr.co.photointerior.kosw.rest.model.Ranking;
import kr.co.photointerior.kosw.rest.model.SpinnerRow;
import kr.co.photointerior.kosw.ui.BaseActivity;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 개인랭킹
 */
public class GGRFragment extends BaseFragment {
    private final String TAG = LogUtils.makeLogTag(GGRFragment.class);

    private int[] mBtnResId = {
      R.id.btn1101,
      R.id.btn1104,
      R.id.btn1102,
      R.id.btn1103
    };

    private int mSelectedBtnId = mBtnResId[0];

    private RecyclerView mGGRRecyclerView;
    private GGRAdapter mGGRAdapter;
    private GGRList mGGRList ;

    public static BaseFragment newInstance(BaseActivity context){
        GGRFragment frag = new GGRFragment();
        frag.mActivity = context;
        return frag;
    }

    @Override
    protected void followingWorksAfterInflateView() {
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        mActivity.toggleActionBarResources(false);
        mGGRRecyclerView = getView(R.id.rv1101);
        mGGRRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new FragmentClickListener(this);
        for( int rs : mBtnResId ){
            getView(rs).setOnClickListener(listener);
        }

    }

    @Override
    public void performFragmentClick(View view) {
        int id = view.getId();
        if (id == mSelectedBtnId) {
            return;
        }
        toggleBtn(id);
    }

    @Override
    public void requestToServer() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("build_seq",user.getBuild_seq() );
        query.put("cust_seq",user.getCust_seq() );
        Call<GGRList> call =
                new DefaultRestClient<CustomerService>(mActivity)
                        .getClient(CustomerService.class)
                        .getGGRHistory(query);
        call.enqueue(new Callback<GGRList>() {
            @Override
            public void onResponse(Call<GGRList> call, Response<GGRList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()) {
                    LogUtils.err(TAG, response.body().string());
                    GGRList ggrList = response.body();
                    if(ggrList.isSuccess()){
                        mGGRList= ggrList;
                        drawList();
                    }
                }
            }

            @Override
            public void onFailure(Call<GGRList> call, Throwable t) {
                closeSpinner();
            }
        });
    }

    /**
     * 결과 리스트 표시
     */
    private void drawList(){
        List<GGRRow> ggrList = new ArrayList<>();
        Boolean isKOM = false ;

        if (mSelectedBtnId == R.id.btn1101) {
            if (mGGRList != null && mGGRList.getGoldList() != null) {
                ggrList = mGGRList.getGoldList();
            }
        }
        if (mSelectedBtnId == R.id.btn1104) {
            if (mGGRList != null && mGGRList.getWalkList() != null) {
                ggrList = mGGRList.getWalkList();
            }
        }
        if (mSelectedBtnId == R.id.btn1102) {
            if (mGGRList != null && mGGRList.getGreenList() != null) {
                ggrList = mGGRList.getGreenList();
            }
        }
        if (mSelectedBtnId == R.id.btn1103) {
            isKOM = true ;
            if (mGGRList != null && mGGRList.getRedList() != null) {
                ggrList = mGGRList.getRedList();
            }
        }
        mGGRAdapter = new GGRFragment.GGRAdapter(mActivity, ggrList,null,isKOM);
        mGGRRecyclerView.setAdapter(mGGRAdapter);
    }
    @Override
    protected void setInitialData() {
        AppUserBase  user = DataHolder.instance().getAppUserBase() ;
        requestToServer();
    }


    private class GGRAdapter extends RecyclerView.Adapter<GGRAdapter.GGRViewHolder>{
        private Context mContext;
        private List<GGRRow> mItems;
        private LayoutInflater mInflater;
        private ItemClickListener mItemClickListener;
        private Boolean isKOM = false ;

        GGRAdapter(Context context, List<GGRRow> list, ItemClickListener listener,Boolean kom ){
            mContext = context;
            mItems = list;
            mItemClickListener = listener;
            mInflater = LayoutInflater.from(context);
            isKOM = kom ;
        }

        @NonNull
        @Override
        public GGRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = mInflater.inflate(R.layout.row_ggr_all, parent, false);

            return new GGRViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull GGRViewHolder holder, int position) {
            GGRRow ggrRow = mItems.get(position);

            String actDate =  ggrRow.getAct_date() ;
            holder.tv1101.setText(actDate.substring(0,4) + "." + actDate.substring(4,6) + "." + actDate.substring(6,8) );
            holder.tv1102.setText(ggrRow.getNickname());
            if (isKOM) {
                Double sec = Double.valueOf(ggrRow.getAct_sec()) / 10000.0 ;
                holder.tv1103.setText(StringUtil.format( sec ,"#,##0.00") +"초/층");
            } else{
                if (mSelectedBtnId != R.id.btn1104 ) {
                    holder.tv1103.setText(StringUtil.format(Double.valueOf(ggrRow.getAct_amt()), "#,##0") + "F");
                } else {
                    holder.tv1103.setText(StringUtil.format(Double.valueOf(ggrRow.getAct_amt()), "#,##0") + "걸음");
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }


        class GGRViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView tv1101;
            TextView tv1102;
            TextView tv1103;

            GGRViewHolder(View view, int viewType){
                super(view);
                pickupViews(viewType);
            }

            /**
             * @param type 0:me, ranking 1,2,3 and else
             */
            private void pickupViews(int type){
                tv1101 = itemView.findViewById(R.id.tv1101);
                tv1102 = itemView.findViewById(R.id.tv1102);
                tv1103 = itemView.findViewById(R.id.tv1103);
            }

            @Override
            public void onClick(View view) {
                //mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    private void toggleBtn(int clickId){
        for( int rs : mBtnResId ){
            TextView tv = getView(rs);
            if(rs == clickId){
                mSelectedBtnId = rs;
                tv.setTextColor(getResources().getColor(R.color.colorWhite));
                tv.setBackgroundColor(getResources().getColor(R.color.color_1a1a1a));
            }else{
                tv.setTextColor(getResources().getColor(R.color.color_545454));
                tv.setBackgroundColor(getResources().getColor(R.color.color_cccccc));
            }
            if (mSelectedBtnId == R.id.btn1103 ) {
                getTextView(R.id.tv2103).setText("시 간");
            }else {
                getTextView(R.id.tv2103).setText("층 수");
            }
        }

        drawList();
    }

    @Override
    public int getViewResourceId() {
        return R.layout.fragment_ggr;
    }
}

package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.CafeMyAllList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeChangeActivity extends BaseUserActivity {
    private String TAG = LogUtils.makeLogTag(CafeChangeActivity.class);
    private Acceptor mAcceptor;
    private RecyclerView mRecyclerView;
    private CafeAdapter mAdapter;
    private  ViewGroup mRootView;

    private List<Cafe> mCAdminList = new ArrayList<>();
    private List<Cafe> mCJoinList = new ArrayList<>();
    private List<Cafe> mCafeList = new ArrayList<>();

    private String mSelectedCafeSeq = "";
    private String mSelectedCafeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_list);
        changeStatusBarColor(getCompanyColor());
        mContentRootView = getWindow().getDecorView().findViewById(android.R.id.content) ;
        mRootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        SharedPreferences prefr = getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);
        mSelectedCafeSeq = prefr.getString("cafeseq", "");

        getUserCafeMyList();
        /*findViews();
        setInitialData();*/
       /* findViews();
        attachEvents();
        setInitialData();*/

    }

    @Override
    protected void findViews() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void attachEvents() {
        findViewById(R.id.popup_close).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });

        findViewById(R.id.btn_change).setOnClickListener(v->{
            saveLastSelectedCafe();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v->{
            finish();
            overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
        });
    }

    @Override
    protected void setInitialData() {
        mAdapter = new CafeAdapter(this, mCafeList);
        mAdapter.setClickListener(mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void saveLastSelectedCafe() {
        SharedPreferences prefr = getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefr.edit();
        editor.putString("cafeseq", mSelectedCafeSeq);
        editor.putString("cafename", mSelectedCafeName);
        editor.commit();

        finish();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }

    private void getUserCafeMyList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        Call<CafeMyAllList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).selectMyCafeMainList(query);
        call.enqueue(new Callback<CafeMyAllList>() {
            @Override
            public void onResponse(Call<CafeMyAllList> call, Response<CafeMyAllList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeMyAllList cafelist = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafelist.isSuccess()) {
                        mCAdminList = cafelist.getAdminList();
                        mCJoinList = cafelist.getJoinList();

                        mCafeList.addAll(mCAdminList);
                        mCafeList.addAll(mCJoinList);

                        boolean isSelected = false;
                        for (int i=0; i<mCafeList.size(); i++) {
                            if (mSelectedCafeSeq.equals(mCafeList.get(i).getCafeseq())) {
                                mCafeList.get(i).setSelected(true);
                                isSelected = true;
                                break;
                            }
                        }

                        if (!isSelected) {
                            mCafeList.get(0).setSelected(true);
                            mSelectedCafeSeq = mCafeList.get(0).getCafeseq();
                            mSelectedCafeName = mCafeList.get(0).getCafename();
                        }

                        findViews();
                        attachEvents();
                        setInitialData();
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<CafeMyAllList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }




    class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeHolder> {
        private Context context;
        private List<Cafe> list;
        private LayoutInflater inflater;
        private ItemClickListener itemClickListener;

        CafeAdapter(Context context, List<Cafe> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CafeAdapter.CafeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_cafe_list, parent, false);
            CafeAdapter.CafeHolder viewHolder = new CafeAdapter.CafeHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeAdapter.CafeHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();

            if(item.isSelected()){
                holder.tvCafename.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mSelectedCafeSeq = item.getCafeseq();
                mSelectedCafeName = item.getCafename();
            }else{
                holder.tvCafename.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            }
            holder.tvCafename.setText(cafename);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Cafe getItem(int id) {
            return list.get(id);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        class CafeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvCafename;

            public CafeHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type){
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                tvCafename.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    private void cafeSelectStatusControl(int position) {
        resetSelected();
        mCafeList.get(position).setSelected(true);
        mAdapter.notifyDataSetChanged();
    }

    private void resetSelected() {
        for (Cafe cf : mCafeList) {
            cf.setSelected(false);
        }
    }

    private ItemClickListener mItemClickListener = (view, position) -> cafeSelectStatusControl(position);



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

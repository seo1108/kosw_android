package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeMyAllList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeMainActivity extends BaseUserActivity {
    private String TAG = LogUtils.makeLogTag(CafeMainActivity.class);
    private Acceptor mAcceptor;
    private RecyclerView mRecyclerViewMine, getmRecyclerViewMy;
    private CafeMineAdapter mMineAdapter;
    private ViewGroup mRootView;

    private List<Cafe> mCAdminList = new ArrayList<>();
    private List<Cafe> mCJoinList = new ArrayList<>();
    private List<Cafe> mCafeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_main);
        changeStatusBarColor(getCompanyColor());
        mContentRootView = getWindow().getDecorView().findViewById(android.R.id.content) ;
        mRootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        getUserCafeMyList();
    }

    @Override
    protected void findViews() {
        mRecyclerViewMine = findViewById(R.id.recycler_view_mine);
        mRecyclerViewMine.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void attachEvents() {

    }

    @Override
    protected void setInitialData() {
        mMineAdapter = new CafeMineAdapter(this, mCAdminList);
        mRecyclerViewMine.setAdapter(mMineAdapter);
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


    class CafeMineAdapter extends RecyclerView.Adapter<CafeMineAdapter.CafeMineHolder> {
        private Context context;
        private List<Cafe> list;
        private LayoutInflater inflater;
        //private ItemClickListener itemClickListener;

        CafeMineAdapter(Context context, List<Cafe> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CafeMineAdapter.CafeMineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_cafe_list, parent, false);
            CafeMineAdapter.CafeMineHolder viewHolder = new CafeMineAdapter.CafeMineHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeMineAdapter.CafeMineHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();
            holder.tvCafename.setText(cafename);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Cafe getItem(int id) {
            return list.get(id);
        }

        class CafeMineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvCafename;

            public CafeMineHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type) {
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                tvCafename.setOnClickListener(this);
            }

                @Override
                public void onClick(View view) {
                }
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeMainActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeMainActivity.class);
    private RecyclerView mRecyclerViewMine, mRecyclerViewMy, mRecyclerViewFind;
    private CafeMineAdapter mMineAdapter;
    private CafeMyAdapter mMyAdapter;
    private CafeAdapter mAdapter;
    private ViewGroup mRootView;

    private List<Cafe> mCAdminList = new ArrayList<>();
    private List<Cafe> mCJoinList = new ArrayList<>();
    private List<Cafe> mList = new ArrayList<>();

    private Button btnMake, btnFind, btnGuide;
    private KoswButton btnJoin;
    private KoswTextView txt_privacy, txt_cafe_mine, txt_cafe_my;
    private KoswEditText txt_cafekey;
    private ImageView btn_back, btn_config;
    private LinearLayout ll_cafe_mine_list, ll_cafe_my_list, ll_find;
    private KoswEditText input_name;

    private int mResultCode = 100;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_cafe_main);
        changeStatusBarColor(getCompanyColor());
        mContentRootView = getWindow().getDecorView().findViewById(android.R.id.content) ;
        mRootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        getUserCafeMyList();
    }

    @Override
    protected void findViews() {
        ll_cafe_mine_list = findViewById(R.id.ll_cafe_mine_list);
        ll_cafe_my_list = findViewById(R.id.ll_cafe_my_list);
        ll_find = findViewById(R.id.ll_find);
        ll_cafe_mine_list.setVisibility(View.GONE);
        ll_cafe_my_list.setVisibility(View.GONE);
        ll_find.setVisibility(View.VISIBLE);

        if ( (null == mCAdminList || mCAdminList.size() == 0) &&
                (null == mCJoinList || mCJoinList.size() == 0)) {
            ll_cafe_mine_list.setVisibility(View.GONE);
            ll_cafe_my_list.setVisibility(View.GONE);
            ll_find.setVisibility(View.VISIBLE);

            getFindCafeList("");
        } else {
            ll_cafe_mine_list.setVisibility(View.VISIBLE);
            ll_cafe_my_list.setVisibility(View.VISIBLE);
            ll_find.setVisibility(View.GONE);
        }



        btn_config = findViewById(R.id.btn_config);
        btn_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(MyCafeManageActivity.class, false);
            }
        });

        txt_privacy = findViewById(R.id.txt_privacy);
        txt_cafe_mine = findViewById(R.id.txt_cafe_mine);
        txt_cafe_my = findViewById(R.id.txt_cafe_my);
        txt_privacy.setTypeface(txt_privacy.getTypeface(), Typeface.BOLD);
        txt_cafe_mine.setTypeface(txt_cafe_mine.getTypeface(), Typeface.BOLD);
        txt_cafe_my.setTypeface(txt_cafe_my.getTypeface(), Typeface.BOLD);

        txt_cafekey = findViewById(R.id.txt_cafekey);

        btnJoin = findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(txt_cafekey.getText().toString().trim())) {
                    godetail();
                } else {
                    toast(R.string.warn_cafe_key_not_inputed);
                }
            }
        });

        btnMake = findViewById(R.id.btn_make_cafe);
        btnMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(CafeCreateDefaultActivity.class, false);
            }
        });

        btnFind = findViewById(R.id.btn_find_cafe);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(CafeFindActivity.class, false);
            }
        });

        btnGuide = findViewById(R.id.btn_cafe_guide);
        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(CafeGuideActivity.class, false);
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            finish();
        });

        input_name = findViewById(R.id.input_name);

        mRecyclerViewMine = findViewById(R.id.recycler_view_mine);
        mRecyclerViewMine.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));

        mRecyclerViewMy = findViewById(R.id.recycler_view_my);
        mRecyclerViewMy.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));

        mRecyclerViewFind = findViewById(R.id.recycler_view_find);
        mRecyclerViewFind.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void attachEvents() {
        findViewById(R.id.btn_find).setOnClickListener(v->{
            String keyword = input_name.getText().toString();
            getFindCafeList(keyword);
        });
    }

    @Override
    protected void setInitialData() {
        mMineAdapter = new CafeMineAdapter(this, mCAdminList);
        mRecyclerViewMine.setAdapter(mMineAdapter);
        mRecyclerViewMine.setNestedScrollingEnabled(false);

        mMyAdapter = new CafeMyAdapter(this, mCJoinList);
        mRecyclerViewMy.setAdapter(mMyAdapter);
        mRecyclerViewMy.setNestedScrollingEnabled(false);
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
                        if (null != mMineAdapter) {
                            mMineAdapter.clear();
                        }
                        if (null != mMyAdapter) {
                            mMyAdapter.clear();
                        }
                        if (null != mAdapter) {
                            mAdapter.clear();
                        }
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

    private void getFindCafeList(String keyword) {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("keyword", keyword);
        Call<CafeMainList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).openCafeList(query);
        call.enqueue(new Callback<CafeMainList>() {
            @Override
            public void onResponse(Call<CafeMainList> call, Response<CafeMainList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeMainList cafelist = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafelist.isSuccess()) {
                        mList = cafelist.getCafelist();

                        if (null != mAdapter) {
                            mAdapter.clear();
                        }

                        if (null == mList || mList.size() == 0) {
                            toast(R.string.warn_cafe_not_exists);
                        } else {
                            mAdapter = new CafeAdapter(getApplicationContext(), mList);
                            mRecyclerViewFind.setAdapter(mAdapter);
                            mRecyclerViewFind.setNestedScrollingEnabled(false);
                        }

                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<CafeMainList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void godetail() {
        Bundle bu = new Bundle();
        bu.putSerializable("cafekey", txt_cafekey.getText().toString().trim());

        // kmj mod
        callActivity(CafeDetailActivity.class, bu,false);

        }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == mResultCode) {
                getUserCafeMyList();
            }
        }
    }*/


    private void shareCafe(String cafename, String cafekey) {
        String subject = "계단왕 '" + cafename + "' 카페로 초대합니다.";

        String text = "계단왕 '" + cafename + "' 카페로 초대합니다.\n\n저희 계단왕 '" + cafename + "' 카페에 가입해 주세요.\n"
                + "계단왕 앱 메뉴의 카페 페이지에서 아래의 키값으로 바로 가입할 수 있습니다. (비공개 카페는 키값을 통해서만 카페 가입이 가능합니다.)\n\n"
                + "카페키값 : " + cafekey + "\n\n"
                + "계단왕은 그룹 멤버들과 함께 운동하며 이야기를 나누는 공간입니다. 아이폰, 안드로이드에서 무료로 다운받아 사용해 보세요.\n\n"
                + Env.Url.URL_SHARE.url();

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유할 앱을 선택하세요.");
        startActivity(chooser);
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
            View view = inflater.inflate(R.layout.row_cafe_detail_mine, parent, false);
            CafeMineAdapter.CafeMineHolder viewHolder = new CafeMineAdapter.CafeMineHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeMineAdapter.CafeMineHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();
            holder.tvCafename.setText(cafename);
            holder.tvCafename.setTypeface(holder.tvCafename.getTypeface(), Typeface.BOLD);
            holder.tvCafename.setOnClickListener(v->{
                /*Bundle bu = new Bundle();
                bu.putSerializable("cafeseq", item.getCafeseq());
                bu.putSerializable("cafekey", item.getCafekey());

                // kmj mod
                callActivity(CafeDetailActivity.class, bu,false);*/

                Intent intent = new Intent(context, CafeDetailActivity.class);
                intent.putExtra("cafeseq", item.getCafeseq());
                intent.putExtra("cafekey", item.getCafekey());
                startActivityForResult(intent, mResultCode);
            });

            holder.btnInvite.setOnClickListener(v->{
                shareCafe(item.getCafename(), item.getCafekey());
            });

            String opendate = item.getOpendate();
            String memcnt = item.getTotal();

            holder.tvOpendate.setText("개설일: " + opendate +"\n멤버: " + memcnt + "명");

            holder.tvInvite.setTypeface(holder.tvInvite.getTypeface(), Typeface.BOLD);

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
            TextView tvOpendate;
            TextView tvInvite;
            //TextView tvMemcnt;
            Button btnInvite;

            public CafeMineHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type) {
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                tvOpendate = itemView.findViewById(R.id.txt_open_date);
                //tvMemcnt = itemView.findViewById(R.id.txt_member);
                btnInvite = itemView.findViewById(R.id.btn_invite);
                tvInvite = itemView.findViewById(R.id.txt_invite);
            }

                @Override
                public void onClick(View view) {
                }
        }

        public void clear() {
            this.list.clear();
            notifyDataSetChanged();
        }
    }

    class CafeMyAdapter extends RecyclerView.Adapter<CafeMyAdapter.CafeMyHolder> {
        private Context context;
        private List<Cafe> list;
        private LayoutInflater inflater;
        //private ItemClickListener itemClickListener;

        CafeMyAdapter(Context context, List<Cafe> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CafeMyAdapter.CafeMyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_cafe_detail_join, parent, false);
            CafeMyAdapter.CafeMyHolder viewHolder = new CafeMyAdapter.CafeMyHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeMyAdapter.CafeMyHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();
            holder.tvCafename.setText(cafename);
            holder.tvCafename.setTypeface(holder.tvCafename.getTypeface(), Typeface.BOLD);
            holder.tvCafename.setOnClickListener(v->{
                Bundle bu = new Bundle();
                bu.putSerializable("cafeseq", item.getCafeseq());
                bu.putSerializable("cafekey", item.getCafekey());

                // kmj mod
                callActivity(CafeDetailActivity.class, bu,false);
            });

            String opendate = item.getOpendate();
            holder.tvOpendate.setText("가입일: " + opendate);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Cafe getItem(int id) {
            return list.get(id);
        }

        class CafeMyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvCafename;
            TextView tvOpendate;

            public CafeMyHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type) {
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                tvOpendate = itemView.findViewById(R.id.txt_open_date);
            }

            @Override
            public void onClick(View view) {
            }
        }

        public void clear() {
            this.list.clear();
            notifyDataSetChanged();
        }
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
            View view = inflater.inflate(R.layout.row_cafe_find, parent, false);
            CafeAdapter.CafeHolder viewHolder = new CafeAdapter.CafeHolder(view, viewType);
            return viewHolder;
        }



        @Override
        public void onBindViewHolder(CafeAdapter.CafeHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();
            holder.tvCafename.setText(cafename);
            holder.tvCafename.setTypeface(holder.tvCafename.getTypeface(), Typeface.BOLD);

            String cafedesc = item.getCafedesc();
            holder.etCafededsc.setText(cafedesc);

            String opendate = item.getOpendate();
            holder.tvOpendate.setText(opendate);

            String confirm = item.getConfirm();
            String confirmMessage = "";
            if ("Y".equals(confirm)) {
                confirmMessage = "비공개";
            } else {
                confirmMessage = "공개";
            }

            String memcnt = item.getTotal();
            holder.tvMember.setText("멤버: " + memcnt +"명  " + confirmMessage);

            String admin = item.getAdmin();
            holder.tvAdmin.setText("관리자: " + admin);


            holder.row.setOnClickListener(v->{
                Bundle bu = new Bundle();
                bu.putSerializable("cafeseq", item.getCafeseq());
                bu.putSerializable("cafekey", item.getCafekey());

                // kmj mod
                callActivity(CafeDetailActivity.class, bu,false);
            });
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

        public void clear() {
            this.list.clear();
            notifyDataSetChanged();
        }


        class CafeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ConstraintLayout row;
            TextView tvCafename, tvOpendate, tvMember, tvAdmin;
            KoswEditText etCafededsc;

            public CafeHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type){
                row = itemView.findViewById(R.id.box_of_row);
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                //tvCafename.setOnClickListener(this);
                etCafededsc = itemView.findViewById(R.id.txt_cafedesc);
                tvOpendate = itemView.findViewById(R.id.txt_open_date);
                tvMember = itemView.findViewById(R.id.txt_member);
                tvAdmin = itemView.findViewById(R.id.txt_admin);
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

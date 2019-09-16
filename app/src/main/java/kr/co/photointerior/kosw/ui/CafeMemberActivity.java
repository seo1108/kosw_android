package kr.co.photointerior.kosw.ui;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.CafeMember;
import kr.co.photointerior.kosw.rest.model.CafeMemberList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeMemberActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeMemberActivity.class);
    private RecyclerView mRecyclerView;
    private CafeMemberAdapter mAdapter;

    private KoswEditText input_name;
    private ImageView btn_back;

    private String mCafeseq;

    private List<CafeMember> mList = new ArrayList<>();

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_member);

        mCafeseq = getIntent().getStringExtra("cafeseq");

        findViews();
        attachEvents();
        setInitialData();

        searchUser("");
    }

    @Override
    protected void findViews() {
        input_name = findViewById(R.id.input_name);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            finish();
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));


    }

    @Override
    protected void attachEvents() {
        findViewById(R.id.btn_find).setOnClickListener(v->{
            String keyword = input_name.getText().toString();
            searchUser(keyword);
        });
    }

    @Override
    protected void setInitialData() {
    }

    private void searchUser(String keyword) {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("cafeseq", mCafeseq);
        query.put("keyword", keyword);
        Call<CafeMemberList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).searchUser(query);
        call.enqueue(new Callback<CafeMemberList>() {
            @Override
            public void onResponse(Call<CafeMemberList> call, Response<CafeMemberList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeMemberList list = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (list.isSuccess()) {
                        mList = list.getList();

                        if (null != mAdapter) {
                            mAdapter.clear();
                        }

                        if (null == mList || mList.size() == 0) {
                            toast(R.string.warn_cafe_not_exists);
                        } else {
                            mAdapter = new CafeMemberAdapter(getApplicationContext(), mList);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setNestedScrollingEnabled(false);
                        }

                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<CafeMemberList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void kickUser(String kickuserseq, int position) {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("kick_user_seq", kickuserseq);
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).kickUser(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_member_kick_success);
                        mAdapter.deleteItem(position);
                    }else{
                        toast(R.string.warn_cafe_member_fail_kick);
                    }
                }else{
                    toast(R.string.warn_cafe_member_fail_kick);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }


    class CafeMemberAdapter extends RecyclerView.Adapter<CafeMemberAdapter.CafeMemberHolder> {
        private Context context;
        private List<CafeMember> list;
        private LayoutInflater inflater;
        private ItemClickListener itemClickListener;

        CafeMemberAdapter(Context context, List<CafeMember> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CafeMemberAdapter.CafeMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_cafe_member, parent, false);
            CafeMemberAdapter.CafeMemberHolder viewHolder = new CafeMemberAdapter.CafeMemberHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeMemberAdapter.CafeMemberHolder holder, int position) {
            CafeMember item = getItem(position);

            String username = item.getUser_name();
            String nickname = item.getNickname();
            String catename = item.getCatename();

            holder.txt_name.setText(username);
            holder.txt_nickname.setText(nickname);
            holder.txt_catename.setText(catename);

            holder.btn_manage.setOnClickListener(v->{
                // 강퇴
                kickUser(item.getUser_seq(), position);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public CafeMember getItem(int id) {
            return list.get(id);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void clear() {
            this.list.clear();
            notifyDataSetChanged();
        }

        private void deleteItem(int position) {
            this.list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.list.size());
        }


        class CafeMemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            LinearLayout row;
            TextView txt_name, txt_nickname, txt_catename;
            KoswButton btn_manage;

            public CafeMemberHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type){
                row = itemView.findViewById(R.id.box_of_row);
                txt_name = itemView.findViewById(R.id.txt_name);
                txt_nickname = itemView.findViewById(R.id.txt_nickname);
                txt_catename = itemView.findViewById(R.id.txt_catename);
                btn_manage = itemView.findViewById(R.id.btn_manage);
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

}

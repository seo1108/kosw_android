package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
import kr.co.photointerior.kosw.rest.model.Notice;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeNoticeActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeFindActivity.class);
    private RecyclerView mRecyclerView;
    private CafeNoticeAdapter mAdapter;
    private ImageView btn_back;
    private KoswTextView title_regist;
    private String mCafeseq, mType;
    private List<Notice> mNoticeList;

    private int mNoticeCreateResultCode = 3000;
    //private int mNoticeEditResultCode = 3000;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_notice);

        mCafeseq = getIntent().getStringExtra("cafeseq");
        mType = getIntent().getStringExtra("type");

        findViews();
        attachEvents();
        setInitialData();

        getCafeNotice();
    }

    @Override
    protected void findViews() {
        title_regist = findViewById(R.id.title_regist);

        if (null != mType && "MODIFY".equals(mType)) {
            title_regist.setVisibility(View.VISIBLE);
        } else {
            title_regist.setVisibility(View.GONE);
        }

        btn_back = findViewById(R.id.btn_back);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));


    }

    @Override
    protected void attachEvents() {

        title_regist.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), CafeNoticePostActivity.class);
            intent.putExtra("cafeseq", mCafeseq);
            startActivityForResult(intent, mNoticeCreateResultCode);
        });


        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void setInitialData() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == mNoticeCreateResultCode) {
                getCafeNotice();
            }
        }
    }


    private void getCafeNotice() {
        showSpinner("");
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("cafeseq", mCafeseq);
        Call<CafeNoticeList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).notice(query);

        call.enqueue(new Callback<CafeNoticeList>() {
            @Override
            public void onResponse(Call<CafeNoticeList> call, Response<CafeNoticeList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeNoticeList noticelist = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (noticelist.isSuccess()) {
                        mNoticeList = noticelist.getNotice();

                        if (null != mAdapter) {
                            mAdapter.clear();
                        }

                        if (null != mNoticeList && mNoticeList.size() > 0) {
                            mAdapter = new CafeNoticeAdapter(getApplicationContext(), mNoticeList);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setNestedScrollingEnabled(false);
                        } else {
                            toast(R.string.warn_cafe_bbs_not_exists);
                        }
                    } else {
                        toast(R.string.warn_cafe_bbs_not_exists);
                    }
                } else {
                    toast(R.string.warn_cafe_bbs_not_exists);
                }
            }

            @Override
            public void onFailure(Call<CafeNoticeList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    class CafeNoticeAdapter extends RecyclerView.Adapter<CafeNoticeAdapter.CafeNoticeHolder> {
        private Context context;
        private List<Notice> list;
        private LayoutInflater inflater;
        private ItemClickListener itemClickListener;

        CafeNoticeAdapter(Context context, List<Notice> list){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public CafeNoticeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.row_notice_event, parent, false);
            CafeNoticeHolder viewHolder = new CafeNoticeAdapter.CafeNoticeHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeNoticeAdapter.CafeNoticeHolder holder, int position) {
            Notice item = getItem(position);
            String context = item.getContents();
            holder.txt_title.setText(context);
            holder.txt_title.setTypeface(holder.txt_title.getTypeface(), Typeface.BOLD);

            String regdate = item.getRegdate();
            holder.txt_date.setText(regdate);

            if (null != mType && "MODIFY".equals(mType)) {
                holder.btn_config.setVisibility(View.VISIBLE);
                holder.btn_config.setOnClickListener(v->{
                    Intent intent = new Intent(getApplicationContext(), CafeNoticeEditActivity.class);
                    intent.putExtra("cafeseq", mCafeseq);
                    intent.putExtra("notiseq", item.getNotiseq());
                    intent.putExtra("content", item.getContents());
                    startActivityForResult(intent, mNoticeCreateResultCode);
                });
            } else {
                holder.btn_config.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Notice getItem(int id) {
            return list.get(id);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void clear() {
            this.list.clear();
            notifyDataSetChanged();
        }


        class CafeNoticeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView txt_title, txt_date;
            ImageView btn_config;

            public CafeNoticeHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type){
                txt_title = itemView.findViewById(R.id.txt_title);
                txt_date = itemView.findViewById(R.id.txt_date);
                btn_config = itemView.findViewById(R.id.btn_config);
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

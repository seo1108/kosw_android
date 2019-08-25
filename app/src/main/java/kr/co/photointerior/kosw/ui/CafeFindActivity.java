package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeFindActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeFindActivity.class);
    private RecyclerView mRecyclerView;
    private CafeAdapter mAdapter;

    private ViewGroup mRootView;
    private List<Cafe> mList = new ArrayList<>();

    private KoswEditText input_name;
    private ImageView btn_back;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_find);

        findViews();
        attachEvents();
        setInitialData();
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
            if (null != keyword && !"".equals(keyword)) {
                getFindCafeList(keyword);
            } else {
                toast(R.string.warn_keyword_not_input);
            }
        });
    }

    @Override
    protected void setInitialData() {
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
                            mRecyclerView.setAdapter(mAdapter);
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
                confirmMessage = "비공개/자동 승인";
            } else {
                confirmMessage = "공개/자동 승인";
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
}

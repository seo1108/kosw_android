package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCafeManageActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(MyCafeManageActivity.class);
    private RecyclerView mRecyclerView;
    private CafeAdapter mAdapter;
    private ImageView btn_back;
    private CafeMainList mCMList ;
    private List<Cafe> mList = new ArrayList<>();
    private List<CafeSubCategory> mCateList = new ArrayList<>();

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cafe_manage);

        findViews();
        attachEvents();
        setInitialData();

        getUserCafeMainList();
    }

    @Override
    protected void findViews() {
        btn_back = findViewById(R.id.btn_back);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void attachEvents() {
        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void setInitialData() {
    }

    private void getUserCafeMainList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        Call<CafeMainList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).selectMyCafeList(query);
        call.enqueue(new Callback<CafeMainList>() {
            @Override
            public void onResponse(Call<CafeMainList> call, Response<CafeMainList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());

                if (response.isSuccessful()) {
                    CafeMainList cafelist = response.body();

                    if (cafelist.isSuccess()) {
                        mCMList = cafelist;
                        mList = mCMList.getCafelist();

                        if (null != mAdapter) {
                            mAdapter.clear();
                        }

                        if (null == mList || mList.size() == 0) {
                            toast(R.string.warn_my_cafe_not_exists);
                        } else {
                            mAdapter = new CafeAdapter(getApplicationContext(), mList);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setNestedScrollingEnabled(false);
                        }
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
            View view = inflater.inflate(R.layout.row_cafe_manage, parent, false);
            CafeAdapter.CafeHolder viewHolder = new CafeAdapter.CafeHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CafeAdapter.CafeHolder holder, int position) {
            Cafe item = getItem(position);
            String cafename = item.getCafename();
            holder.tvCafename.setText(cafename);
            holder.tvCafename.setTypeface(holder.tvCafename.getTypeface(), Typeface.BOLD);

            String confirm = item.getConfirm();
            String confirmMessage = "";
            if ("Y".equals(confirm)) {
                confirmMessage = "비공개";
            } else {
                confirmMessage = "공개";
            }

            String opendate = item.getOpendate();
            holder.tvOpendate.setText(opendate+"     " + confirmMessage);

            String memcnt = item.getTotal();
            holder.tvMember.setText("멤버: " + memcnt +"명");

            // 스피너 구성
            ArrayList<SubCategory> categoryList = new ArrayList<>();
            List<CafeSubCategory> catelist = item.getCategory();
            int myselection = 0;
            if (null != catelist && catelist.size() > 0) {
                holder.spinner.setVisibility(View.VISIBLE);

                for(int idx = 0; idx < catelist.size(); idx++) {
                    /*if (idx == 0) {
                        mSelectedCategorySpinnerItem = mCate.get(idx).getCateseq();
                    }*/
                    if (!"".equals(catelist.get(idx).getName())) {
                        categoryList.add(new SubCategory(catelist.get(idx).getCateseq(), catelist.get(idx).getName()));
                        if (item.getCateseq().equals(catelist.get(idx).getCateseq())) {
                            myselection = idx;
                        }
                    }
                }


                ArrayAdapter<SubCategory> adapter = new ArrayAdapter<SubCategory>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
                holder.spinner.setAdapter(adapter);
                holder.spinner.setSelection(myselection);
                adapter.notifyDataSetChanged();
            } else {
                holder.box_spinner.setVisibility(View.GONE);
            }

            if (null == categoryList || categoryList.size() == 0) {
                holder.box_spinner.setVisibility(View.GONE);
            }

            holder.input_additions.setFocusable(true);
            holder.input_additions.setClickable(true);
            holder.input_additions.setText(item.getMyadditions());

            AppUserBase user = DataHolder.instance().getAppUserBase() ;
            if ((String.valueOf(user.getUser_seq())).equals(item.getAdminseq())) {
                holder.btn_unregist.setVisibility(View.GONE);
            } else {
                holder.btn_unregist.setOnClickListener(v->{
                    // 회원탈퇴
                    kickUser(String.valueOf(user.getUser_seq()), position, item.getCafeseq());
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Cafe getItem(int id) {
            return list.get(id);
        }

        private void deleteItem(int position) {
            this.list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.list.size());
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
            TextView tvCafename, tvOpendate, tvMember;
            Spinner spinner;
            KoswEditText input_additions;
            KoswButton btn_unregist, btn_edit;
            RelativeLayout box_spinner;

            public CafeHolder(View view, int viewType) {
                super(view);
                pickupViews(viewType);
            }

            private void pickupViews(int type){
                row = itemView.findViewById(R.id.box_of_row);
                tvCafename = itemView.findViewById(R.id.txt_cafename);
                tvOpendate = itemView.findViewById(R.id.txt_open_date);
                tvMember = itemView.findViewById(R.id.txt_member);
                spinner = itemView.findViewById(R.id.spinner);
                input_additions = itemView.findViewById(R.id.input_additions);
                btn_unregist = itemView.findViewById(R.id.btn_unregist);
                btn_edit = itemView.findViewById(R.id.btn_edit);
                box_spinner = itemView.findViewById(R.id.box_spinner);
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


    public class SubCategory {
        private String id;
        private String name;

        public SubCategory(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubCategory that = (SubCategory) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    private void kickUser(String kickuserseq, int position, String cafeseq) {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("kick_user_seq", kickuserseq);
        query.put("cafeseq", cafeseq);

        SharedPreferences prefr = getSharedPreferences("lastSelectedCafe", MODE_PRIVATE);
        String selectedCafeSeq = prefr.getString("cafeseq", "");

        // 탈퇴한 카페가 현재 선택된 카페랑 같을 경우, 선택된 카페를 리셋한다.
        if (selectedCafeSeq.equals(cafeseq)) {
            SharedPreferences.Editor editor = prefr.edit();
            editor.putString("cafeseq", "");
            editor.commit();
        }

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
                        toast(R.string.cafe_member_kick_myself_success);
                        mAdapter.deleteItem(position);
                    }else{
                        toast(R.string.warn_cafe_member_fail_kick);
                    }
                }else{
                    toast(R.string.warn_cafe_member_fail_kick);
                }
                closeSpinner();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }
}

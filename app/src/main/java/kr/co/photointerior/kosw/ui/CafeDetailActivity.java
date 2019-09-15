package kr.co.photointerior.kosw.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.ResultType;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.listener.ItemClickListener;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeBbsList;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeNotice;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
import kr.co.photointerior.kosw.rest.model.CafeRankingList;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Notice;
import kr.co.photointerior.kosw.rest.model.RankInCafe;
import kr.co.photointerior.kosw.ui.row.RowCafeBbs;
import kr.co.photointerior.kosw.ui.row.RowCafeBbsComment;
import kr.co.photointerior.kosw.ui.row.RowDotSeparator;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeDetailActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeDetailActivity.class);
    private Cafe mCafe;
    private List<Notice> mNoticeList;

    private int[] mBtnResId = {
            R.id.btn_notice,
            R.id.btn_daily,
            R.id.btn_weekly,
            R.id.btn_monthly
    };
    private int mSelectedBtnId = mBtnResId[0];

    private KoswTextView tv_title, txt_cafename, txt_cafe_message, txt_notice_date, txt_notice;
    private KoswTextView txt_open_date, txt_member, txt_admin;
    private KoswEditText txt_cafedesc;
    private KoswEditText bbs_count;
    private KoswButton btn_join_cafe;
    private AppCompatSpinner spinner, spinner_category;
    private ImageView btn_back, img_post;
    private LinearLayout ll_notice, notice_linearlayout;
    private String mCafeseq = "";
    private String mCafekey = "";
    private List<CafeSubCategory> mCate;

    private RecyclerView mRecyclerView;
    private RankingAdapter mAdapter;
    private List<RankInCafe> mList = new ArrayList<>();
    private List<CafeNotice> mBbsList = new ArrayList<>();

    private String mBtnType;
    private String mSelectedSpinnerItem = "1";
    private String mSelectedCategorySpinnerItem = "-1";

    private int mBbsCreateResultCode = 1000;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_detail);

        mCafeseq = getIntent().getStringExtra("cafeseq");
        mCafekey = getIntent().getStringExtra("cafekey");

        getCafeDetail();



    }

    @Override
    protected void findViews() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mCafe.getCafename());

        txt_cafename = findViewById(R.id.txt_cafename);
        txt_cafename.setTypeface(txt_cafename.getTypeface(), Typeface.BOLD);
        txt_cafename.setText(mCafe.getCafename());

        txt_cafe_message = findViewById(R.id.txt_cafe_message);
        txt_cafe_message.setTypeface(txt_cafe_message.getTypeface(), Typeface.BOLD);

        txt_notice_date = findViewById(R.id.txt_notice_date);
        txt_notice_date.setTypeface(txt_notice_date.getTypeface(), Typeface.BOLD);

        txt_notice = findViewById(R.id.txt_notice);

        String confirm = mCafe.getConfirm();
        String confirmMessage = "";
        if ("Y".equals(confirm)) {
            confirmMessage = "비공개/자동 승인";
        } else {
            confirmMessage = "공개/자동 승인";
        }

        txt_open_date = findViewById(R.id.txt_open_date);
        txt_open_date.setText(mCafe.getOpendate() + "  " + confirmMessage);

        txt_member = findViewById(R.id.txt_member);
        txt_member.setText("맴버: " + mCafe.getTotal() + "명");

        txt_admin = findViewById(R.id.txt_admin);
        txt_admin.setText("관리자: " + mCafe.getAdmin());

        btn_join_cafe = findViewById(R.id.btn_join_cafe);


        ll_notice  = findViewById(R.id.ll_notice);
        notice_linearlayout = findViewById(R.id.notice_linearlayout);
        bbs_count = findViewById(R.id.bbs_count);

        spinner = findViewById(R.id.spinner);
        spinner_category = findViewById(R.id.spinner_category);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,
                        LinearLayoutManager.VERTICAL, false));

        img_post = findViewById(R.id.img_post);


        btn_back = findViewById(R.id.btn_back);

    }

    @Override
    protected void attachEvents() {
        View.OnClickListener listener = new ClickListener(this);
        for( int rs : mBtnResId ){
            getView(rs).setOnClickListener(listener);
        }

        toggleBtn(mBtnResId[0]);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!"N".equals(mBtnType)) {
                    spinner.setVisibility(View.VISIBLE);
                    Category cate = (Category) parent.getSelectedItem();
                    mSelectedSpinnerItem = cate.getId();
                    if ("1".equals(mSelectedSpinnerItem)) {
                        spinner_category.setVisibility(View.GONE);
                        getCafeRankingList();
                    } else if ("2".equals(mSelectedSpinnerItem)) {
                        spinner_category.setVisibility(View.GONE);
                        getCafeRankingByCategortyList();
                    } else if ("3".equals(mSelectedSpinnerItem)) {
                        if (null != mCate && mCate.size() > 0) {
                            spinner_category.setVisibility(View.VISIBLE);
                        }

                        getCafeRankingByCategoryIndividualList();
                    }
                } else {
                    spinner.setVisibility(View.GONE);
                    spinner_category.setVisibility(View.GONE);
                    if (null != mAdapter) mAdapter.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!"N".equals(mBtnType)) {
                    SubCategory cate = (SubCategory) parent.getSelectedItem();
                    mSelectedCategorySpinnerItem = cate.getId();
                    getCafeRankingByCategoryIndividualList();
                } else {
                    if (null != mAdapter) mAdapter.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_join_cafe.setOnClickListener(v->{
            if ("1".equals(mCafe.getIsjoin())) {
                toast(R.string.warn_cafe_already_join);
            } else {
                String catenames = "";
                String cateseqs = "";
                String additions = "";

                additions = mCafe.getAdditions();
                if (null != mCate && mCate.size() > 0) {
                    int idx = 0;
                    catenames += "분류(부서)를 선택하세요#@#";
                    cateseqs += "0#@#";
                    for (int i = 0; i < mCate.size(); i++) {
                        catenames += mCate.get(i).getName() + "#@#";
                        cateseqs += mCate.get(i).getCateseq() + "#@#";
                    }

                    catenames = catenames.substring(0, catenames.length()-3);
                    cateseqs = cateseqs.substring(0, cateseqs.length()-3);
                }

                if ("".equals(additions) && "".equals(cateseqs)) {
                    // 부서 정보와 추가 정보가 없으면 바로 카페 가입
                    toast("바로가입");
                } else {
                    Bundle bu = new Bundle();
                    bu.putSerializable("cafeseq", mCafe.getCafeseq());
                    bu.putSerializable("cafename", mCafe.getCafename());
                    bu.putSerializable("catenames", catenames);
                    bu.putSerializable("cateseqs", cateseqs);
                    bu.putSerializable("additions", additions);

                    // kmj mod
                    callActivity(CafeJoinActivity.class, bu,true);
                }
            }
        });

        img_post.setOnClickListener(v->{
            Intent intent = new Intent(this, BbsPostActivity.class);
            intent.putExtra("cafeseq", mCafeseq);
            intent.putExtra("postType", "BBS");

            startActivityForResult(intent, mBbsCreateResultCode);
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

        if (requestCode == mBbsCreateResultCode) {
            getCafeBBS();
        }
    }

    private void setSpinner() {
        ArrayList<Category> categoryList = new ArrayList<>();

        categoryList.add(new Category("1", getString(R.string.cafe_rank_individual)));
        categoryList.add(new Category("2", getString(R.string.cafe_rank_category)));
        categoryList.add(new Category("3", getString(R.string.cafe_rank_cate_indi)));

        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(adapter);

    }

    private void setSpinnerCategory() {
        ArrayList<SubCategory> categoryList = new ArrayList<>();

        if (null != mCate && mCate.size() > 0) {
            spinner_category.setVisibility(View.VISIBLE);

            for(int idx = 0; idx < mCate.size(); idx++) {
                if (idx == 0) {
                    mSelectedCategorySpinnerItem = mCate.get(idx).getCateseq();
                }
                categoryList.add(new SubCategory(mCate.get(idx).getCateseq(), mCate.get(idx).getName()));
            }

            ArrayAdapter<SubCategory> adapter = new ArrayAdapter<SubCategory>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryList);
            spinner_category.setAdapter(adapter);
        } else {
            spinner_category.setVisibility(View.GONE);
            return;
        }


    }

    private void getCafeDetail() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        if (null != mCafeseq && !"".equals(mCafeseq)) {
            query.put("cafeseq", mCafeseq);
        }
        if (null != mCafekey && !"".equals(mCafekey)) {
            query.put("cafekey", mCafekey);
        }

        Call<CafeDetail> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).detail(query);

        call.enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeDetail cafedetail = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafedetail.isSuccess()) {
                        mCafe = cafedetail.getCafe();
                        mCafeseq = mCafe.getCafeseq();
                        mCate = mCafe.getCategory();

                        findViews();
                        setSpinner();
                        setSpinnerCategory();
                        attachEvents();
                        setInitialData();

                        getCafeNotice();
                    } else {
                    }
                }

                if (!"0000".equals(response.body().getResponseCode())) {
                    toast(response.body().getResponseMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
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

                        if (null != mNoticeList && mNoticeList.size() > 0) {
                            txt_notice_date.setText("[공지] " + mNoticeList.get(0).getRegdate());
                            txt_notice.setText(mNoticeList.get(0).getContents() + "....[+더보기]");
                            txt_notice.setOnClickListener(v->{
                                // 공지사항 액티비티로 이동
                            });
                        } else {
                            txt_notice_date.setVisibility(View.GONE);
                            txt_notice.setVisibility(View.GONE);
                        }
                    } else {
                    }
                } else {
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

    @Override
    public void performClick(View view) {
        int id = view.getId();
        if (id == mSelectedBtnId) {
               return;
        }
        toggleBtn(id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }

    private void getCafeRankingList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);
        query.put("period", mBtnType);

        Call<CafeRankingList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).rankingIndividual(query);
        call.enqueue(new Callback<CafeRankingList>() {
            @Override
            public void onResponse(Call<CafeRankingList> call, Response<CafeRankingList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    if (null != mAdapter) {
                        mAdapter.clear();
                    }

                    if (null == response.body().getList() || response.body().getList().size() == 0) {
                        toast(R.string.warn_cafe_rank_not_exists);
                    } else {
                        mList = response.body().getList();
                        if (null != response.body().getMine()) {
                            RankInCafe rankMine = response.body().getMine();
                            rankMine.setIsmine("Y");
                            mList.add(0, response.body().getMine());
                        }

                        mAdapter = new RankingAdapter(getApplicationContext(), mList);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setNestedScrollingEnabled(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<CafeRankingList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    private void getCafeRankingByCategortyList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("cafeseq", mCafeseq);
        query.put("period", mBtnType);

        // 임시 하드코딩 (확인필요)
        query.put("cateseq", "999");

        Call<CafeRankingList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).rankingByCategory(query);
        call.enqueue(new Callback<CafeRankingList>() {
            @Override
            public void onResponse(Call<CafeRankingList> call, Response<CafeRankingList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    if (null != mAdapter) {
                        mAdapter.clear();
                    }

                    if (null == response.body().getList() || response.body().getList().size() == 0) {
                        toast(R.string.warn_cafe_rank_not_exists);
                    } else {
                        mList = response.body().getList();

                        mAdapter = new RankingAdapter(getApplicationContext(), mList);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setNestedScrollingEnabled(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<CafeRankingList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    private void getCafeRankingByCategoryIndividualList() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);
        query.put("cateseq", mSelectedCategorySpinnerItem);
        query.put("period", mBtnType);

        Call<CafeRankingList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).rankingIndividualByCategory(query);
        call.enqueue(new Callback<CafeRankingList>() {
            @Override
            public void onResponse(Call<CafeRankingList> call, Response<CafeRankingList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    if (null != mAdapter) {
                        mAdapter.clear();
                    }

                    if (null == response.body().getList() || response.body().getList().size() == 0) {
                        toast(R.string.warn_cafe_rank_not_exists);
                    } else {
                        mList = response.body().getList();
                        if (null != response.body().getMine()) {
                            RankInCafe rankMine = response.body().getMine();
                            rankMine.setIsmine("Y");
                            mList.add(0, response.body().getMine());
                        }

                        mAdapter = new RankingAdapter(getApplicationContext(), mList);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setNestedScrollingEnabled(false);
                    }

                }
            }

            @Override
            public void onFailure(Call<CafeRankingList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

    }

    private void getCafeBBS() {
        notice_linearlayout.removeAllViews();

        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);

        Call<CafeBbsList> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).bbs(query);
        call.enqueue(new Callback<CafeBbsList>() {
            @Override
            public void onResponse(Call<CafeBbsList> call, Response<CafeBbsList> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    if (null == response.body().getList() || response.body().getList().size() == 0) {
                        bbs_count.setText("게시글 : 0개");
                        bbs_count.setTypeface(bbs_count.getTypeface(), Typeface.BOLD);
                    } else {
                        mBbsList = response.body().getList();

                        bbs_count.setText("게시글 : " + mBbsList.size() + "개");
                        bbs_count.setTypeface(bbs_count.getTypeface(), Typeface.BOLD);

                        for (int idx = 0; idx < mBbsList.size(); idx++) {
                            RowCafeBbs n_layout = new RowCafeBbs(getApplicationContext());
                            LinearLayout con = (LinearLayout) findViewById(R.id.notice_linearlayout);
                            con.addView(n_layout);

                            int info_id = Integer.parseInt("200" + mBbsList.get(idx).getBbsseq());
                            int content_id = Integer.parseInt("300" + mBbsList.get(idx).getBbsseq());
                            int commcount_id = Integer.parseInt("400" + mBbsList.get(idx).getBbsseq());
                            int replycomm_id = Integer.parseInt("500" + mBbsList.get(idx).getBbsseq());

                            String notice_info = mBbsList.get(idx).getRegdate() + "   " + mBbsList.get(idx).getNickname() + "님";
                            KoswEditText et_info = (KoswEditText) con.findViewById(R.id.notice_info);
                            et_info.setId(info_id);
                            et_info.setText(notice_info);

                            String notice_content = mBbsList.get(idx).getContent();
                            KoswEditText et_context = (KoswEditText) con.findViewById(R.id.notice_context);
                            et_context.setId(content_id);
                            et_context.setTypeface(et_context.getTypeface(), Typeface.BOLD);
                            et_context.setText(notice_content);

                            KoswEditText et_comm_count = (KoswEditText) con.findViewById(R.id.notice_comment_count);
                            et_comm_count.setId(commcount_id);

                            // 댓글달기 이벤트
                            final String bbsseq = mBbsList.get(idx).getBbsseq();
                            KoswEditText et_notice_comment = (KoswEditText) con.findViewById(R.id.notice_comment);
                            et_notice_comment.setId(replycomm_id);
                            et_notice_comment.setOnClickListener(v->{
                                Intent intent = new Intent(getApplicationContext(), BbsPostActivity.class);
                                intent.putExtra("bbsseq", bbsseq);
                                intent.putExtra("postType", "COMMENT");

                                startActivityForResult(intent, mBbsCreateResultCode);
                            });

                            if (null != mBbsList.get(idx).getComments() && mBbsList.get(idx).getComments().size() > 0) {
                                et_comm_count.setText(mBbsList.get(idx).getComments().size() + "");

                                for (int b_idx = 0; b_idx < mBbsList.get(idx).getComments().size(); b_idx++) {
                                    RowCafeBbsComment b_layout = new RowCafeBbsComment(getApplicationContext());
                                    LinearLayout b_con = (LinearLayout) findViewById(R.id.notice_linearlayout);
                                    b_con.addView(b_layout);

                                    int comm_info_id = Integer.parseInt("600" + mBbsList.get(idx).getBbsseq() + mBbsList.get(idx).getComments().get(b_idx).getCommentseq());
                                    int comm_content_id = Integer.parseInt("700" + mBbsList.get(idx).getBbsseq() + mBbsList.get(idx).getComments().get(b_idx).getCommentseq());
                                    int comm_date_id = Integer.parseInt("800" + mBbsList.get(idx).getBbsseq() + mBbsList.get(idx).getComments().get(b_idx).getCommentseq());

                                    KoswEditText et_comm_info = (KoswEditText) b_con.findViewById(R.id.comment_context_info);
                                    et_comm_info.setId(comm_info_id);
                                    et_comm_info.setTypeface(et_comm_info.getTypeface(), Typeface.BOLD);
                                    et_comm_info.setText(mBbsList.get(idx).getComments().get(b_idx).getNickname());

                                    KoswEditText et_comm_context = (KoswEditText) b_con.findViewById(R.id.comment_context);
                                    et_comm_context.setId(comm_content_id);
                                    et_comm_context.setText(mBbsList.get(idx).getComments().get(b_idx).getContent());

                                    KoswEditText et_comm_date = (KoswEditText) b_con.findViewById(R.id.comment_date);
                                    et_comm_date.setId(comm_date_id);
                                    et_comm_date.setText(mBbsList.get(idx).getComments().get(b_idx).getRegdate());
                                }
                            } else {
                                et_comm_count.setText("0");
                            }

                            RowDotSeparator d_layout = new RowDotSeparator(getApplicationContext());
                            LinearLayout d_con = (LinearLayout) findViewById(R.id.notice_linearlayout);
                            d_con.addView(d_layout);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<CafeBbsList> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });

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
        }
        if(mSelectedBtnId == R.id.btn_daily){
            mBtnType = "D";
            if ("1".equals(mSelectedSpinnerItem)) {
                getCafeRankingList();
            } else if ("2".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategortyList();
            } else if ("3".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategoryIndividualList();
            }
        } else if(mSelectedBtnId == R.id.btn_weekly){
            mBtnType = "W";
            if ("1".equals(mSelectedSpinnerItem)) {
                getCafeRankingList();
            } else if ("2".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategortyList();
            } else if ("3".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategoryIndividualList();
            }
        } else if(mSelectedBtnId == R.id.btn_monthly){
            mBtnType = "M";
            if ("1".equals(mSelectedSpinnerItem)) {
                getCafeRankingList();
            } else if ("2".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategortyList();
            } else if ("3".equals(mSelectedSpinnerItem)) {
                getCafeRankingByCategoryIndividualList();
            }
        } else if(mSelectedBtnId == R.id.btn_notice) {
            mBtnType = "N";
            getCafeBBS();
            spinner_category.setVisibility(View.GONE);
            if (null != mAdapter) {
                mAdapter.clear();
            }
        }

        if (!"N".equals(mBtnType)) {
            ll_notice.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            ll_notice.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        }
    }

    public class Category {
        private String id;
        private String name;

        public Category(String id, String name) {
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
            Category category = (Category) o;
            return Objects.equals(id, category.id) &&
                    Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
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
            Category category = (Category) o;
            return Objects.equals(id, category.id) &&
                    Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    private class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingRowHolder>{
        private Context mContext;
        private List<RankInCafe> mItems;
        private LayoutInflater mInflater;
        private ItemClickListener mItemClickListener;

        RankingAdapter(Context context, List<RankInCafe> list){
            mContext = context;
            mItems = list;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public RankingRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.row_ranking_my, parent, false);
            return new RankingRowHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RankingRowHolder holder, int position) {
            RankInCafe item = mItems.get(position);

            String rank = item.getRank();
            String nickname = item.getNickname();
            String catename = item.getCatename();
            String act_amt = item.getAct_amt();

            holder.ranking.setText(rank);

            // 분류내 랭킹일 경우, 닉네임 부분에 부서 표시
            if (!"2".equals(mSelectedSpinnerItem)) {
                holder.nickname.setText(nickname);
            } else {
                holder.nickname.setText(catename);
            }

            // 분류내 랭킹일 경우, 분류 부분 미표기해야함
            if (!"2".equals(mSelectedSpinnerItem)) {
                holder.departName.setText(catename);
            } else {
                holder.departName.setText("");
            }
            holder.recordAmount.setText(act_amt);

            if (position == 0 && null != item.getIsmine() && "Y".equals(item.getIsmine())) {
                setTextBackground(holder);
            }
        }

        private void setTextBackground(RankingRowHolder holder){
            int color;
            holder.itemView.setBackgroundResource(R.color.color_35c2ef);
            color = getResources().getColor(R.color.colorWhite);
            holder.ranking.setTextColor(color);
            holder.nickname.setTextColor(color);
            holder.departName.setTextColor(color);
            holder.recordAmount.setTextColor(color);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public void clear() {
            this.mItems.clear();
            notifyDataSetChanged();
        }

       /* @Override
        public int getItemViewType(int position) {
            if(mItems.get(position).isMe() && mItems.get(position).getRankingToInt() > 4){
                return 0;
            }
            return mItems.get(position).getRankingToInt();
        }*/

        class RankingRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView ranking;
            TextView nickname;

            TextView departName;
            TextView recordAmount;

            RankingRowHolder(View view, int viewType){
                super(view);
                pickupViews(viewType);
            }

            /**
             * @param type 0:me, ranking 1,2,3 and else
             */
            private void pickupViews(int type){
                ranking = itemView.findViewById(R.id.txt_ranking);
                nickname = itemView.findViewById(R.id.txt_name);
                departName = itemView.findViewById(R.id.txt_depart);
                recordAmount = itemView.findViewById(R.id.txt_amount);
            }

            @Override
            public void onClick(View view) {
                //mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}

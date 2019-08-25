package kr.co.photointerior.kosw.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.Notice;
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

    private KoswTextView tv_title, txt_cafename, txt_cafe_message, txt_notice_date, txt_notice;
    private KoswTextView txt_open_date, txt_member, txt_admin;
    private KoswEditText txt_cafedesc;
    private KoswButton btn_join_cafe;
    private ImageView btn_back;
    private String mCafeseq = "";
    private String mCafekey = "";
    private List<CafeSubCategory> mCate;

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
        btn_join_cafe.setOnClickListener(v->{
            if ("1".equals(mCafe.getIsjoin())) {
                toast(R.string.warn_cafe_already_join);
            } else {
                String catenames = "";
                String cateseqs = "";
                String additions = "";

                additions = mCafe.getAdditions();
                mCate = mCafe.getCategory();
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

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void attachEvents() {
    }

    @Override
    protected void setInitialData() {

    }

    private void getCafeDetail() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);
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

                        findViews();
                        attachEvents();
                        setInitialData();

                        getCafeNotice();
                    } else {
                    }
                } else {
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

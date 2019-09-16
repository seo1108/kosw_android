package kr.co.photointerior.kosw.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.ui.row.RowAddCategory;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeCategoryActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeCategoryActivity.class);
    private ImageView add_cate_btn, btn_back;
    private LinearLayout category_linearlayout;
    private String mCafeseq;
    private Cafe mCafe;
    private List<CafeSubCategory> mCate;
    private int mCategoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_category);

        mCafeseq = getIntent().getStringExtra("cafeseq");

        getCafeDetail();

        findViews();
        attachEvents();

    }

    @Override
    protected void findViews() {
        category_linearlayout = findViewById(R.id.category_linearlayout);

        add_cate_btn = findViewById(R.id.add_cate_btn);


        btn_back = findViewById(R.id.btn_back);

    }

    @Override
    protected void attachEvents() {
        add_cate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RowAddCategory n_layout = new RowAddCategory(getApplicationContext());
                LinearLayout con = (LinearLayout)findViewById(R.id.category_linearlayout);
                //n_layout.setId(mCategoryIndex);
                con.addView(n_layout);

                KoswEditText et = (KoswEditText) con.findViewById(R.id.add_cate_title);
                et.setId(mCategoryIndex+2000);
                et.setFocusableInTouchMode(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(true);
                et.setFocusable(true);

                ImageView iv = (ImageView) con.findViewById(R.id.remove_cate_btn);
                iv.setId(mCategoryIndex+1000);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        n_layout.setVisibility(View.GONE);
                    }
                });

                mCategoryIndex++;
            }
        });

        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void setInitialData() {
        if (null != mCate && mCate.size() > 0) {
            for (int idx = 0; idx < mCate.size(); idx++) {
                RowAddCategory n_layout = new RowAddCategory(getApplicationContext());
                LinearLayout con = (LinearLayout) findViewById(R.id.category_linearlayout);
                //n_layout.setId(mCategoryIndex);
                con.addView(n_layout);

                KoswEditText et = (KoswEditText) con.findViewById(R.id.add_cate_title);
                et.setId(mCategoryIndex + 2000);
                et.setFocusableInTouchMode(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(true);
                et.setFocusable(true);
                et.setText(mCate.get(idx).getName());

                ImageView iv = (ImageView) con.findViewById(R.id.remove_cate_btn);
                iv.setId(mCategoryIndex + 1000);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        n_layout.setVisibility(View.GONE);
                    }
                });

                mCategoryIndex++;
            }
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
                        mCate = mCafe.getCategory();

                        setInitialData();
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

}

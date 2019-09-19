package kr.co.photointerior.kosw.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeJoinActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeJoinActivity.class);
    private Spinner mDeptSpinner;
    private KoswTextView txt_join_message, title_cate, txt_additions_message;
    private KoswEditText txt_additions;
    private ImageView popup_close;
    private LinearLayout box_input_additions;
    private KoswButton btn_join, btn_cancel;

    private String mCafeseq = "";
    private String mCafename = "";
    private String mCateseqs = "";
    private String mCatenames = "";
    private String mAdditions = "";
    private int mSelected = 0;
    private String mSeledtedCateseq = "0";

    private List<String> mNames;
    private List<String> mSeqs;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_join);

        mCafeseq = getIntent().getStringExtra("cafeseq");
        mCafename = getIntent().getStringExtra("cafename");
        mCateseqs = getIntent().getStringExtra("cateseqs");
        mCatenames = getIntent().getStringExtra("catenames");
        mAdditions = getIntent().getStringExtra("additions");

        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        txt_join_message = findViewById(R.id.txt_join_message);
        txt_join_message.setTypeface(txt_join_message.getTypeface(), Typeface.BOLD);
        txt_join_message.setText("'" + mCafename + "'" + getString(R.string.txt_cafe_join_alert));

        title_cate = findViewById(R.id.title_cate);
        title_cate.setTypeface(title_cate.getTypeface(), Typeface.BOLD);

        txt_additions_message = findViewById(R.id.txt_join_message);
        txt_additions_message.setTypeface(txt_additions_message.getTypeface(), Typeface.BOLD);

        mDeptSpinner = findViewById(R.id.spinner_depart);

        if ("".equals(mCateseqs)) {
            title_cate.setVisibility(View.GONE);
            mDeptSpinner.setVisibility(View.GONE);
        } else {
            setDepartData();
        }

        box_input_additions = findViewById(R.id.box_input_additions);
        if ("".equals(mAdditions)) {
            box_input_additions.setVisibility(View.GONE);
        }

        txt_additions = findViewById(R.id.txt_additions);

        popup_close = findViewById(R.id.popup_close);
        popup_close.setOnClickListener(v->{
            finish();
        });
        btn_join = findViewById(R.id.btn_join);
        btn_join.setOnClickListener(v->{
            if (!"".equals(mCafeseq)) {
                if (mDeptSpinner.getSelectedItemPosition() == 0) {
                    toast(R.string.txt_cafe_category_select);
                } else if ("".equals(txt_additions.getText().toString())) {
                    toast(R.string.txt_cafe_additions_select);
                } else {
                    mSeledtedCateseq = mSeqs.get(mDeptSpinner.getSelectedItemPosition());
                    join();
                }
            } else {
                if ("".equals(txt_additions.getText().toString())) {
                    toast(R.string.txt_cafe_additions_select);
                } else {
                    join();
                }
            }

        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void attachEvents() {
    }

    @Override
    protected void setInitialData() {
    }

    private void setDepartData() {
        mNames = Arrays.asList(mCatenames.split("#@#"));
        mSeqs = Arrays.asList(mCateseqs.split("#@#"));

        mDeptSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.row_depart_spinner, mNames));
        mDeptSpinner.setSelection(mSelected);
    }

    private void join() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cateseq", mSeledtedCateseq);
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).join(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.warn_cafe_success_join);
                        finish();
                    }else{
                        toast(R.string.warn_cafe_fail_join);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_join);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
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

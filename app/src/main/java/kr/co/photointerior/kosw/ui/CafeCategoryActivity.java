package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.ui.row.RowAddCategory;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeCategoryActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeCategoryActivity.class);
    private Dialog mDialog;
    private ImageView add_cate_btn, btn_back;
    private Button btn_edit;
    private LinearLayout category_linearlayout;
    private String mCafeseq;
    private Cafe mCafe;
    private List<CafeSubCategory> mCate;
    private int mCategoryIndex = 0;

    private Activity mActivity;

    private String mDeleted, mSeqs, mNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_cafe_category);

        mCafeseq = getIntent().getStringExtra("cafeseq");

        mSeqs = "";
        mNames = "";
        mDeleted = "";

        getCafeDetail();

        findViews();
        attachEvents();

    }

    @Override
    protected void findViews() {
        category_linearlayout = findViewById(R.id.category_linearlayout);
        add_cate_btn = findViewById(R.id.add_cate_btn);
        btn_edit = findViewById(R.id.btn_edit);
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
                et.setTag("0");
                et.setFocusableInTouchMode(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(true);
                et.setFocusable(true);

                ImageView iv = (ImageView) con.findViewById(R.id.remove_cate_btn);
                iv.setId(mCategoryIndex+1000);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteWarnPopup(n_layout, "0");
                    }
                });

                mCategoryIndex++;
            }
        });

        btn_edit.setOnClickListener(v->{
            showConfirmPopup();
        });


        btn_back.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    protected void setInitialData() {
        if (null != mCate && mCate.size() > 0) {
            for (int idx = 0; idx < mCate.size(); idx++) {
                final String delseq = mCate.get(idx).getCateseq();

                RowAddCategory n_layout = new RowAddCategory(getApplicationContext());
                LinearLayout con = (LinearLayout) findViewById(R.id.category_linearlayout);
                //n_layout.setId(mCategoryIndex);
                con.addView(n_layout);

                KoswEditText et = (KoswEditText) con.findViewById(R.id.add_cate_title);
                et.setId(mCategoryIndex + 2000);
                et.setTag(mCate.get(idx).getCateseq());
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
                        showDeleteWarnPopup(n_layout, delseq);
                    }
                });

                mCategoryIndex++;

                if ("".equals(mSeqs)) {
                    mSeqs += mCate.get(idx).getCateseq();
                } else {
                    mSeqs += "#@#" + mCate.get(idx).getCateseq();
                }
                if ("".equals(mNames)) {
                    mNames += mCate.get(idx).getName();
                } else {
                    mNames += "#@#" + mCate.get(idx).getName();
                }
            }

            Log.d("CATEGORYMOD_BF_NAME", mNames);
            Log.d("CATEGORYMOD_BF_SEQ", mSeqs);
        }
    }

    private void showConfirmPopup(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    modifyCategory();

                }
            };
            String msg = getString(R.string.txt_confirm_message);
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{getString(R.string.txt_cancel), null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    private void showDeleteWarnPopup(RowAddCategory n_layout, String delseq){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    //requestWithdrawal();
                    if (!"0".equals(delseq)) {
                        if ("".equals(mDeleted)) {
                            mDeleted += delseq;
                        } else {
                            mDeleted += "#@#" + delseq;
                        }
                    }
                    n_layout.setVisibility(View.GONE);
                }
            };
            String msg = getString(R.string.warn_category_delete);
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{getString(R.string.txt_cancel), null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
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

    private void modifyCategory() {
        mNames = "";
        mSeqs = "";


        for (int i = 0; i < mCategoryIndex; i++) {
            KoswEditText et = (KoswEditText)findViewById(i+2000);

            if (null != et) {
                if (et.isShown()) {
                    if (null != et.getText().toString() && !"".equals(et.getText().toString().trim())) {
                        if ("".equals(mSeqs)) {
                            mSeqs += et.getTag().toString();
                        } else {
                            mSeqs += "#@#" + et.getTag().toString();
                        }
                        if ("".equals(mNames)) {
                            mNames += et.getText().toString();
                        } else {
                            mNames += "#@#" + et.getText().toString();
                        }
                    }
                }
            } else {
            }
        }


        Log.d("CATEGORYMOD_NAME", mNames);
        Log.d("CATEGORYMOD_SEQ", mSeqs);
        Log.d("CATEGORYMOD_DEL", mDeleted);

        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);
        query.put("category", mNames);
        query.put("cateseqs", mSeqs);
        query.put("deleted", mDeleted);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).modifyCategory(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_category_edit_success);
                    }else{
                        toast(R.string.warn_cafe_fail_category_edit);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_category_edit);
                }

                closeSpinner();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
                closeSpinner();
            }
        });
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

package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeMyAllList;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogBbsUpdate;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.event.Keyboard;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BbsDetailActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(BbsDetailActivity.class);
    private KoswEditText et_content;
    private KoswButton btn_edit, btn_del;
    private ImageView btn_back;

    private String mBbsseq, mContent, mIsAdmin;
    private boolean mIsCreator;
    private int mBbsCreateResultCode = 1000;

    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_detail);

        mBbsseq = getIntent().getStringExtra("bbsseq");
        mContent = getIntent().getStringExtra("content");
        mIsAdmin = getIntent().getStringExtra("isAdmin");
        mIsCreator = getIntent().getBooleanExtra("isCreator", false);

        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        et_content = findViewById(R.id.et_content);
        et_content.setFocusable(false);
        et_content.setClickable(false);

        btn_edit = findViewById(R.id.btn_edit);
        btn_del = findViewById(R.id.btn_delete);

        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void attachEvents() {
        btn_del.setOnClickListener(v->{
            deleteBbs();
        });
        btn_edit.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), BbsEditActivity.class);
            intent.putExtra("bbsseq", mBbsseq);
            intent.putExtra("content", mContent);

            startActivityForResult(intent, mBbsCreateResultCode);
        });
        btn_back.setOnClickListener(v->{
            Intent intent = new Intent() ;
            setResult(RESULT_CANCELED, intent);
            finish();
        });
    }

    @Override
    protected void setInitialData() {
        et_content.setText(mContent);

        if (!mIsCreator && "Y".equals(mIsAdmin)) {
            btn_edit.setVisibility(View.GONE);
        } else if (!mIsCreator && !"Y".equals(mIsAdmin)) {
            btn_edit.setVisibility(View.GONE);
            btn_del.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == mBbsCreateResultCode) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private void deleteBbs() {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("bbsseq", mBbsseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).deleteBbs(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_bbs_delete_success);
                        Intent intent = new Intent() ;
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        toast(R.string.warn_cafe_fail_bbs_delete);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_bbs_delete);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }


    private void showBbsEditPopup(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    finish();
                }

                public void action(String editMsg) {
                    et_content.setText(editMsg);
                }
            };
            mDialog =
                    new DialogBbsUpdate(this,
                            acceptor,
                            mContent);
            mDialog.setCancelable(false);
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


            mDialog.show();
        }
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

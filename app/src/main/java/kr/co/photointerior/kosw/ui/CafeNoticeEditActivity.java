package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeNoticeEditActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeNoticeEditActivity.class);
    private KoswEditText et_content;
    private KoswButton btn_edit, btn_delete;
    private ImageView btn_back;

    private String mNotiseq, mContent;

    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_cafe_notice_edit);

        mNotiseq = getIntent().getStringExtra("notiseq");
        mContent = getIntent().getStringExtra("content");

        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        et_content = findViewById(R.id.et_content);

        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);

        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void attachEvents() {
        btn_edit.setOnClickListener(v->{
            // 수정
            modifyNotice();
        });

        btn_delete.setOnClickListener(v->{
            // 삭제

        });

        btn_back.setOnClickListener(v->{
            Intent intent = new Intent() ;
            setResult(RESULT_CANCELED, intent);
            mActivity.finish();
        });
    }

    @Override
    protected void setInitialData() {
        et_content.setText(mContent);
    }

    private void modifyNotice() {
        showSpinner("");

        String content = et_content.getText().toString();

        if (null == content || "".equals(content)) {
            toast(R.string.warn_cafe_bbs_content_empty);
            return;
        }

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("notiseq", mNotiseq);
        query.put("contents", content);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).modifyNotice(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_notice_edit_success);
                        Intent intent = new Intent() ;
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        toast(R.string.warn_cafe_fail_notice_edit);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_notice_edit);
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

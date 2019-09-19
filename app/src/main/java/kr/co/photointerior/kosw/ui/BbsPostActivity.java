package kr.co.photointerior.kosw.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
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
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BbsPostActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(BbsPostActivity.class);
    private KoswEditText et_content;
    private KoswTextView title_done;
    private ImageView btn_back;

    private String mCafeseq, mPostType, mBbsseq;
    private int mBbsCreateResultCode = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_post);

        mCafeseq = getIntent().getStringExtra("cafeseq");
        mBbsseq = getIntent().getStringExtra("bbsseq");
        mPostType = getIntent().getStringExtra("postType");

        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        et_content = findViewById(R.id.et_content);
        title_done = findViewById(R.id.title_done);
        btn_back = findViewById(R.id.btn_back);
    }

    @Override
    protected void attachEvents() {
        btn_back.setOnClickListener(v->{
            finish();
        });

        title_done.setOnClickListener(v->{
            if ("BBS".equals(mPostType)) {
                writeBbs();
            } else {
                writeComment();
            }
        });
    }

    @Override
    protected void setInitialData() {
        if ("BBS".equals(mPostType)) {
            et_content.setHint("새로운 소식을 남겨보세요.");
        } else {
            et_content.setHint("댓글을 남겨보세요.");
        }
    }

    private void writeBbs() {
        showSpinner("");

        String content = et_content.getText().toString();

        if (null == content || "".equals(content)) {
            toast(R.string.warn_cafe_bbs_content_empty);
            return;
        }

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);
        query.put("content", content);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).writeBbs(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_bbs_success);
                        Intent intent = new Intent() ;
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        toast(R.string.warn_cafe_fail_bbs_write);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_bbs_write);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void writeComment() {
        showSpinner("");

        String content = et_content.getText().toString();

        if (null == content || "".equals(content)) {
            toast(R.string.warn_cafe_bbs_content_empty);
            return;
        }

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("bbsseq", mBbsseq);
        query.put("content", content);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).writeComment(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_comment_success);
                        Intent intent = new Intent() ;
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        toast(R.string.warn_cafe_fail_comment_write);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_comment_write);
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

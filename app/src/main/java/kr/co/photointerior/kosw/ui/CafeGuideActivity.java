package kr.co.photointerior.kosw.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswTextView;

public class CafeGuideActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeGuideActivity.class);

    private KoswTextView tv_privacy_hide_title, tv_privacy_open_title;
    private ImageView btn_back;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_guide);

        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        tv_privacy_hide_title = findViewById(R.id.tv_privacy_hide_title);
        tv_privacy_hide_title.setTypeface(tv_privacy_hide_title.getTypeface(), Typeface.BOLD);
        tv_privacy_open_title = findViewById(R.id.tv_privacy_open_title);
        tv_privacy_open_title.setTypeface(tv_privacy_open_title.getTypeface(), Typeface.BOLD);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

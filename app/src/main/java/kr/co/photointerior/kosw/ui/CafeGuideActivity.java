package kr.co.photointerior.kosw.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswTextView;

public class CafeGuideActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeGuideActivity.class);

    //private KoswTextView tv_title_1, tv_title_2, tv_title_3, tv_title_4, tv_title_5;
    private ImageView btn_back;
    private WebView mWebView;

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
//        tv_title_1 = findViewById(R.id.tv_title_1);
//        tv_title_1.setTypeface(tv_title_1.getTypeface(), Typeface.BOLD);
//        tv_title_2 = findViewById(R.id.tv_title_2);
//        tv_title_2.setTypeface(tv_title_2.getTypeface(), Typeface.BOLD);
//        tv_title_3 = findViewById(R.id.tv_title_3);
//        tv_title_3.setTypeface(tv_title_3.getTypeface(), Typeface.BOLD);
//        tv_title_4 = findViewById(R.id.tv_title_4);
//        tv_title_4.setTypeface(tv_title_4.getTypeface(), Typeface.BOLD);
//        tv_title_5 = findViewById(R.id.tv_title_5);
//        tv_title_5.setTypeface(tv_title_5.getTypeface(), Typeface.BOLD);

        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.loadUrl(Env.UrlPath.CAFEGUIDE.path());

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

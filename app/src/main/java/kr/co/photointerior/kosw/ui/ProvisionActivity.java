package kr.co.photointerior.kosw.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.listener.ClickListener;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 이용약관 / 개인정보 보호정책 화면
 */
public class ProvisionActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(ProvisionActivity.class);
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provision);
        changeStatusBarColor(getCompanyColor());
        findViews();
        attachEvents();
        setInitialData();

    }

    /**
     * 앱에 저장된 회사코드를 기준으로 회사의 로고를 적용.
     */
    @Override
    protected void setInitialData() {
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.loadUrl(Env.Url.URL_API.url().concat(Env.UrlPath.PROVISION.path()));
    }

    @Override
    protected void findViews() {
        mWebView = getView(R.id.web_view);
    }

    @Override
    protected void attachEvents() {
        ClickListener clickListener = new ClickListener(this);
        getView(R.id.btn_back).setOnClickListener(clickListener);
    }

    @Override
    public void performClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in_full,R.anim.slide_out_right);
    }
}

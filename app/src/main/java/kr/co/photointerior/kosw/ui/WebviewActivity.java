package kr.co.photointerior.kosw.ui;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.utils.Log;
import kr.co.photointerior.kosw.utils.LogUtils;

public class WebviewActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(WebviewActivity.class);

    private WebView mWebView;

    private String mUrl;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mUrl = getIntent().getStringExtra("url");

        Log.d("TTTTTTTTTTTTTTTTT", mUrl);

        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {
        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void attachEvents() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}

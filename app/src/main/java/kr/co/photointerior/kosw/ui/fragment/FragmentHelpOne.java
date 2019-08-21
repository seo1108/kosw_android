package kr.co.photointerior.kosw.ui.fragment;

import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;

/**
 * 도움말 1페이지
 */
public class FragmentHelpOne extends BaseFragment {
    private WebView mWebView;

    public static BaseFragment newInstance(){
        return new FragmentHelpOne();
    }

    @Override
    protected void followingWorksAfterInflateView() {
        mWebView = getView(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.loadUrl(Env.getHtmlUrl(Env.UrlPath.STAIR_CODE));
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
    }


    @Override
    public int getViewResourceId() {
        return R.layout.fragment_help_one_new;
    }
}

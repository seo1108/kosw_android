package kr.co.photointerior.kosw.social.kakao;

import android.app.Activity;
import android.content.Intent;

import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.ui.LoginActivity;

public class BaseActivity extends kr.co.photointerior.kosw.ui.BaseActivity {
    protected static Activity self;

    @Override
    protected void onResume() {
        super.onResume();
        KoswApp.setCurrentActivity(this);
        self = BaseActivity.this;
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = KoswApp.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this)) {
            KoswApp.setCurrentActivity(null);
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        startActivity(intent);
        finish();
    }
}

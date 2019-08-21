package kr.co.photointerior.kosw.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.service.fcm.Push;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.event.KsEvent;

public class PushActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        Intent intent = getIntent();

        String msg = intent.getStringExtra("message");
        Log.v("test","test start ") ;
        if (msg != null) {
            Log.v("test",msg) ;

            /*
            LogUtils.err(TAG, "push receive : " + push.string());
            String topActivity = AUtil.getTopActivity(context);

            KsEvent.Type type = getEventtype(push.getStringData("push_type"));
            KoswApp app = (KoswApp) context.getApplicationContext() ;
            app.push = push ;
            */


        }
    }
}

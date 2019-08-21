package kr.co.photointerior.kosw.service.fcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.co.photointerior.kosw.conf.ConditionHolder;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 푸시메세지 수신 클래스.
 * Created by kugie.
 */
public class FcmMessagingService extends FirebaseMessagingService {
    private final String TAG = LogUtils.makeLogTag(FcmMessagingService.class);
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        ConditionHolder.instance()
                .getFcmMessageWorker().work(getApplicationContext(), remoteMessage);
    }

//    @Override
//    public void handleIntent(Intent intent) {
//        Bundle bundle = intent.getExtras();
//        ConditionHolder.instance()
//                .getFcmMessageWorker().work(getApplicationContext(), bundle);
//    }
}

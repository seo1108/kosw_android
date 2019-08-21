package kr.co.photointerior.kosw.service.fcm;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.messaging.RemoteMessage;

public interface FcmMessageWorker {
    void work(Context context, RemoteMessage remoteMessage);
    void work(Context context, Bundle bundle);
}

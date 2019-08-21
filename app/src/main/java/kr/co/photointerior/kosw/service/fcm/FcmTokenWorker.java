package kr.co.photointerior.kosw.service.fcm;

import android.content.Context;

public interface FcmTokenWorker {

    void work(Context context, String fcmToken);
}

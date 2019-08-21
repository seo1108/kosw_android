package kr.co.photointerior.kosw.service.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import kr.co.photointerior.kosw.conf.ConditionHolder;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * FCM push token을 수신하는 service class.
 * Created by kugie.
 */
public class FcmInstanceIdService extends FirebaseInstanceIdService {
    private final String TAG = LogUtils.makeLogTag(FcmInstanceIdService.class);

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        ConditionHolder.instance()
                .getFcmTokenWorker().work(getApplicationContext(),refreshedToken);
    }
}
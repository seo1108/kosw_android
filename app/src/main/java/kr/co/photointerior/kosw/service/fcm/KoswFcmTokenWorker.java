package kr.co.photointerior.kosw.service.fcm;

import android.content.Context;

import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * FCM Token 갱신 처리
 * Created by kugie on 2018. 4. 30.
 */
public class KoswFcmTokenWorker extends AbsFcmTokenWorker {
    private final String TAG = LogUtils.makeLogTag(KoswFcmTokenWorker.class);
    @Override
    public void work(Context context, String fcmToken) {
        if(!StringUtil.isEmptyOrWhiteSpace(fcmToken)){
            LogUtils.err(TAG, "fcm-token=" + fcmToken);
            //fcm-token=fuvFekp6Bok:APA91bFqQwmqIwOGmLbhejTWt0PCnPDCLGMWbJ2CNwV0IAYnwOtolmG3PKvXpf90WiXGsD-YJjtExrfFb9qSS6utXN2W6kVchJPF-KurimiProwsMN_Sd5M0PBtuzSTbOlJowTyIBg94
            if(!StringUtil.isEquals(fcmToken, KUtil.getStringPref(PrefKey.FCM_TOKEN, ""))){
                Pref.instance().saveBooleanValue(PrefKey.FCM_TOKEN_REFRESHED, true);
                Pref.instance().saveStringValue(PrefKey.FCM_TOKEN, fcmToken);
            }
        }
    }
}

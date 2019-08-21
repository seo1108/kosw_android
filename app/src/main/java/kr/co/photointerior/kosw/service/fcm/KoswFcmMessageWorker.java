package kr.co.photointerior.kosw.service.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.otto.Bus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.global.KoswApp;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.utils.AUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;
import kr.co.photointerior.kosw.utils.event.BusProvider;
import kr.co.photointerior.kosw.utils.event.KsEvent;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * FCM 메세지 수신 처리
 * Created by kugie on 2018. 4. 30.
 */

public class KoswFcmMessageWorker extends AbsFcmMessageWorker {
    private final String TAG = KoswFcmMessageWorker.class.getSimpleName();
    private boolean mTreated;

    @Override
    public void work(Context context, RemoteMessage remoteMessage) {
        LogUtils.err(TAG, "remote message=" + remoteMessage.getData());
        /*LogUtils.err(TAG, "remote message=" + remoteMessage.getData());
        LogUtils.err(TAG, "remote title=" + remoteMessage.getTtl());*/
        if( remoteMessage != null && !mTreated ){
            mTreated = true;
            new Handler(Looper.getMainLooper()).postDelayed(()->{
                /** 이중수신 방지를 위한 2.5초 딜레이 */
                mTreated = false;
            }, 2500);
            Map<String, String> data = remoteMessage.getData();
            if(data != null) {
                Push push = new Push(
                        data.get("title"),
                        data.get("message"));

                Iterator<String> keys = data.keySet().iterator();
                for (; keys.hasNext(); ) {
                    String k = keys.next();
                    push.addData(k, data.get(k));
                }
                LogUtils.err(TAG, "push receive : " + push.string());
                String topActivity = AUtil.getTopActivity(context);

                KsEvent.Type type = getEventtype(push.getStringData("push_type"));
                KoswApp app = (KoswApp) context.getApplicationContext() ;
                app.push = push ;


                /*
                if ("kr.co.photointerior.kosw.ui.MainActivity".equals(topActivity)) {
                    //if("kr.co.photointerior.kosw.ui.MainActivityyy".equals(topActivity)){
                    //sendPushReceiveBroadcast(context, push);
                    BusProvider.instance().post(
                            new KsEvent<Push>().setType(type).setValue(push)
                    );
                }
                /*else {
                    int badgeCount = Pref.instance().getIntValue(PrefKey.FCM_BADGE_COUNT, 0) + 1;
                    //ShortcutBadger.applyCount(context, badgeCount);
                    Pref.instance().saveIntValue(PrefKey.FCM_BADGE_COUNT, badgeCount);
                    if ("NOTICE_EVENT".equals(push.getStringData("push_type"))) {
                        *//*이벤트.공지사항만 notification 띄움*//*
                        sendNotification(context, push);
                    }
                }
                */
                sendNotification(context, push);
            }
        }
    }

    @Override
    public void work(Context context, Bundle bundle) {

        if( bundle != null && !mTreated ){
            mTreated = true;
            new Handler(Looper.getMainLooper()).postDelayed(()->{
                /** 이중수신 방지를 위한 2.5초 딜레이 */
                mTreated = false;
            }, 2500);

            Push push = new Push(
                    bundle.getString("gcm.notification.title"),
                    bundle.getString("gcm.notification.body"));


            Iterator<String> keys = bundle.keySet().iterator();
            for( ; keys.hasNext(); ){
                String k = keys.next();
                push.addData(k, bundle.get(k));
            }
            LogUtils.err(TAG, "push receive : " + push.string());
            String topActivity = AUtil.getTopActivity(context);

            KsEvent.Type type = getEventtype(push.getStringData("push_type"));

            KoswApp app = (KoswApp) context.getApplicationContext() ;
            app.push = push ;


            if("kr.co.photointerior.kosw.ui.MainActivity".equals(topActivity)){
            //if("kr.co.photointerior.kosw.ui.MainActivityyy".equals(topActivity)){
                //sendPushReceiveBroadcast(context, push);
                BusProvider.instance().post(
                        new KsEvent<Push>().setType(type).setValue(push)
                );
            }else {
                int badgeCount = Pref.instance().getIntValue(PrefKey.FCM_BADGE_COUNT, 0) + 1;
                //ShortcutBadger.applyCount(context, badgeCount);
                Pref.instance().saveIntValue(PrefKey.FCM_BADGE_COUNT, badgeCount);
                //if("NOTICE_EVENT".equals(push.getStringData("push_type"))) {
                    /*이벤트.공지사항만 notification 띄움*/
                    sendNotification(context, push);
                //}
            }
        }
    }

    private KsEvent.Type getEventtype(String value){
        if("NOTICE_EVENT".equals(value) ||
                "JERSEY_GREEN".equals(value) ||
                "JERSEY_GOLD".equals(value) ||
                "JERSEY_REDDOT".equals(value)
                ){
            return KsEvent.Type.MAIN_PUSH;
        }

        return null;
    }

    /**
     * 앱이 백그라운드에 있을 때 푸시수신 시 Toast message를 띄움
     * @param context
     * @param push
     */
    private void showToast(final Context context, final Push push){
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast ts = Toast.makeText(context, push.getMessage(), Toast.LENGTH_LONG);
            ts.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 300);
            ts.show();
        });
    }

    /**
     * 하나고 앱이 최상위에서 구동중일 경우는 팝업만 띄움.
     * @param context
     * @param push
     */
    private void sendPushReceiveBroadcast(Context context, Push push){
        Intent intent = new Intent(Env.Action.PUSH_RECEIVE_ACTION.action());
        addIntentValues(intent, push);
        context.sendBroadcast(intent);
    }

    private void addIntentValues(Intent intent, Push push){
        try {
            JSONObject json = new JSONObject();
            json.put("push_json", push.getDataMap());
            intent.putExtra("_PUSH_OBJECT_", push);
        }catch(JSONException e){
            LogUtils.err(TAG, e);
        }
    }

    /**
     * 하나고 앱이 백그라운드에 있을 경우 Notification 처리
     * @param context
     * @param push
     */
    private void sendNotification(Context context, Push push) {
        String clickAction = push.getClickAction();
        if(StringUtil.isEmptyOrWhiteSpace(clickAction)){
            clickAction = Env.Action.PUSH_NOTIFICATION_ACTION.action();
        }
        Intent intent = new Intent(clickAction);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        addIntentValues(intent, push);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 1001, intent,
                        PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                        .setContentTitle(push.getTitle())
                        .setContentText(push.getMessage())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Notification notification = notificationBuilder.build();

        notificationManager.notify(1001, notification);


    }
}

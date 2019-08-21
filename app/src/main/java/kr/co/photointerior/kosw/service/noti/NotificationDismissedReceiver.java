package kr.co.photointerior.kosw.service.noti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getExtras().getInt("notificationId");
        /* Your code to handle the event here */
        Log.d("NOTIFICATION", "DISMiSS " + notificationId);
        context.stopService(new Intent(context, NotiService.class));
    }
}
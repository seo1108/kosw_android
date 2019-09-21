package kr.co.photointerior.kosw.service.noti;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class RestartService  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("000 RestartService" , "RestartService called : " + intent.getAction());

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if(intent.getAction().equals("ACTION.RESTART.NotiService")){

            Log.i("000 RestartService" ,"ACTION.RESTART.NotiService " );

            /*Intent i = new Intent(context, NotiService.class);

            context.startService(i);*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, NotiService.class));
            } else {
                context.startService(new Intent(context, NotiService.class));
            }
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            Log.i("RestartService" , "ACTION_BOOT_COMPLETED" );
            //Intent i = new Intent(context, NotiService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, NotiService.class));
            } else {
                context.startService(new Intent(context, NotiService.class));
            }

            //context.startService(i);

        }


    }
}
package kr.co.photointerior.kosw.service.noti;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.service.stepcounter.StepCounterService;
import kr.co.photointerior.kosw.ui.MainActivity;

public class NotiService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    private NotificationManager notificationManager;
    WifiManager.WifiLock wifiLock;
    PowerManager.WakeLock wakeLock;
    Context mContext;

    private static final int MILLISINFUTURE = 1000*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;

    private CountDownTimer countDownTimer;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        thread = new ServiceThread(this);
        thread.start();
        buildNotification();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        super.onCreate();

        initData();
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        super.onDestroy();

        Log.i("NotiService" , "onDestroy" );
        countDownTimer.cancel();

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        registerRestartAlarm();
    }

    private void initData(){
        countDownTimer();
        countDownTimer.start();
    }

    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

                Log.i("NotiService","onTick");
            }
            public void onFinish() {

                Log.i("NotiService","onFinish");
            }
        };
    }

    private void registerRestartAlarm(){

        Log.i("000 NotiService" , "registerRestartAlarm" );
        Intent intent = new Intent(NotiService.this,RestartService.class);
        intent.setAction("ACTION.RESTART.NotiService");
        PendingIntent sender = PendingIntent.getBroadcast(NotiService.this,0,intent,0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1*1000;

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,1*1000,sender);

    }

    private void unregisterRestartAlarm(){

        Log.i("000 NotiService" , "unregisterRestartAlarm" );

        Intent intent = new Intent(NotiService.this,RestartService.class);
        intent.setAction("ACTION.RESTART.NotiService");
        PendingIntent sender = PendingIntent.getBroadcast(NotiService.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);



    }

    /*private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), NotiService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder( icon, title, pendingIntent ).build();
    }
*/



    private void buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), AppConst.NOTIFICATION_CHANNEL_ID);
        NotificationChannelSupport notificationChannelSupport = new NotificationChannelSupport();
        notificationChannelSupport.createNotificationChannel(getApplicationContext(), AppConst.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "";
        if(!isMyServiceRunning(StepCounterService.class)) {
            title = "[수동측정]";
        } else {
            title = "[자동측정]";
        }

        builder.setContentTitle(title)
                .setContentText("[랭킹:" + AppConst.NOTI_RANKS + "] " + AppConst.NOTI_FLOORS + "F / " + AppConst.NOTI_CALS + "kcal / " + AppConst.NOTI_SECS + "sec")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_floor))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                //.setColorized(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(createOnDismissedIntent(getApplicationContext(), AppConst.NOTIFICATION_ID));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        startForeground(AppConst.NOTIFICATION_ID, builder.build());
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
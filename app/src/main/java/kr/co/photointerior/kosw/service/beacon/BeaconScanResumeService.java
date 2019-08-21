package kr.co.photointerior.kosw.service.beacon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import kr.co.photointerior.kosw.global.Env;

/**
 * @deprecated 2018.07.05
 */
public class BeaconScanResumeService extends Service {
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Env.Action.KOSW_RANGE_RESTART_ACTION.isMatch(intent.getAction())){
                restartService();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(Env.Action.KOSW_RANGE_RESTART_ACTION.action()));
    }

    private void restartService(){
        //stopService(new Intent(this, BeaconRagingInRegionService.class));
        new Handler().postDelayed(()->{
            startService(new Intent(this, BeaconRagingInRegionService.class));
            stopSelf();
        }, 5000);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package kr.co.photointerior.kosw.service.stepcounter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StepCounterService extends Service {
    StepThread thread;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new StepThread(this);
        thread.start();
        return Service.START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
    }
}

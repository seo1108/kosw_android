package kr.co.photointerior.kosw.service.stepcounter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StepCounterService extends Service {
    StepThread thread;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "StepCounterService 시작", Toast.LENGTH_SHORT);
        Log.d("999999999999777771", "StepCounterService 시작");
        thread = new StepThread(this);
        thread.start();
        return Service.START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        Toast.makeText(this, "StepCounterService 종료", Toast.LENGTH_SHORT);
        Log.d("999999999999777771", "StepCounterService 종료");
        thread.stopForever();
    }
}

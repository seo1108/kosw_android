package kr.co.photointerior.kosw.service.beacon;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 휴대폰이 걷기 기준 움직이는 상태인가를 검사하기 위한 서비스 클래스.
 */
public class StepSensorService extends BaseService implements SensorEventListener {
    private String TAG = "StepSensorService";
    private int mStepCount;
    private long mPrevSensorInputTime;
    private float mSensorSpeed;
    private float mSensorLastX;
    private float mSensorLastY;
    private float mSensorLastZ;

    private float mX, mY, mZ;
    private static final int SHAKE_THRESHOLD = 800;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.err(TAG, TAG + "#onCreate()");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogUtils.err(TAG, TAG + "#onStartCommand()");
        if (mAccelerometerSensor != null) {
            mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        mLastTagTime = System.currentTimeMillis();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.err(TAG, TAG + "#onDestroy()");
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mStepCount = 0;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** 스텝 증가 최종 시간 */
    private long mLastTagTime = 0L;
    /** 서비스 종료 제한시간
     * 2018.07.19. 10분으로 변경. 송대표 요청
     * */
    private final long LIMIT_TIME = (1000 * 60) * 10L;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //LogUtils.err(TAG, "sensor-event=" + Arrays.toString(sensorEvent.values));
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - mPrevSensorInputTime);

            if (gabOfTime > 100) { //  gap of time of step count
                //LogUtils.err(TAG, "Step count prepared.");
                mPrevSensorInputTime = currentTime;

                mX = sensorEvent.values[0];
                mY = sensorEvent.values[1];
                mZ = sensorEvent.values[2];

                mSensorSpeed = Math.abs(mX + mY + mZ - mSensorLastX - mSensorLastY - mSensorLastZ) / gabOfTime * 10000;

                if (mSensorSpeed > SHAKE_THRESHOLD) {
                    //LogUtils.err(TAG, "step count up");
                    mStepCount++;
                    mLastTagTime = System.currentTimeMillis();
                    /*String msg = count / 2 + "";
                    Intent it = new Intent("kr.co.photointerior.kosw.STEP_UPDATE_ACTION");
                    it.putExtra("steps", msg);
                    sendBroadcast(it);*/
                }

                mSensorLastX = sensorEvent.values[0];
                mSensorLastY = sensorEvent.values[1];
                mSensorLastZ = sensorEvent.values[2];

                if ((System.currentTimeMillis() - mLastTagTime) >= LIMIT_TIME) {
                    stopServices();
                }

            }
        }
    }

    /** 제한시간 동안 휴대폰 움직임이 없을 때 모든 서비스 종료 */
    @Override
    public void stopServices(){
        if(isTopActivityIsMainActivity()){//Main Activity destroy를 통한 서비스 종료
            sendBroadcast(new Intent(Env.Action.EXIT_ACTION.action()));
        }else {
            stopService(new Intent(this, BeaconRagingInRegionService.class));
            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

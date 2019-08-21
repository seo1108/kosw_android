package kr.rapids.altimeasure;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * 고도 관리 클래스.
 */
public class StepManager {
    private String TAG = "StepSensorService";

    private MainActivity context;

    private int mStepCount;
    private long mPrevSensorInputTime;
    private float mSensorSpeed;
    private float mSensorLastX;
    private float mSensorLastY;
    private float mSensorLastZ;

    private float mX, mY, mZ;
    private static final int SHAKE_THRESHOLD = 200;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private PresureListener sensorListener = new PresureListener();
    private boolean mHasSensor;
    private float mStep ;

    private long mLastTagTime = 0L;


    public StepManager(MainActivity context){
        mSensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager != null) {
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mHasSensor = true;
        }else{
            mHasSensor = false;
        }
        this.context = context;
    }



    private class PresureListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Probably not needed
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {


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
                    context.setCurrentStep(1) ;
                }

                mSensorLastX = sensorEvent.values[0];
                mSensorLastY = sensorEvent.values[1];
                mSensorLastZ = sensorEvent.values[2];


            }
        }
    }


    /** 고도측정 시작 */
    public void startMeasure(){
        if(mHasSensor) {
            mSensorManager.unregisterListener(sensorListener);
            mSensorManager.registerListener(sensorListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /** 고도측정 종료 */
    public void stopMeasure(){
        if(mHasSensor) {
            mSensorManager.unregisterListener(sensorListener);
        }
    }

    public void restartMeasure(){
    }


}

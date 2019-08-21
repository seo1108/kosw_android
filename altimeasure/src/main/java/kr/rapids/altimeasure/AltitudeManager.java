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
public class AltitudeManager {
    private String TAG = "AltitudeManager";

    /** 비콘 서비스 */
    private MainActivity context;
    /** 고도 계산 클래스 */
    private PresureHeight presureHeight = new PresureHeight();
    /** 센서 메니저 */
    private SensorManager sensorManager;
    /** 센서 */
    private Sensor sensor;
    private PresureListener sensorListener = new PresureListener();
    private boolean mHasSensor;
    public AltitudeManager(MainActivity context){
        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mHasSensor = true;
        }else{
            mHasSensor = false;
        }
        this.context = context;
    }

    /**
     * @return 고도계산 클래스.
     */
    public PresureHeight getPresureHeight() {
        return presureHeight;
    }

    /* Max possible samples to get average of start pressure */
//    private static final int START_AVG_MAX = 10;
    private static final int START_AVG_MAX = 20;
//    private static final int START_AVG_MAX = 50;

    /* Max possible samples to get average of current pressure */
    private static final int CURRENT_AVG_MAX = 20;

    private static double startValues[] = new double[START_AVG_MAX];
    private static double presureAverage[] = new double[CURRENT_AVG_MAX];

    private static final int startAvgMaxSetDefault = 16; // default value
    private static final int currentAvgMaxSetDefault = 8;  // default value

    private static int startAverageMaxSet = startAvgMaxSetDefault;
    private static int currentAvgMaxSet = currentAvgMaxSetDefault;

    private static int avgCnt = 0;
    private static int counter = 0;
    private static int isWorking = 0;
    private boolean measureFinished;

    private class PresureListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Probably not needed
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (counter < START_AVG_MAX) {
                Log.d(TAG, "altitude counter : " + counter);
                startValues[counter] = event.values[0];
            }
            if (counter < startAverageMaxSet) {
                Log.d(TAG, "altitude 시작 기압 측정 중 ...");
            } else if (counter == startAverageMaxSet) {
                Log.d(TAG, "altitude counter : " + counter);
                double sum = 0;
                for (int i = 0; i < startAverageMaxSet; ++i) {
                    sum += startValues[i];
                }
                presureHeight.setPresureStart(sum / startAverageMaxSet);
                Log.d(TAG, "altitude start presure value: " + sum / startAverageMaxSet);
            } else if (counter > startAverageMaxSet) {
                Log.d(TAG, "altitude presure value: " + event.values[0]);
                addAvgValue(event.values[0]);
                presureHeight.setPresureFinal(countAgv());
                // presureHeight.setPresureFinal(event.values[0]);
                //beaconService.setCurrentAltitude(presureHeight.getAltitude(false));
                //altitude = presureHeight.getAltitude(false);
                altitude = presureHeight.getAltitude(false);
                if(measureFinished) {
                    if (altitude > 500 || altitude < -500 )  {

                    } else {
                        context.setCurrentAltitude(altitude);

                    }
                }else{
                    //context.setCurrentAltitude(-99999);
                }
            }
            if (counter <= START_AVG_MAX) {
                ++counter;
            }else{
                measureFinished = true;
            }
        }
    }
    public boolean isMeasureFinished(){
        return measureFinished;
    }
    private double altitude;
    public double getAltitude(){
        if(counter >= START_AVG_MAX) {
            return altitude;
        }
        return -99999;
    }

    /** 고도측정 시작 */
    public void startMeasure(){

        if(mHasSensor) {
            measureFinished = false;
            resetCounter();
            sensorManager.unregisterListener(sensorListener);
            presureHeight.setTemperatureCelcius(20.0);
             sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(sensorListener, sensor, 10000);
//            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /** 고도측정 종료 */
    public void stopMeasure(){
        if(mHasSensor) {
            sensorManager.unregisterListener(sensorListener);
        }
    }

    public void restartMeasure(){
        if(mHasSensor) {
            measureFinished = false;
            resetCounter();
        }
    }

    private void resetCounter() {
        counter = 0;
    }
    private void addAvgValue(double value) {
        presureAverage[avgCnt] = value;
        avgCnt = (avgCnt + 1) % currentAvgMaxSet;
    }

    private double countAgv() {
        double sum = 0;
        for (int i = 0; i < currentAvgMaxSet; ++i) {
            sum += presureAverage[i];
        }
        return sum / currentAvgMaxSet;
    }
}

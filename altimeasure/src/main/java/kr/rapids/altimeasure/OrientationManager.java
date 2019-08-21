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
public class OrientationManager {
    private String TAG = "AltitudeManager";

    /** 비콘 서비스 */
    private MainActivity context;
    /** 고도 계산 클래스 */
    private PresureHeight presureHeight = new PresureHeight();
    /** 센서 메니저 */
    private SensorManager sensorManager;
    /** 센서 */
    private Sensor mAccelerometer;
    private Sensor mMagneticField;
    private PresureListener sensorListener = new PresureListener();
    private boolean mHasSensor;
    float[] mGravity;
    float[] mMagnetic;
    private float mAzimut ;

    public OrientationManager(MainActivity context){

        float[] rotation = new float[9];
        float[] result_data = new float[3];
        float[] mag_data = new float[3];
        float[] acc_data = new float[3];

        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null) {
            mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
    private static final int START_AVG_MAX = 32;
//    private static final int START_AVG_MAX = 50;

    /* Max possible samples to get average of current pressure */
    private static final int CURRENT_AVG_MAX = 32;

    private static double startValues[] = new double[START_AVG_MAX];
    private static double presureAverage[] = new double[CURRENT_AVG_MAX];

    private static final int startAvgMaxSetDefault = 32; // default value
    private static final int currentAvgMaxSetDefault = 32;  // default value

    private static int startAverageMaxSet = startAvgMaxSetDefault;
    private static int currentAvgMaxSet = currentAvgMaxSetDefault;


    private static int avgCnt = 0;
    private static int counter = 0;
    private static int mcounter = 0;
    private static int isWorking = 0;
    private boolean measureFinished;

    float[] rotation ;
    float[] result_data ;
    float[] mag_data ;
    float[] acc_data ;

    private class PresureListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Probably not needed
        }

        private float[] rotationMatrix = new float[9];
        private float[] orientation = new float[3];
        @Override
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    mGravity = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mMagnetic = event.values.clone();
                    break;
                default:
                    return;
            }

            if (mGravity != null && mMagnetic != null) {

                SensorManager.getRotationMatrix(this.rotationMatrix,null, mGravity, mMagnetic);
                SensorManager.getOrientation(this.rotationMatrix, this.orientation);


                float azimuthInRadiands = this.orientation[0];

                // this is now the heading of the phone. If you want
                // to rotate a view to north don´t forget that you have
                // to rotate by the negative value.
                float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadiands);
                //updateDirection();

                altitude = (int) azimuthInDegrees  ;
                context.setCurrentOrientation(altitude);

                /*
                if (counter < START_AVG_MAX) {
                    Log.d(TAG, "altitude counter : " + counter);
                    startValues[counter] = altitude;
                }
                if (counter < startAverageMaxSet) {
                    Log.d(TAG, "altitude 시작 기압 측정 중 ...");
                } else if (counter == startAverageMaxSet) {
                    Log.d(TAG, "altitude counter : " + counter);
                    double sum = 0;
                    for (int i = 0; i < startAverageMaxSet; ++i) {
                        sum += startValues[i];
                    }
                    //presureHeight.setPresureStart(sum / startAverageMaxSet);
                    Log.d(TAG, "altitude start presure value: " + sum / startAverageMaxSet);
                } else if (counter > startAverageMaxSet) {
                    mcounter++ ;

                    addAvgValue(altitude);

                    if (mcounter >= 32) {
                        mcounter = 0 ;
                        altitude = countAgv() ;
                        context.setCurrentOrientation(altitude);
                    }
                    //presureHeight.setPresureFinal(countAgv());
                    // presureHeight.setPresureFinal(event.values[0]);
                    //beaconService.setCurrentAltitude(presureHeight.getAltitude(false));
                    //altitude = presureHeight.getAltitude(false);
                    //altitude = presureHeight.getAltitude(false);
                }
                if (counter <= START_AVG_MAX) {
                    ++counter;
                }else{
                    measureFinished = true;
                }

*/
            }
        }
    }
    public boolean isMeasureFinished(){
        return measureFinished;
    }
    private double altitude;


    private void updateDirection() {
        float[] temp = new float[9];
        float[] R = new float[9];
        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);
        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);
        //Return the orientation values
        float[] values = new float[3];
        SensorManager.getOrientation(R, values);
        //Convert to degrees
        for (int i=0; i < values.length; i++) {
            Double degrees = (values[i] * 180) / Math.PI;
            values[i] = degrees.floatValue();
        }

        altitude = (int) (values[0] )   ;

        if (counter < START_AVG_MAX) {
            Log.d(TAG, "altitude counter : " + counter);
            startValues[counter] = altitude;
        }
        if (counter < startAverageMaxSet) {
            Log.d(TAG, "altitude 시작 기압 측정 중 ...");
        } else if (counter == startAverageMaxSet) {
            Log.d(TAG, "altitude counter : " + counter);
            double sum = 0;
            for (int i = 0; i < startAverageMaxSet; ++i) {
                sum += startValues[i];
            }
            //presureHeight.setPresureStart(sum / startAverageMaxSet);
            Log.d(TAG, "altitude start presure value: " + sum / startAverageMaxSet);
        } else if (counter > startAverageMaxSet) {
            mcounter++ ;

            addAvgValue(altitude);

            if (mcounter >= 32) {
                mcounter = 0 ;
                altitude = countAgv() ;
                context.setCurrentOrientation(altitude);
            }
            //presureHeight.setPresureFinal(countAgv());
            // presureHeight.setPresureFinal(event.values[0]);
            //beaconService.setCurrentAltitude(presureHeight.getAltitude(false));
            //altitude = presureHeight.getAltitude(false);
            //altitude = presureHeight.getAltitude(false);
        }
        if (counter <= START_AVG_MAX) {
            ++counter;
        }else{
            measureFinished = true;
        }

    }

    private String getDirectionFromDegrees(float degrees) {
        if(degrees >= -22.5 && degrees < 22.5) { return "N"; }
        if(degrees >= 22.5 && degrees < 67.5) { return "NE"; }
        if(degrees >= 67.5 && degrees < 112.5) { return "E"; }
        if(degrees >= 112.5 && degrees < 157.5) { return "SE"; }
        if(degrees >= 157.5 || degrees < -157.5) { return "S"; }
        if(degrees >= -157.5 && degrees < -112.5) { return "SW"; }
        if(degrees >= -112.5 && degrees < -67.5) { return "W"; }
        if(degrees >= -67.5 && degrees < -22.5) { return "NW"; }

        return null;
    }


    /** 고도측정 시작 */
    public void startMeasure(){
        if(mHasSensor) {
            measureFinished = false;
            resetCounter();

            sensorManager.unregisterListener(sensorListener);
            presureHeight.setTemperatureCelcius(20.0);
            sensorManager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(sensorListener, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
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
        double cnt = 0 ;
        double sum_m = 0 ;
        double cnt_m = 0 ;
        for (int i = 0; i < currentAvgMaxSet; ++i) {
            if (presureAverage[i] >  0 ) {
                sum += presureAverage[i];
                cnt++ ;
            } else {
                sum_m += presureAverage[i];
                cnt_m++ ;
            }


        }

        if (cnt > cnt_m) {
            return sum / cnt;
        } else {
            return sum_m / cnt_m;
        }
    }


}

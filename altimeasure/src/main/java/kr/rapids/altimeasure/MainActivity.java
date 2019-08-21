package kr.rapids.altimeasure;

import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,BLocationManager.DelegateFindLocation {
    private TextView mValue;
    private boolean mStarted;
    private double mAltitude;
    private double mOrientation;
    private double mX;
    private double mY;
    private double mZ;
    private double mStep;
    private int mFloor ;

    private AltitudeManager mAltiManager;
    private OrientationManager mOrientationManager ;
    private BLocationManager mBLocationManager;
    private StepManager mStepManager;
    private DirectionManager mDirectionManager ;

    private  boolean isStart = false ;
    private  boolean isTurn = false ;
    // Create the Handler
    private Handler handler = new Handler();
    private ArrayList<MeasureObj> mStartList = new ArrayList<>() ;
    private ArrayList<MeasureObj> mUpList = new ArrayList<>() ;
    private int cnt = 0 ;
    private int checkStartTimeOut = 30 ;
    private int checkRotationTimeOut = 30 ;
    private int checkUpTimeOut = 60 ;
    private TextView tvFloor;
    private TextView tvStart;
    private TextView tvStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAltiManager = new AltitudeManager(this);
        //mOrientationManager = new OrientationManager(this);
        mBLocationManager = new BLocationManager(this) ;
        mStepManager = new StepManager(this) ;
        mDirectionManager = new DirectionManager(this) ;

        if(!EasyPermissions.hasPermissions(this, Env.PERMISSIONS)){
            EasyPermissions.requestPermissions(this, "권한설정을 해주세요.", Env.REQ_CODE[3],
                    Env.PERMISSIONS);
        }else {
            //Gps start
            mBLocationManager.registerLocationUpdates();
            mBLocationManager.mDelegateFindLocation = this ;
        }

        mValue = findViewById(R.id.txt_alti);
        tvFloor = findViewById(R.id.txt_floor) ;
        tvStart = findViewById(R.id.txt_start) ;
        tvStep = findViewById(R.id.txt_step) ;


        findViewById(R.id.btn_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeasure(mStarted);
            }
        });
        findViewById(R.id.btn_remeasure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartMeasure();
            }
        });

        for (int i = 0 ; i < 600 ; i++ ) {
            mStartList.add(new MeasureObj()) ;
        }


// Start the Runnable immediately
        handler.post(runnable);


    }


    // Define the code block to be executed
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here

            if (mStarted)  {
                checkFloor();
            }
            // Repeat every 2 seconds
            handler.postDelayed(runnable, 100);
        }
    };


    public void checkFloor() {

            checkStart() ;
    }

    private  void checkStart() {

        if (!isStart ) {
            // 30초 check 클리어
            if (cnt >= checkStartTimeOut * 10) {
                mStartList.clear();
                for (int i = 0; i < 600; i++) {
                    mStartList.add(new MeasureObj());
                }
                cnt = 0;
            }
        } else {
            if (!isTurn ) { // 계단에서 회전 90도가 없으면
                // 30초 check 클리어
                if (cnt >= checkStartTimeOut * 10) {
                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;
                }
            } else {
                // 60초 check 클리어
                if (cnt >= checkUpTimeOut * 10) {
                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;

                }
            }
        }

        MeasureObj obj = mStartList.get(cnt) ;
        obj.altitude = mAltitude ;
        obj.orientation = mOrientation ;
        obj.step = mStep ;
        obj.x = mX ;
        obj.y = mY ;
        obj.z = mZ ;


        MeasureObj obj_b = mStartList.get(0) ;
        obj.altitudeGap =  obj.altitude -  obj_b.altitude ;
        obj.orientationGap =  Math.abs(obj.orientation -  obj_b.orientation) ;
        obj.stepGap =  Math.abs(obj.step -  obj_b.step) ;
        obj.xGap =  Math.abs(obj.x -  obj_b.x) ;
        obj.yGap =  Math.abs(obj.y -  obj_b.y) ;
        obj.zGap =  Math.abs(obj.z -  obj_b.z) ;

        // 고도 합계
        double gapAlitude =   0 ;
        gapAlitude = obj.altitude - obj_b.altitude ;

        // 스텝 합계
        double gapStep =   0 ;
        if (cnt > 50) {
            obj_b = mStartList.get(cnt - 50 ) ;
            obj.stepGap = Math.abs(obj.step - obj_b.step);
        }
        gapStep = obj.step - obj_b.step ;
        double step = gapStep ; // 초당 걸음수

        List<Double> list = Arrays.asList(obj.xGap,obj.yGap,obj.zGap ) ;
        Double mDir =  Collections.max(list) ;

        Log.v("kmj",String.format("count %d ,높이 :  %.2f , 걸음수 : %.2f , 방향 : %.2f",cnt,gapAlitude,step,mDir)) ;
        tvStep.setText(String.format("스텝 : %.2f" ,step));
        mValue.setText(String.format("높이 : %.2fm \n 방향  %.2f " , gapAlitude ,mDir)  );

        // 스타트 상태이면
        if (isStart) {
            if (mDir > 180) {
                isTurn = true ;
            } else {
                isTurn = false ;
            }

            if (isTurn) { // 회전이 감지 되면
                if (mDir >  320) {  // 360 한계단 회전
                    if ( step > 0 ) { // 걷기중이면
                        if (gapAlitude > 0 ) {
                            mFloor++ ;
                        } else {
                            mFloor-- ;
                        }
                        tvFloor.setText(String.format("%d",mFloor));
                        clearMeasur();
                        return ;
                    }

                }
            } else {
                if (Math.abs(gapAlitude) > 3.0) { // 회전이 없으면 3미터 이상이면 한계단으로 간주
                    if ( cnt > 30 ) {  // 3초이상 일경우만  엘리베이터 체크
                        if (step > 0) { // 걷기중이면
                            if (gapAlitude > 0) {
                                mFloor++;
                            } else {
                                mFloor--;
                            }
                            tvFloor.setText(String.format("%d", mFloor));
                            clearMeasur();
                            return;
                        }
                    }
                }
            }

        } else { // 스타트상태가 아니면 체크 계단걷기 시작한건지 체크
            // 1m 이상 고도 변화가 있으면
            if (Math.abs(gapAlitude) > 1.0 ) {
                if ( step > 0 ) { // 걷기중이면
                    isStart = true ;  // 계단오르기 시작
                    tvStart.setText("Start");
                    tvStart.setTextColor(Color.RED);

                    mStartList.clear();
                    for (int i = 0; i < 600; i++) {
                        mStartList.add(new MeasureObj());
                    }
                    cnt = 0;
                    mStartList.get(0).x = mX  ;
                    mStartList.get(0).y = mY  ;
                    mStartList.get(0).z = mZ  ;
                    mStartList.get(0).altitude = mAltitude ;

                }
            }

        }

        cnt++ ;



    }

    private  void clearMeasur() {
        mStartList.clear();
        for (int i = 0; i < 600; i++) {
            mStartList.add(new MeasureObj());
        }
        cnt = 0;

    }

    private  void initMeasure() {

        tvStart.setText("Start");
        tvStart.setTextColor(Color.RED);

        tvFloor.setText("0");
        mFloor = 0 ;
        mStep = 0 ;
        mX = 0 ;
        mY = 0 ;
        mZ = 0 ;

        mStartList.clear();
        for (int i = 0; i < 600; i++) {
            mStartList.add(new MeasureObj());
        }
        cnt = 0;

        isStart = false ;
        isTurn = false ;


    }

    private void startMeasure(boolean started){

       if(mStarted){
            mAltiManager.stopMeasure();
            mDirectionManager.stopMeasure();
            mStepManager.stopMeasure();
        }else{

           initMeasure();
           mAltiManager.startMeasure();
           mDirectionManager.startMeasure();
           mStepManager.startMeasure();
           mValue.setText("wait...");
        }
        mStarted = !started;
        Button btn = findViewById(R.id.btn_measure);
        btn.setText(mStarted ? "중지" : "시작");
    }

    private void restartMeasure(){
        if(!mStarted){
            Toast.makeText(this, "측정이 시작되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }else{
            mAltiManager.restartMeasure();
            mValue.setText("wait...");
        }
    }

    public void setCurrentAltitude(double altitude){
        mAltitude = altitude;
        displayAlti();
    }

    public void setCurrentOrientation(double val  ){
        mOrientation = val;
        displayAlti();
    }

    public void setCurrentOrientation2(double x ,double y, double z  ){
        mX = x;
        mY = y;
        mZ = z;
        //displayAlti();
    }

    public void setCurrentStep(double val  ){
        mStep += val;
        displayAlti();
    }

    private void displayAlti(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //Gps start

        mBLocationManager.registerLocationUpdates();
        mBLocationManager.mDelegateFindLocation = this ;

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        finish();
        /*// (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }*/
    }


    @Override
    public void findLocation(Location loc, Address addr ) {

        TextView addrTextView = (TextView) findViewById(R.id.txt_addr) ;
        addrTextView.setText(addr.getAddressLine(0));

        TextView gpsTextView = (TextView) findViewById(R.id.txt_gps) ;
        gpsTextView.setText(String.format("%f , %f" , addr.getLatitude(), addr.getLongitude()));

        Log.v("kmj",addr.toString()) ;

    }


}

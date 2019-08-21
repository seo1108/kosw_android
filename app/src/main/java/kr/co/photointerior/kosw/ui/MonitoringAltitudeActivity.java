package kr.co.photointerior.kosw.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.model.KsBeacon;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.service.beacon.PresureHeight;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LibUtils;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 고도 모니터링을 위한 뷰
 */
public class MonitoringAltitudeActivity extends BaseActivity implements View.OnClickListener{
    private BroadcastReceiver mRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Env.Action.BEACON_MONITORING_ALTITUDE_ACTION.isMatch(intent.getAction())){
                //updateInfo(intent);
            }
        }
    };
    private BeaconRagingInRegionService mBeaconService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BeaconRagingInRegionService.BeaconBinder binder = ((BeaconRagingInRegionService.BeaconBinder)iBinder);
            mBeaconService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private boolean mStarted;
    private TextView mTxtFloor;
    private TextView mTxtAltitude;
    private TextView mTxtAltitudePrev;
    private TextView mTxtCurrentBeacon;
    private TextView mTxtUpdown;

    private Timer mTimer;
    private boolean mConnectionUnbinded;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_altitude);
        findViews();
        attachEvents();
        setInitialData();
        registerReceiver(mRec, new IntentFilter(Env.Action.BEACON_MONITORING_ALTITUDE_ACTION.action()));
    }

    private void startMeasure(){
        bindService(new Intent(this, BeaconRagingInRegionService.class), mConnection, Context.BIND_AUTO_CREATE);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setInitialData();
            }
        }, 2000, 1000);
    }

    private void stopMeasure(){
        unbindService(mConnection);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtAltitude.setText("0.0m");
                mTxtAltitudePrev.setText("0.0m");
                mTxtFloor.setText("0층");
            }
        });
        mTimer.cancel();
    }

    @Override
    protected void findViews() {
        mTxtAltitude = getView(R.id.txt_current_altitude);
        mTxtAltitudePrev = getView(R.id.txt_prev_altitude);

        mTxtFloor = getView(R.id.txt_current_floor);
        mTxtFloor.setVisibility(View.GONE);
        mTxtCurrentBeacon = getView(R.id.txt_current_beacon);
        mTxtUpdown = getView(R.id.txt_current_updownmsg);
        getEditText(R.id.input_rate).setText(KUtil.getStringPref(PrefKey.FLOOR_GAP_RATE, "0.3333"));
    }

    @Override
    protected void attachEvents() {
        getView(R.id.btn_back).setOnClickListener(this);
        getView(R.id.btn_start_stop).setOnClickListener(this);
        getView(R.id.btn_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if( id == R.id.btn_back ){
            finish();
        }else if(id == R.id.btn_save){
            saveRate();
        }else if(id == R.id.btn_start_stop){
            startOrStop();
        }
    }

    private void saveRate(){
        String rate = getEditText(R.id.input_rate).getText().toString();
        if(StringUtil.isEmptyOrWhiteSpace(rate)){
            toast("비율을 입력하세요.");
            return;
        }
        KUtil.saveStringPref(PrefKey.FLOOR_GAP_RATE, rate);
    }

    private void startOrStop(){
        mStarted = !mStarted;
        Button btn = getView(R.id.btn_start_stop);
        btn.setText(mStarted ? "STOP" : "START");
        if(mStarted){
            startMeasure();
        }else{
            stopMeasure();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mRec);
        try {
            unbindService(mConnection);
        }catch(IllegalArgumentException e){

        }
        if(mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void setInitialData() {
        if(mBeaconService != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*mTxtFloor.setText("Flr. ".concat(String.valueOf(LibUtils.format(mBeaconService.getPrevMinorValue(), "#,##0"))));
                    mTxtAltitudePrev.setText("Alt. ".concat(LibUtils.format(mBeaconService.getCurrentAltitude(), "#,##0.00")));
                    mTxtAltitude.setText("Gap. ".concat(LibUtils.format(mBeaconService.getDummyAlti(), "#,##0.0")));
                    KsBeacon kb = mBeaconService.getCurrentMatchedBeacon();
                    if(kb != null){
                        mTxtCurrentBeacon.setText(kb.string());
                    }*/
                    mTxtUpdown.setText( mBeaconService.getInfoTxt());
                }
            });
        }
    }
}

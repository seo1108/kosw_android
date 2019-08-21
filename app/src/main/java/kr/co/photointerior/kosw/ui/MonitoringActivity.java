package kr.co.photointerior.kosw.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.model.KsBeacon;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 비콘 신호 모니터링을 위한 뷰
 */
public class MonitoringActivity extends BaseActivity implements View.OnClickListener{
    private String TAG = LogUtils.makeLogTag(MonitoringActivity.class);
    private BroadcastReceiver mRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.err(TAG, "action=" + intent.getAction());
            if(Env.Action.BEACON_MONITORING_ACTION.isMatch(intent.getAction())){
                updateInfo(intent);
            }
        }
    };
    private TextView mResult;
    private EditText mMajor;
    private EditText mMinor;
    private String mValueMajor;
    private String mValueMinor;
    private TextView mScanTarget;
    private TextView mScanUuid;
    private ScrollView mScroll;
    private boolean mEndLess;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        findViews();
        attachEvents();
        setInitialData();
        registerReceiver(mRec, new IntentFilter(Env.Action.BEACON_MONITORING_ACTION.action()));
    }

    @Override
    protected void findViews() {
        mResult = getView(R.id.scan_result);
        mMajor = getView(R.id.input_major);
        mMinor = getView(R.id.input_minor);
        mScanTarget = getView(R.id.scan_taret);
        mScanUuid = getView(R.id.target_uuid);
        mScroll = getView(R.id.scrollView);
    }

    @Override
    protected void attachEvents() {
        getView(R.id.btn_set).setOnClickListener(this);
        getView(R.id.btn_clear).setOnClickListener(this);
        getView(R.id.btn_clear_result).setOnClickListener(this);
        getView(R.id.btn_endless).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if( id == R.id.btn_set ){
            mValueMajor = mMajor.getText().toString();
            mValueMinor = mMinor.getText().toString();
        }else if(id == R.id.btn_clear) {
            mMinor.setText("");
            mMinor.setText("");
            mValueMajor = null;
            mValueMinor = null;
        }else if(id == R.id.btn_endless){
            mEndLess = !mEndLess;
            ((TextView)getView(R.id.btn_endless)).setText(mEndLess ? "ENDLESS" : "REPEAT");
        }else{
            mResult.setText("");
            mScanTarget.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mRec);
        super.onDestroy();
    }

    @Override
    protected void setInitialData() {

    }

    private void updateInfo(Intent intent){
        String major = mMajor.getText().toString();
        String minor = mMinor.getText().toString();
        major = StringUtil.isEmptyOrWhiteSpace(major) ? "0" : major;
        minor = StringUtil.isEmptyOrWhiteSpace(minor) ? "0" : minor;

        LogUtils.err("TEST", "input major/minor=" + major + "/" + minor);
        Bundle b = intent.getExtras();
        List<KsBeacon> list = (ArrayList<KsBeacon>)b.getSerializable("_KS_BEACON_");
        if(list != null) {
            if(list.size() > 0){
                mScanUuid.setText(list.get(0).getUuid());
            }
            StringBuilder sb = new StringBuilder();
            if(mEndLess){
                sb.append(mResult.getText().toString());
            }
            for (KsBeacon kb : list) {
                if (kb.getMajor().equals(major) && kb.getMinor().equals(minor)) {
                    mScanTarget.setText("major=".concat(kb.getMajor()).concat(", minor=").concat(kb.getMinor())
                                    .concat(" , rssi=").concat(String.valueOf(kb.getRssi())));
                }
                sb.append("major=" + kb.getMajor() + ", minor=" + kb.getMinor() +
                        " , rssi=" + kb.getRssi()).append("\n");
            }
            mResult.setText(sb.toString());
            mScroll.post(new Runnable() {
                @Override
                public void run() {
                    mScroll.fullScroll(View.FOCUS_DOWN);
                }
            });

        }
    }
}

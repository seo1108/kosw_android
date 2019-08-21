package kr.co.photointerior.kosw.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;

public class DummyActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(getBaseContext(), BeaconRagingInRegionService.class));
        new Handler().postDelayed(()->{
            finish();
        }, 3000);
    }
}

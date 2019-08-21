package kr.rapids.beaconmonitoring;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class MonitoringApp extends Application implements BootstrapNotifier{
    private String TAG = getClass().getSimpleName();
    private RegionBootstrap regionBootstrap;
    //private BackgroundPowerSaver backgroundPowerSaver;
    private boolean enterRegion;
    @Override
    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        beaconManager.setBackgroundMode(true);

        Region region =
                new Region("ppp-backgroundRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        beaconManager.setForegroundScanPeriod(1000L);
        beaconManager.setForegroundBetweenScanPeriod(100L);
        beaconManager.setBackgroundScanPeriod(1000L);
        beaconManager.setBackgroundBetweenScanPeriod(100L);
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    public RegionBootstrap startBeaconMonitoring() {
        if (regionBootstrap == null) {
            Region region = new Region("ppp-backgroundRegion", null, null, null);
            regionBootstrap = new RegionBootstrap(this, region);
        }
        return regionBootstrap;
    }

    public RegionBootstrap stopBeaconMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap.disable();
        }
        return regionBootstrap;
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.e(TAG, "region enter");
//        if(!enterRegion){
        regionBootstrap.disable();
        //Toast.makeText(this, "enter", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        enterRegion = true;
//        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.e(TAG, "region exit");
        enterRegion = false;
        //regionBootstrap.disable();
        Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}

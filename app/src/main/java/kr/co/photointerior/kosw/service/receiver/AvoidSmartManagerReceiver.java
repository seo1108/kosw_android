package kr.co.photointerior.kosw.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import java.util.Random;

import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.ui.AvoidSmartManagerActivity;

public class AvoidSmartManagerReceiver extends BroadcastReceiver {
    private static final String SMART_MANAGER_PACKAGE_NAME = "com.samsung.android.sm";

    public void onReceive(final Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) ||
                "android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction()) ||
                "android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent.getAction())) {
            boolean isSmartManagerExist = false;
            try {
                //context.getPackageManager().getPackageInfo(SMART_MANAGER_PACKAGE_NAME, 128);
                context.getPackageManager().getPackageInfo(SMART_MANAGER_PACKAGE_NAME, PackageManager.GET_META_DATA);
                isSmartManagerExist = true;
            } catch (NameNotFoundException e) {}
            if (isSmartManagerExist) {
//                BeaconRagingInRegionService.getServiceIsntance().sendServiceLog("avoid smart manager:by " + intent.getAction());
                new Handler().postDelayed(()-> {
                    Intent serviceIntent = new Intent(context, AvoidSmartManagerActivity.class);
                    serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(serviceIntent);
                }, (long) new Random().nextInt(3000));
            }
        }
    }
}

package kr.co.photointerior.kosw.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;

/**
 * 기기 부팅시 비콘 서비스 시작
 * @deprecated 2018.07.18 비콘 스캔 제한시간으로 인해 사용하지 않게 됨.
 */
public class BootStartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                Env.Action.KOSW_SERVICE_RESTART_ACTION.isMatch(action)){
//            BeaconRagingInRegionService.getServiceIsntance().sendServiceLog("restart beacon service:by " + action);
//            context.startService(new Intent(context, BeaconRagingInRegionService.class));
        }
    }
}

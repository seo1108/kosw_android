package kr.co.photointerior.kosw.service.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.Build;

import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.utils.AUtil;

public abstract class BaseService extends Service {


    protected void destroyMainActivity(){
        String topActivity = AUtil.getTopActivity(this);

        if (!"kr.co.photointerior.kosw.ui.MainActivity".equals(topActivity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0 이상이면
                /*startService(new Intent(this, BeaconScanResumeService.class));
                stopSelf();*/
                //Intent dialogIntent = new Intent(this, MainActivity.class);
                Intent inten = new Intent(this, MainActivity.class);
                inten.putExtra("_FROM_", "SERVICE");
                inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(inten);
            }
        }
    }

    /**
     * 현재 최상위 액티비티가 계단왕 Main activity인가 검사.
     * @return
     */
    protected boolean isTopActivityIsMainActivity(){
        String topActivity = AUtil.getTopActivity(this);
        return "kr.co.photointerior.kosw.ui.MainActivity".equals(topActivity);
    }

    public abstract void stopServices();
}
